package cn.edu.xmu.order.service;

import cn.edu.xmu.goodsservice.client.IGoodsService;
import cn.edu.xmu.goodsservice.model.vo.OrderItemVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.dao.OrderDao;
import cn.edu.xmu.order.discount.BaseCouponDiscount;
import cn.edu.xmu.order.discount.Computable;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.bo.OrderItem;
import cn.edu.xmu.order.model.vo.OrderFreightSnVo;
import cn.edu.xmu.order.model.vo.OrderMessageVo;
import cn.edu.xmu.order.model.vo.OrderSimpleVo;
import cn.edu.xmu.order.model.vo.OrderVo;
import cn.edu.xmu.order.util.SnowFlake;
import cn.edu.xmu.provider.server.OtherForOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import model.GoodsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.FreightForOtherService;
//import service.FreightForOtherService;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务类
 *
 * @author jwy
 * @date Created in 2020/11/4 11:48
 * Modified in 2020/12/3 12:16
 **/
@DubboService
@Service
public class OrderService {

    private Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderDao orderDao;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Value("${orderservice.order-pay-topic.delay-level}")
    private int delayLevel;

    @Value("${orderservice.order-pay-topic.timeout}")
    private long timeout;

    @DubboReference(check = false)
    private FreightForOtherService freightForOtherService;

    @DubboReference(check = false)
    private IGoodsService goodsService;

    @DubboReference(check=false)
    private OtherForOrderService otherForOrderService;

    private SnowFlake snowFlake=new SnowFlake(1,1);

//    @DubboReference(version = "0.0.1")
//    private GoodsService goodsService;
//
    private Computable computable;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    public Long flashSellByLuaScript(String skuCode,int num) {
        //lua脚本语法
        StringBuilder script = new StringBuilder();
        script.append(" local buyNum = ARGV[1]");//参数
        script.append(" local goodsKey = KEYS[1] ");//key键
        script.append(" local goodsNum = redis.call('get',goodsKey) "); // 调用方式，必须是redis.call 或者pcall结合redis里的方法
        script.append(" if goodsNum >= buyNum  ");
        script.append(" then redis.call('decrby',goodsKey,buyNum)   ");
        script.append(" return buyNum   ");
        script.append(" else    ");
        script.append(" return '0'  ");
        script.append(" end");

        DefaultRedisScript<String> longDefaultRedisScript = new DefaultRedisScript<>(script.toString(), String.class);
        String result = stringRedisTemplate.execute(longDefaultRedisScript, Collections.singletonList(skuCode), String.valueOf(num));
        return Long.valueOf(result);
    }

