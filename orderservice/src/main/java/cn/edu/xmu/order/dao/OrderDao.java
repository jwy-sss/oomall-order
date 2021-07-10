package cn.edu.xmu.order.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.order.mapper.OrderPoMapper;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderItemPoExample;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.po.OrderPoExample;
import cn.edu.xmu.order.model.vo.OrderFreightSnVo;
import cn.edu.xmu.order.model.vo.OrderMessageVo;
import cn.edu.xmu.order.model.vo.OrderSimpleVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单DAO
 * @author jwy
 **/
@Repository
public class OrderDao {

    private  static  final Logger logger = LoggerFactory.getLogger(OrderDao.class);

    @Autowired
    private OrderPoMapper orderPoMapper;

    @Autowired
    private OrderItemPoMapper orderItemPoMapper;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 新增订单,级联增加订单明细
     *
     * @author jwy
     * @param order 订单bo
     * @return ReturnObject<Role> 新增结果
     * createdBy
     * modifiedBy  2020/11/7 19:20
     */
    public void insertOrder(Order order) {
        //先由bo创建po
        OrderPo orderPo = order.gotOrderPo();

        List<OrderItemPo> orderItemPoList = order.gotOrderItemPo();

        ReturnObject<Order> retObj = null;
        try {
            int ret = orderPoMapper.insertSelective(orderPo);
            //同步插入orderitem表,同时更新库存
            for (OrderItemPo orderItemPo : orderItemPoList) {
                logger.info(orderItemPo.getQuantity().toString());
                orderItemPo.setOrderId((long) ret);
//                //根据orderitem中的数据减库存
//                GoodsSkuPoExample example = new GoodsSkuPoExample();
//                GoodsSkuPoExample.Criteria criteria = example.createCriteria();
//                criteria.andGoodsSpuIdEqualTo(orderItemPo.getGoodsSkuId());
//                List<GoodsSkuPo> goodsSkuPos = goodsSkuPoMapper.selectByExample(example);
//                for (GoodsSkuPo po:goodsSkuPos) {
//                    if(po.getInventory()- orderItemPo.getQuantity()>=0) {
//                        po.setInventory(po.getInventory() - orderItemPo.getQuantity());
//                        goodsSkuPoMapper.updateByPrimaryKeySelective(po);
//                    }
//                    //库存不足
//                    else
//                    {
//                        retObj = new ReturnObject<>(ResponseCode.SKU_NOTENOUGH, String.format("新增失败：" + orderPo.getId()));
//                        return retObj;
//                    }
//                }

                orderItemPoMapper.insertSelective(orderItemPo);
            }
//            if (ret == 0) {
//                //插入失败
//                logger.debug("insertOrder: insert order fail " + orderPo.toString());
//                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + orderPo.getId()));
//            } else {
//                //插入成功
//                logger.debug("insertOrder: insert order = " + orderPo.toString());
//                order.setId(orderPo.getId());
//                retObj = new ReturnObject<>(order);
//            }
//        }
        }
        catch (DataAccessException e) {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
//        return retObj;
        }

    /*
     * 判断订单是否已完成支付，如果未完成支付就取消订单
     */
    public ReturnObject<Object> JudgePaied(String orderSn){
        OrderPoExample example=new OrderPoExample();
        OrderPoExample.Criteria criteria=example.createCriteria();
        criteria.andOrderSnEqualTo(orderSn);
        List<OrderPo> orderPos = orderPoMapper.selectByExample(example);

        for(OrderPo po:orderPos){
            //如果30min后还是新订单状态，即30min内没有付款，就取消订单
            if(po.getState().intValue()==Order.State.ARRIED.getCode()||
                    (po.getSubstate().intValue()==Order.SubState.RESTPAING.getCode()&&
                            po.getOrderType().intValue()==Order.OrderType.PRESALE.getCode()))
            {
                logger.info("30min内订单已付款 orderSn="+orderSn);
                return new ReturnObject<>();
            }

            po.setState(Order.State.CANCELED.getCode().byteValue());
            int ret;
            try{
                ret=orderPoMapper.updateByPrimaryKeySelective(po);
            }
            catch (DataAccessException e) {
                logger.error("updateOrder: DataAccessException:" + e.getMessage());
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            } catch (Exception e) {
                logger.error("other exception:" + e.getMessage());
                e.printStackTrace();
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
            }
            //检查更新是否成功
            if(ret==0)
            {
                logger.info("订单id不存在或已被删除");
            }
            else
            {
                logger.info("订单"+po.getOrderSn()+"取消成功！");
            }
        }
        return new ReturnObject<>();
    }

    /**
     * 查询订单概要
     *
     * @author jwy
     * @param orderSn 订单编号
     * @param state 订单状态
     * @param beginTime 时间下限
     * @param endTime 时间上限
     * @param page 分页
     * @param pageSize 分页大小
     * @return ReturnObject<PageInfo<VoObject>> 查询的分页结果
     * createdBy
     * modifiedBy  2020/11/7 19:20
     */
    public ReturnObject<PageInfo<VoObject>> findSimpleOrderSelective(Long userId,String orderSn, Integer state, String beginTime, String endTime,Integer page, Integer pageSize){
        OrderPoExample example=new OrderPoExample();
        OrderPoExample.Criteria criteria=example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        criteria.andBeDeletedNotEqualTo((byte) 1);
        if(orderSn!=null)
            criteria.andOrderSnEqualTo(orderSn);
        if(state!=null)
            criteria.andStateEqualTo(state.byteValue());
        if(beginTime!=null) {
            LocalDateTime bt = LocalDateTime.parse(beginTime);
            criteria.andGmtCreateGreaterThanOrEqualTo(bt);
        }
        if(endTime!=null) {
            LocalDateTime et = LocalDateTime.parse(endTime);
            criteria.andGmtCreateLessThanOrEqualTo(et);
        }
        PageHelper.startPage(page,pageSize);
        try {
            List<OrderPo> orderPos = orderPoMapper.selectByExample(example);
            logger.debug("numbers"+orderPos.size());
            List<VoObject> orders = new ArrayList<>(orderPos.size());
            PageInfo<OrderPo> orderPoPageInfo = new PageInfo<>(orderPos);
            for (OrderPo po : orderPos) {
                logger.debug("get po orderSn"+po.getOrderSn());
                Order order = new Order(po,null);
                orders.add(order);
            }
            PageInfo<VoObject> orderPage=new PageInfo<>(orders);
            orderPage.setPages(orderPoPageInfo.getPages());
            orderPage.setPageNum(orderPoPageInfo.getPageNum());
            orderPage.setPageSize(orderPoPageInfo.getPageSize());
            orderPage.setTotal(orderPoPageInfo.getTotal());
            return new ReturnObject<>(orderPage);
        }
        catch (DataAccessException e){
            logger.error("selectOrders: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch(Exception e)
        {
            logger.error("other exception:"+e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 买家查询单个订单
     *
     * @author jwy
     * @param userId 用户id
     * @param id 订单id
     * @return ReturnObject<order> 查询结果
     * createdBy
     * modifiedBy  2020/11/29 19:20
     */
    public ReturnObject<Order> findOrderById(Long userId,Long id) {
        OrderItemPoExample example = new OrderItemPoExample();
        OrderItemPoExample.Criteria criteria1 = example.createCriteria();
        criteria1.andOrderIdEqualTo(id);
        try {
            OrderPo orderPo = orderPoMapper.selectByPrimaryKey(id);
            if(orderPo==null||orderPo.getBeDeleted().equals((byte)1))
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            if(!orderPo.getCustomerId().equals(userId))
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            List<OrderItemPo> orderItemPos = orderItemPoMapper.selectByExample(example);
            Order order = new Order(orderPo, orderItemPos);
            return new ReturnObject<>(order);
        } catch (DataAccessException e) {
            logger.error("selectOrder: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception:" + e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }

    }

    /**
     * 店家查询订单概要
     *
     * @author cyx
     * @param shopId 商户id
     * @param customerId 查询的购买者用户id
     * @param orderSn 订单编号
     * @param beginTime 时间下限
     * @param endTime 时间上限
     * @param page 分页
     * @param pageSize 分页大小
     * @return ReturnObject<PageInfo<VoObject>> 查询的分页结果
     * createdBy cyx 2020/11/30 9:20
     * modifiedBy
     */
    public ReturnObject<PageInfo<VoObject>> shopsShopIdOrdersGet(Long shopId, Long customerId, String orderSn, String beginTime, String endTime, Integer page, Integer pageSize) {
        OrderPoExample example=new OrderPoExample();
        OrderPoExample.Criteria criteria=example.createCriteria();
        criteria.andShopIdEqualTo(shopId);
        if(customerId!=null)
            criteria.andCustomerIdEqualTo(customerId);
        if(orderSn!=null)
            criteria.andOrderSnEqualTo(orderSn);
//        if(beginTime!=null&&endTime!=null){
//            LocalDateTime bt = LocalDateTime.parse(beginTime);
//            LocalDateTime et = LocalDateTime.parse(endTime);
//            if(bt.isAfter(et))
//                return new ReturnObject<>(ResponseCode.Log_Bigger);
//        }
        if(beginTime!=null) {
            LocalDateTime bt = LocalDateTime.parse(beginTime);
            criteria.andGmtCreateGreaterThanOrEqualTo(bt);
        }
        if(endTime!=null) {
            LocalDateTime et = LocalDateTime.parse(endTime);
            criteria.andGmtCreateLessThanOrEqualTo(et);
        }
        PageHelper.startPage(page,pageSize);
        try {
            List<OrderPo> orderPos = orderPoMapper.selectByExample(example);
            logger.debug("numbers"+orderPos.size());
            List<VoObject> orders = new ArrayList<>(orderPos.size());
            PageInfo<OrderPo> orderPoPageInfo = new PageInfo<>(orderPos);
            for (OrderPo po : orderPos) {
                logger.debug("get po orderSn"+po.getOrderSn());
                Order order = new Order(po,null);
                orders.add(order);
            }
            PageInfo<VoObject> orderPage=new PageInfo<>(orders);
            orderPage.setPages(orderPoPageInfo.getPages());
            orderPage.setPageNum(orderPoPageInfo.getPageNum());
            orderPage.setPageSize(orderPoPageInfo.getPageSize());
            orderPage.setTotal(orderPoPageInfo.getTotal());
            return new ReturnObject<>(orderPage);
        }
        catch (DataAccessException e){
            logger.error("selectOrders: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch(Exception e)
        {
            logger.error("other exception:"+e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 店家查询订单详细信息
     *
     * @author cyx
     * @param shopId 店铺id
     * @param id 订单id
     * @return ReturnObject<VoObject> 订单详细视图
     * createdBy cyx 2020/11/30 10:00
     * modifiedBy
     */
    public ReturnObject<Order> shopFindOrderById(Long shopId, Long id) {
        OrderItemPoExample example = new OrderItemPoExample();
        OrderItemPoExample.Criteria criteria1 = example.createCriteria();
        criteria1.andOrderIdEqualTo(id);
        try {
            OrderPo orderPo = orderPoMapper.selectByPrimaryKey(id);
            if(orderPo==null)
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("查询的订单id不存在"));
            if(orderPo.getShopId()==null||(!orderPo.getShopId().equals(shopId)))
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("查询的订单id不是本店铺的"));
            List<OrderItemPo> orderItemPos = orderItemPoMapper.selectByExample(example);
            Order order = new Order(orderPo, orderItemPos);
            return new ReturnObject<>(order);
        } catch (DataAccessException e) {
            logger.error("selectOrder: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception:" + e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 买家发货前修改订单
     * @author jwy
     * @param id 订单id
     * @param vo 订单修改视图
     * @return ReturnObject 返回对象
     * createdBy jwy 2020/11/30 20:40
     */
    public ReturnObject<Object> modifyUserOrderByVo(Long id, OrderSimpleVo vo,Long userId) {
        OrderPo orderPo=orderPoMapper.selectByPrimaryKey(id);
        if(orderPo==null||orderPo.getBeDeleted().equals((byte) 1))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        if(!orderPo.getCustomerId().equals(userId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        //如果  已发货，3：已完成，4：已取消 则不能修改收货地址
        if(((orderPo.getSubstate()!=null&&orderPo.getSubstate().intValue()==Order.SubState.TRANS.getCode()))
                || (orderPo.getState()!=null&&orderPo.getState().intValue()>=Order.State.END.getCode()))
        {
            logger.info("已发货不能修改收货地址 id="+id);
            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        }

        Order order=new Order(orderPo,null);
        OrderPo po=order.createUpdatePo(vo);
        po.setGmtModified(LocalDateTime.now());
        int ret;
        try{
            ret=orderPoMapper.updateByPrimaryKeySelective(po);
        }
        catch (DataAccessException e) {
            logger.error("updateOrder: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception:" + e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }
        return new ReturnObject<>();
    }

    /**
     * 店家修改订单留言
     * @author cyx
     * @param shopId 店铺id
     * @param id 订单id
     * @param vo 订单修改视图
     * @return ReturnObject 返回对象
     * createdBy cyx 2020/12/01 15:46
     */
    public ReturnObject shopModifyUserOrderByVo(Long shopId, Long id, OrderMessageVo vo) {
        OrderPo orderPo=orderPoMapper.selectByPrimaryKey(id);

        if(orderPo==null){
            logger.info("订单id不存在 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("修改的订单id不存在"));
        }
        //如果不是本店铺的订单则不能修改留言
        if(orderPo.getShopId()==null||(!orderPo.getShopId().equals(shopId)))
        {
            logger.info("非本店铺订单不能修改 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("修改的订单id不是本店铺的"));
        }
        if(vo.getMessage()==null||vo.getMessage().equals(""))
        {
            logger.info("message不能为空");
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID,String.format("Message字段为空"));
        }
        orderPo.setMessage(vo.getMessage());
        int ret;
        try{
            ret=orderPoMapper.updateByPrimaryKeySelective(orderPo);
        }
        catch (DataAccessException e) {
            logger.error("updateOrder: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception:" + e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }
        return new ReturnObject<>();
    }

    /**
     * 买家标记确认收货
     * @author jwy
     * @param userId
     * @param id 订单id
     * @return ReturnObject 返回对象
     * createdBy jwy 2020/12/01 20:40
     */
    public ReturnObject confirmUserOrderById(Long userId, Long id) {
        OrderPo orderPo=orderPoMapper.selectByPrimaryKey(id);
        if(orderPo==null||orderPo.getBeDeleted().intValue()==1)
        {
            logger.info("操作的订单id不存在 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(!orderPo.getCustomerId().equals(userId))
        {
            logger.info("操作的订单id不是自己的 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        logger.debug("标记发货的订单状态"+orderPo.getState().toString()+orderPo.getSubstate().toString());
        //子状态为已发货，主状态为待收货的订单标记确认收货。
        if(orderPo.getState().intValue()!=Order.State.ARRIED.getCode()
        ||orderPo.getSubstate().intValue()!=Order.SubState.TRANS.getCode())
        {
            logger.info("订单状态不是待收货 id="+id);
            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        }
        orderPo.setConfirmTime(LocalDateTime.now());
        orderPo.setState(Order.State.END.getCode().byteValue());
        orderPo.setSubstate(null);
        orderPo.setGmtModified(LocalDateTime.now());
        int ret;
        try{
            ret=orderPoMapper.updateByPrimaryKeySelective(orderPo);
        }
        catch (DataAccessException e) {
            logger.error("updateOrder: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception:" + e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }
        //检查更新是否成功
        if(ret==0)
        {
            logger.info("操作的订单id不存在 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>();
    }

    /**
     * 买家取消删除订单
     * @author jwy
     * @param userId
     * @param id 订单id
     * @return ReturnObject 返回对象
     * createdBy jwy 2020/12/01 20:40
     */
    public ReturnObject cancelDeleteOrderById(Long userId, Long id) {
        OrderPo orderPo=orderPoMapper.selectByPrimaryKey(id);
        if(orderPo==null||orderPo.getBeDeleted().intValue()==1)
        {
            logger.info("操作的订单id不存在 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(!orderPo.getCustomerId().equals(userId))
        {
            logger.info("操作的订单id不是自己的 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        //状态为 3：已完成，4：已取消 订单不能取消，其他状态均能取消
        if(orderPo.getState().intValue()!=Order.State.END.getCode()
                &&orderPo.getState().intValue()!=Order.State.CANCELED.getCode()
                &&orderPo.getSubstate().intValue()!=Order.SubState.TRANS.getCode())
        {
            logger.info("订单状态发货前，取消订单 id="+id);
            orderPo.setState(Order.State.CANCELED.getCode().byteValue());
            orderPo.setSubstate(null);
        }
        else if(orderPo.getState().intValue()==Order.State.END.getCode()
        ||orderPo.getState().intValue()==Order.State.CANCELED.getCode())
        {
            logger.info("订单状态已收货，逻辑删除订单 id="+id);
            orderPo.setBeDeleted((byte) 1);
        }
        else
        {
            logger.info("订单状态不合法 id="+id);
            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        }
        int ret;
        try{
            ret=orderPoMapper.updateByPrimaryKeySelective(orderPo);
        }
        catch (DataAccessException e) {
            logger.error("updateOrder: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception:" + e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }
        //检查更新是否成功
        if(ret==0)
        {
            logger.info("操作的订单id不存在 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>();
    }

    /**
     * 买家将团购订单转为普通订单
     * @author jwy
     * @param userId
     * @param id 订单id
     * @return ReturnObject 返回对象
     * createdBy jwy 2020/12/01 20:40
     */
    public ReturnObject changeToNormalOrder(Long userId, Long id) {

        OrderPo orderPo=orderPoMapper.selectByPrimaryKey(id);
        if(orderPo==null||orderPo.getBeDeleted().intValue()==1)
        {
            logger.info("操作的订单id不存在 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(!orderPo.getCustomerId().equals(userId))
        {
            logger.info("操作的订单id不是自己的 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        //子状态必须为已支付后的团购未达到门槛。
        if(orderPo.getState().intValue()!=Order.State.ARRIED.getCode()
        ||orderPo.getSubstate().intValue()!= Order.SubState.GROUPONFAILED.getCode())
        {
            logger.info("订单状态不合法 id="+id);
            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        }
        orderPo.setGmtModified(LocalDateTime.now());
        orderPo.setGrouponId(null);  //要不要设置团购id为空???
        orderPo.setOrderType(Order.OrderType.NORMAL.getCode().byteValue());
        //子状态置为
        orderPo.setSubstate(Order.SubState.PAIED.getCode().byteValue());
        orderPo.setState((Order.State.ARRIED.getCode().byteValue()));
        int ret;
        try{
            ret=orderPoMapper.updateByPrimaryKeySelective(orderPo);
        }
        catch (DataAccessException e) {
            logger.error("updateOrder: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception:" + e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }
        //检查更新是否成功
        if(ret==0)
        {
            logger.info("操作的订单id不存在 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>();
    }

    /**
     * 店家对订单标记发货
     * @author cyx
     * @param shopId
     * @param id 订单id
     * @return ReturnObject 返回对象
     * createdBy cyx 2020/12/01 20:09
     */
    public ReturnObject postFreights(Long shopId, Long id, OrderFreightSnVo vo) {
        OrderPo orderPo=orderPoMapper.selectByPrimaryKey(id);
        if(orderPo==null)
        {
            logger.info("操作的订单id不存在 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("修改的订单id不存在"));
        }
        //如果不是本店铺的订单则不能标记发货
        if(orderPo.getShopId()==null||(!orderPo.getShopId().equals(shopId)))
        {
            logger.info("非本店铺订单不能修改 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("修改的订单id不是本店铺的"));
        }
        //运单号为空
        if(vo.getFreightSn()==null||vo.getFreightSn().equals(""))
        {
            logger.info("freightSn不能为空");
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID,String.format("freightSn字段不能为空"));
        }
        //状态为付款完成的订单标记发货。
        if(orderPo.getSubstate()==null||orderPo.getSubstate().intValue()!=Order.SubState.PAIED.getCode()
                ||orderPo.getState().intValue()!=Order.State.ARRIED.getCode())
        {
            logger.info("订单状态不是待发货 id="+id);
            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        }
        orderPo.setShipmentSn(vo.getFreightSn());
        //将状态变为待收货
        orderPo.setState(Order.State.ARRIED.getCode().byteValue());
        //子状态变为已发货
        orderPo.setSubstate(Order.SubState.TRANS.getCode().byteValue());
        int ret;
        try{
            ret=orderPoMapper.updateByPrimaryKeySelective(orderPo);
        }
        catch (DataAccessException e) {
            logger.error("updateOrder: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception:" + e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }
        //检查更新是否成功
        if(ret==0)
        {
            logger.info("订单id不存在或已被删除");
        }
        return new ReturnObject<>();
    }

    /**
     * 管理员取消订单
     * @author cyx
     * @param shopId
     * @param id 订单id
     * @return ReturnObject 返回对象
     * createdBy cyx 2020/12/01 21:08
     */
    public ReturnObject shopsShopIdOrdersIdDelete(Long shopId, Long id) {
        OrderPo orderPo=orderPoMapper.selectByPrimaryKey(id);
        if(orderPo==null)
        {
            logger.info("操作的订单id不存在 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("取消的订单id不存在"));
        }
        //如果不是本店铺的订单则不能取消
        if(orderPo.getShopId()==null||(!orderPo.getShopId().equals(shopId)))
        {
            logger.info("非本店铺订单不能修改 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("取消的订单id不是本店铺的"));
        }
        //可取消状态
        if(orderPo.getState().intValue()<=Order.State.ARRIED.getCode()&&orderPo.getSubstate().intValue()!=Order.SubState.TRANS.getCode())
        {
            logger.info("订单状态合法，取消订单 id="+id);
            orderPo.setState(Order.State.CANCELED.getCode().byteValue());
        }
        else
        {
            logger.info("订单状态不合法 id="+id);
            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
        }
        int ret;
        try{
            ret=orderPoMapper.updateByPrimaryKeySelective(orderPo);
        }
        catch (DataAccessException e) {
            logger.error("updateOrder: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception:" + e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }
        //检查更新是否成功
        if(ret==0)
        {
            logger.info("操作的订单id不存在 id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>();
    }


    /**
     * 为支付单查找需要支付的价格 = 商品价格+运费-优惠价格
     * @author cyx
     * @param orderId 订单id
     * @return long 返回对象
     * createdBy cyx 2020/12/17 12:43
     */
    public Long caculateAmount(Long orderId) {
        OrderPo orderPo = orderPoMapper.selectByPrimaryKey(orderId);
        if(orderPo==null)
            return (-1L);
        else
            return orderPo.getOriginPrice()+orderPo.getFreightPrice()- orderPo.getDiscountPrice();
    }

    /**
     * 查找订单的shopId
     * @author cyx
     * @param orderId 订单id
     * @return long 返回对象
     * createdBy cyx 2020/12/17 15:24
     */
    public Long getShopIdByOrderId(Long orderId) {
        OrderPo orderPo = orderPoMapper.selectByPrimaryKey(orderId);
        if(orderPo==null)
            return (-1L);
        else
            return orderPo.getShopId();
    }

    public int getCustomerIdByOrderId(Long orderId,Long userId) {
        OrderPo orderPo = orderPoMapper.selectByPrimaryKey(orderId);
        if(orderPo==null)
            return 1;
        else if(orderPo.getCustomerId()!=null&&orderPo.getCustomerId().equals(userId))
            return 2;
        else return 3;
    }

    public int getOrderState(Long orderId) {
        OrderPo orderPo = orderPoMapper.selectByPrimaryKey(orderId);
        if(orderPo==null)
            return 1;
        else if(orderPo.getState()!=Order.State.WAITINGPAY.getCode().byteValue()||
                (orderPo.getSubstate()!=Order.SubState.CREATED.getCode().byteValue()
                        &&orderPo.getSubstate()!=Order.SubState.RESTPAING.getCode().byteValue()))
            return 2;
        else return 3;
    }
}