    /**
     * 新建订单
     * @author jwy
     * @param userId 用户id
     * @param order 订单bo
     * @return ReturnObject<VoObject> 订单返回视图
     * createdBy jwy 2020/11/04 13:57
     * modifiedBy jwy 2020/11/7 19:20
     */
    @Transactional
    public ReturnObject<String> insertOrder(Long userId, Order order) throws JsonProcessingException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException, ClassNotFoundException {
        ReturnObject<String> retOrder = null;
        //校验regionId是否合法

        //订单类型，0普通，1团购，2预售
        //对于团购订单要去校验订单中的商品是否全部是团购商品
        if(order.getGrouponId()!=null&&order.getGrouponId()!=0) {
            order.setOrderType(Order.OrderType.getTypeByCode(1));
            List<Long> grouponGoodsList = goodsService.getSkuListByGrouponId(order.getGrouponId());
            //要是团购使用了优惠卷，则报错
            if(order.getCouponId()!=null&&order.getCouponId()!=0)
                return new ReturnObject<>();
            for(OrderItem orderItem:order.getOrderItemList())
            {
                if(!grouponGoodsList.contains(orderItem.getGoodsSkuId()))
                return new ReturnObject<>();
            }

        }

        //对于预售订单
        //对于预售订单要去校验订单中的商品是否全部是预售商品
        if(order.getGrouponId()!=null&&order.getGrouponId()!=0) {
            order.setOrderType(Order.OrderType.getTypeByCode(2));
            Long presaleGoodsList = goodsService.getSkuByPresaleId(order.getPresaleId());
            for(OrderItem orderItem:order.getOrderItemList())
            {
                if(!presaleGoodsList.equals(orderItem.getGoodsSkuId()))
                return new ReturnObject<>();
            }

        }
        //不能同时存在团购和预售
        if(order.getGrouponId()!=null&&order.getPresaleId()!=null&&order.getGrouponId()!=0&&order.getPresaleId()!=0)
            return new ReturnObject<>();
        else
            order.setOrderType(Order.OrderType.getTypeByCode(0));

        //然后各种计算价格,校验各种id是否存在
        String json=null;

        //先把所有没有couponActId的捞出来
        List<OrderItem> nonActOrderItemList=new ArrayList<>();
        int size=order.getOrderItemList().size();
        for(int i=0;i<size;i++)
        {
            if(order.getOrderItemList().get(i).getCouponActivityId()==null) {
                nonActOrderItemList.add(order.getOrderItemList().get(i));
                order.getOrderItemList().remove(i);
                size--;
                i--;
            }
        }
        Long discountPrice=0l;
        //计算优惠方法1,按照couponActId分类计算
        Map<Long, List<OrderItem>> collect = order.getOrderItemList().stream().collect(Collectors.groupingBy(OrderItem::getCouponActivityId));
        order.getOrderItemList().clear();
        try {
            for (Long key : collect.keySet()) {
                json=goodsService.getStrategyByActivityId(key,order.getCouponId());
                BaseCouponDiscount bc = BaseCouponDiscount.getInstance(json);
                order.getOrderItemList().addAll(bc.compute(collect.get(key)));
            }
        }catch (Exception e)
        {
            logger.info(e.getMessage());
            return new ReturnObject<>(ResponseCode.COUPONACT_STATENOTALLOW);
        }

        for(OrderItem orderItem:order.getOrderItemList()) {
            if (orderItem.getDiscount() != null)
                discountPrice += orderItem.getDiscount();
        }
        //清除
        order.getOrderItemList().clear();

        //再把东西加回去
        order.getOrderItemList().addAll(nonActOrderItemList);
        order.setDiscountPrice(discountPrice);

        //        List<OrderItem> caculateOrderItem=new ArrayList<>();
//
//        //计算优惠的方法2,对于每一个有couponactid的orderitem都去计算一次
//        for(OrderItem orderItem:order.getOrderItemList()) {
//            List<OrderItem> orderItems = new ArrayList<>();
//            orderItems.add(orderItem);
//            json = goodsService.getStrategyByActivityId(orderItem.getCouponActivityId(), order.getCouponId());
//            if(json==null)
//            {
//                orderItem.setCouponActivityId(null);
//                caculateOrderItem.add(orderItem);
//            }
//            else {
//                BaseCouponDiscount bc = BaseCouponDiscount.getInstance(json);
//                caculateOrderItem.addAll(bc.compute(orderItems));
//            }
//        }
        //获取运费
        List<GoodsVo> voList=new ArrayList<>(order.getOrderItemList().size());
        for(OrderItem orderItem:order.getOrderItemList())
        {
            GoodsVo vo=new GoodsVo();
            vo.setSkuId(orderItem.getGoodsSkuId());
            vo.setCount(orderItem.getQuantity());
            voList.add(vo);
        }
        Long freightPrice;
        Long shopId;
        shopId=goodsService.getShopBySkuId(order.getOrderItemList().get(0).getGoodsSkuId()).getId();
        if(shopId!=null)
            order.setShopId(shopId);
        ReturnObject<Long> returnObject=freightForOtherService.caculateFreight(voList,order.getRegionId(),order.getShopId());
        if(returnObject.getCode()!=ResponseCode.OK)
            return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
        else
        freightPrice=returnObject.getData();
        logger.debug(freightPrice.toString());
        order.setFreightPrice(freightPrice);

        List<OrderItemVo> orderItemVos=new ArrayList<>();
        //最后去扣库存
        for(OrderItem orderItem:order.getOrderItemList()) {
            String key = "fg_" +orderItem.getGoodsSkuId();
            //判断是否在秒杀商品列表中,如果是秒杀商品，在这边扣库存
            if(redisTemplate.opsForValue().get(key)!=null)
            {
                Long num=flashSellByLuaScript(key, orderItem.getQuantity());
                if(num==0) {
                    retOrder = new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
                    return retOrder;
                }
            }
            //非秒杀商品,去商品模块减库存
            //如果是普通或者团购,加到库存列表里面
            else if(order.getOrderType().getCode().intValue()!=2)
            {
                OrderItemVo orderItemVo=new OrderItemVo();
                orderItemVo.setSkuId(orderItem.getGoodsSkuId());
                orderItemVo.setQuantity(orderItem.getQuantity());
                orderItemVos.add(orderItemVo);
            }
            //如果是预售订单，扣除预售的库存量
            else if(order.getOrderType().getCode().intValue()==2)
            {
                Boolean deductSuccess=goodsService.deductPresaleStock(order.getPresaleId(),orderItem.getQuantity());
                if(deductSuccess==false)
                    return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);
            }
        }

        //整体扣除普通商品的库存
        Boolean deductSuccess=goodsService.deductSkuStock(orderItemVos);
        if(deductSuccess==false)
            return new ReturnObject<>(ResponseCode.SKU_NOTENOUGH);

        //生成orderSn
        String OrderSn=null;
        OrderSn=String.valueOf(snowFlake.nextId());
        order.setOrderSn(OrderSn);

        //order.setOrderSn(Common2.randomUUID());
        //最后用mq插入数据库
        sendOrderInsertMessage(order);
        //orderDao.insertOrder(order);
        //sendOrderInsertMessage(order);
        //sendOrderPayMessage(order.getOrderSn());
        //ReturnObject<Order> retObj = orderDao.insertOrder(order);

        //发送延时消息检查订单是否付款
        sendOrderPayMessage(order.getOrderSn());

        return new ReturnObject<>(order.getOrderSn());
//        if (retObj.getCode().equals(ResponseCode.OK)) {
//            retOrder = new ReturnObject<>(retObj.getData());
//        } else {
//            retOrder = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
//        }
//        return retOrder;
    }

    public void sendInsertMessage(Order order)
    {
        String json= JacksonUtil.toJson(order);
        Message message= MessageBuilder.withPayload(json).build();
        logger.info("send insert order Message: message="+message);
        rocketMQTemplate.sendOneWay("order-topic:1",message);
    }

    public void sendOrderInsertMessage(Order order){
        String json= JacksonUtil.toJson(order);
        //logger.info("send insert order Message b: message="+json);
        rocketMQTemplate.asyncSend("order-insert-topic:1", MessageBuilder.withPayload(json).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                //logger.info("sendOrderInsertMessage: onSuccess result = "+ sendResult+" time ="+LocalDateTime.now());
            }

            @Override
            public void onException(Throwable throwable) {
                logger.info("sendOrderPayMessage: onException e = "+ throwable.getMessage()+" time ="+LocalDateTime.now());
            }
        });
    }

    //rocketmq实现30分钟后未支付的订单自动取消
    public void sendOrderPayMessage(String orderSn){
        logger.info("sendOrderPayMessage: send message orderSn = "+orderSn+" delay ="+delayLevel+" time =" +LocalDateTime.now());
        rocketMQTemplate.asyncSend("order-pay-topic", MessageBuilder.withPayload(orderSn).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.info("sendOrderPayMessage: onSuccess result = "+ sendResult+" time ="+LocalDateTime.now());
            }

            @Override
            public void onException(Throwable throwable) {
                logger.info("sendOrderPayMessage: onException e = "+ throwable.getMessage()+" time ="+LocalDateTime.now());
            }
        }, timeout * 1000, delayLevel);
    }

    @Transactional
    public Boolean insertOrderToDataBase(Order order)
    {
        orderDao.insertOrder(order);
        return true;
    }

    /**
     * 买家查询名下订单 (概要)
     * @author jwy
     * @param orderSn
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return ReturnObject<PageInfo<VoObject>> 订单概要返回视图
     * createdBy jwy 2020/11/28 22:57
     * modifiedBy jwy
     */
    @Transactional
    public ReturnObject<PageInfo<VoObject>> findSimpleOrderSelective(Long userId,String orderSn, Integer state, String beginTime, String endTime, Integer page, Integer pageSize) {
        return orderDao.findSimpleOrderSelective(userId,orderSn,state,beginTime,endTime,page,pageSize);
    }

    /**
     * 买家查询单个订单
     *
     * @author jwy
     * @param userId 用户id
     * @param id 订单id
     * @return ReturnObject<Role> 新增结果
     * createdBy
     * modifiedBy  2020/11/29 19:20
     */
    @Transactional
    public ReturnObject<VoObject> findOrderById(Long userId, Long id) {
        ReturnObject<Order> returnObject;
        returnObject=orderDao.findOrderById(userId,id);
        if(returnObject.getCode()==ResponseCode.OK) {
//            returnObject.getData().setShopVo(goodsService.getShopByshopId(returnObject.getData().getShopId()));
//            returnObject.getData().setCustomerVo(otherForOrderService.getUserByuserId(userId));
            return new ReturnObject<>(returnObject.getData());
        }
        else
            return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
    }


    /**
     * 店家查询商户所有订单 (概要)
     * @author cyx
     * @param shopId
     * @param customerId
     * @param orderSn
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return ReturnObject<PageInfo<VoObject>> 订单概要返回视图
     * createdBy cyx 2020/11/30 8:59
     * modifiedBy
     */
    @Transactional
    public ReturnObject<PageInfo<VoObject>> shopsShopIdOrdersGet(Long shopId, Long customerId, String orderSn, String beginTime, String endTime, Integer page, Integer pageSize) {
        return orderDao.shopsShopIdOrdersGet(shopId, customerId, orderSn, beginTime, endTime, page, pageSize);
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）
     * @author cyx
     * @param shopId
     * @param id
     * @return ReturnObject<VoObject> 订单返回视图
     * createdBy cyx 2020/11/30 9:41
     * modifiedBy
     */
    @Transactional
    public ReturnObject<VoObject> shopFindOrderById(Long shopId, Long id) {
        ReturnObject<Order> returnObject;
        returnObject=orderDao.shopFindOrderById(shopId,id);
        if(returnObject.getCode()==ResponseCode.OK) {
//            returnObject.getData().setShopVo(goodsService.getShopByshopId(returnObject.getData().getShopId()));
//            returnObject.getData().setCustomerVo(otherForOrderService.getUserByuserId(returnObject.getData().getCustomerId()));
            return new ReturnObject<>(returnObject.getData());
        }
        else
            return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
    }

    /**
     * 买家修改订单
     * @author jwy
     * @param id 订单id
     * @param vo 订单修改视图
     * @return ReturnObject 返回对象
     * createdBy jwy 2020/11/30 20:40
     */
    @Transactional
    public ReturnObject<Object> modifyUserOrder(Long id, OrderSimpleVo vo,Long userId) {
        return orderDao.modifyUserOrderByVo(id,vo,userId);
    }

    /**
     * 买家标记确认收货
     * @author jwy
     * @param userId
     * @param id 订单id
     * @return ReturnObject 返回对象
     * createdBy jwy 2020/12/01 20:40
     */
    @Transactional
    public ReturnObject confirmUserOrder(Long userId, Long id) {
        return orderDao.confirmUserOrderById(userId,id);
    }

    /**
     * 店家修改订单（留言）
     * @author cyx
     * @param shopId 店铺id
     * @param id 订单id
     * @param vo 订单修改视图
     * @return ReturnObject 返回对象
     * createdBy cyx 2020/12/1 15:39
     */
    @Transactional
    public ReturnObject shopModifyUserOrder(Long shopId, Long id, OrderMessageVo vo) {
        return orderDao.shopModifyUserOrderByVo(shopId,id,vo);
    }

    /**
     * 买家取消删除订单
     * @author jwy
     * @param userId
     * @param id 订单id
     * @return ReturnObject 返回对象
     * createdBy jwy 2020/12/01 20:40
     */
    @Transactional
    public ReturnObject cancelDeleteOrder(Long userId, Long id) {
        return orderDao.cancelDeleteOrderById(userId,id);
    }

    /**
     * 买家将团购订单转为普通订单
     * @author jwy
     * @param userId
     * @param id 订单id
     * @return ReturnObject 返回对象
     * createdBy jwy 2020/12/01 20:40
     */
    @Transactional
    public ReturnObject changeToNormalOrder(Long userId, Long id) {
        return orderDao.changeToNormalOrder(userId,id);
    }
    /**
     * 店家对订单标记发货
     * @author cyx
     * @param shopId 店铺id
     * @param id 订单id
     * @param vo 订单修改视图
     * @return ReturnObject 返回对象
     * createdBy cyx 2020/12/1 19:58
     */
    @Transactional
    public ReturnObject postFreights(Long shopId, Long id, OrderFreightSnVo vo) {
        return orderDao.postFreights(shopId,id,vo);
    }

    /**
     * 管理员取消本店铺订单
     * @author cyx
     * @param shopId 店铺id
     * @param id 订单id
     * @return ReturnObject 返回对象
     * createdBy cyx 2020/12/1 21:18
     */
    @Transactional
    public ReturnObject shopsShopIdOrdersIdDelete(Long shopId, Long id) {
        return orderDao.shopsShopIdOrdersIdDelete(shopId,id);
    }
}
