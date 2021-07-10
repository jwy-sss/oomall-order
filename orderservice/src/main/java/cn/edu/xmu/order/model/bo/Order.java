package cn.edu.xmu.order.model.bo;


import cn.edu.xmu.goodsservice.model.vo.ShopVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.vo.*;
import cn.edu.xmu.provider.model.vo.CustomerVo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jwy
 * @date Created in 2020/11/7 11:48
 **/
@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Order implements VoObject, Serializable {

    /**
     * 默认构造函数
     */
    public Order() {

    }


    /**
     * 后台订单状态
     */
    //订单：1：待付款，2：待收货，3：已完成，4：已取消，11：新订单，12：待支付尾款，21：付款完成，22：待成团，23：未成团，24：已发货
    public enum State {
        WAITINGPAY(1, "待付款"),
        ARRIED(2, "待收货"),
        END(3, "已完成"),
        CANCELED(4, "已取消");

        private static final Map<Integer, State> stateMap;

        //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
        static {
            stateMap = new HashMap();
            for (State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }


        private int code;


        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 后台订单子状态
     */
    public enum SubState {
        CREATED(11, "新订单"),
        RESTPAING(12, "待支付尾款"),
        PAIED(21, "付款完成"),
        WAITGROUPON(22, "待成团"),
        GROUPONFAILED(23, "未成团"),
        TRANS(24, "已发货");

        private static final Map<Integer, SubState> subStateMap;

        //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
        static {
            subStateMap = new HashMap();
            for (SubState enum1 : values()) {
                subStateMap.put(enum1.code, enum1);
            }
        }


        private int code;


        private String description;

        SubState(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static SubState getTypeByCode(Integer code) {
            return subStateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 订单类型
     */
    public enum OrderType {
        NORMAL(0, "普通订单"),
        GROUPON(1, "团购订单"),
        PRESALE(2, "预售订单"),
        NONE(3,"未知");

        private static final Map<Integer, OrderType> orderTypeMap;

        //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
        static {
            orderTypeMap = new HashMap();
            for (OrderType enum1 : values()) {
                orderTypeMap.put(enum1.code, enum1);
            }
        }

        private int code;

        private String description;

        OrderType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static OrderType getTypeByCode(Integer code) {
            return orderTypeMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private List<OrderItem> orderItemList;

    private Long id;

    private CustomerVo customerVo;

    private Long customerId;

    private Long shopId;

    private ShopVo shopVo;

    private String orderSn;

    private Long pid;

    private String consignee;

    private Long regionId;

    private String address;

    private String mobile;

    private String message;

    private OrderType orderType;

    private Long freightPrice;

    private Long couponId;

    private Long couponActivityId;

    private Long discountPrice;

    private Long originPrice;

    private Long presaleId;

    private Long grouponId;

    private Long grouponDiscount;

    private Integer rebateNum;

    private LocalDateTime confirmTime;

    private String shipmentSn;

    private State state;

    private SubState substate;

    private boolean beDeleted;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    @Override
    public Object createVo() {
        return new OrderRetVo(this);
    }

    @Override
    public OrderSimpleRetVo createSimpleVo() { return new OrderSimpleRetVo(this); }

    /**
     * 用bo对象创建po对象
     * @return
     */
    public OrderPo gotOrderPo() {
        OrderPo orderPo=new OrderPo();
        orderPo.setAddress(this.address);
        orderPo.setConfirmTime(this.getConfirmTime());
        orderPo.setConsignee(this.consignee);
        orderPo.setCouponActivityId(this.couponActivityId);
        orderPo.setCouponId(this.couponId);
        orderPo.setCustomerId(this.customerId);
        orderPo.setDiscountPrice(this.discountPrice);
        orderPo.setFreightPrice(this.freightPrice);
        orderPo.setGmtCreate(this.gmtCreate);
        orderPo.setGmtModified(this.gmtModified);
        orderPo.setGrouponDiscount(this.getGrouponDiscount());
        orderPo.setGrouponId(this.getGrouponId());
        orderPo.setId(this.getId());
        orderPo.setMessage(this.getMessage());
        orderPo.setMobile(this.getMobile());
        orderPo.setOrderSn(this.getOrderSn());
        if(this.orderType!=null) {
            Byte orderType = (byte) this.orderType.code;
            orderPo.setOrderType(orderType);
        }
        orderPo.setOriginPrice(this.getOriginPrice());
        orderPo.setPid(this.getPid());
        orderPo.setPresaleId(this.getPresaleId());
        orderPo.setRebateNum(this.getRebateNum());
        orderPo.setRegionId(this.getRegionId());
        orderPo.setShipmentSn(this.getShipmentSn());
        orderPo.setShopId(this.getShopId());
        if(this.beDeleted==true)
            orderPo.setBeDeleted((byte) 1);
        else
            orderPo.setBeDeleted((byte) 0);
        Byte state = (byte) this.state.code;
        orderPo.setState(state);
        Byte subState = (byte) this.substate.code;
        orderPo.setSubstate(subState);
        return  orderPo;
    }


    /**
     * 用orderbo对象创建orderitempo对象
     * @return
     */
    public List<OrderItemPo> gotOrderItemPo() {
        List<OrderItemPo> orderItemPoList=new ArrayList<>(this.getOrderItemList().size());
        for(OrderItem orderItem:this.getOrderItemList())
        {
            OrderItemPo orderItemPo=new OrderItemPo();
            orderItemPo.setBeShareId(orderItem.getBeSharedId());
            orderItemPo.setCouponActivityId(orderItem.getCouponActivityId());
            orderItemPo.setDiscount(orderItem.getDiscount());
            orderItemPo.setGmtCreate(orderItem.getGmtCreate());
            orderItemPo.setGoodsSkuId(orderItem.getGoodsSkuId());
            orderItemPo.setId(orderItem.getId());
            orderItemPo.setName(orderItem.getName());
            orderItemPo.setOrderId(orderItem.getOrderId());
            orderItemPo.setPrice(orderItem.getPrice());
            orderItemPo.setQuantity(orderItem.getQuantity());
            orderItemPoList.add(orderItemPo);
        }
        return orderItemPoList;
    }

    /**
     * 用po对象构建bo对象
     */
    public Order (OrderPo po,List<OrderItemPo> orderItemPos)
    {

        if(po.getOrderType()!=null)
        this.orderType=OrderType.getTypeByCode(po.getOrderType().intValue());
        if(po.getSubstate()!=null)
        this.substate=SubState.getTypeByCode(po.getSubstate().intValue());
        this.state=State.getTypeByCode(po.getState().intValue());
        this.address=po.getAddress();
        if (po.getBeDeleted().intValue()==1) this.beDeleted = true;
        else this.beDeleted = false;
        this.confirmTime=po.getConfirmTime();
        this.consignee=po.getConsignee();
        this.couponActivityId=po.getCouponActivityId();
        this.couponId=po.getCouponId();
        this.customerId=po.getCustomerId();
        this.discountPrice=po.getDiscountPrice();
        this.freightPrice=po.getFreightPrice();
        this.id=po.getId();
        this.message=po.getMessage();
        this.mobile=po.getMobile();
        this.orderSn=po.getOrderSn();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
        this.originPrice=po.getOriginPrice();
        if(orderItemPos!=null) {
            this.orderItemList = new ArrayList<>(orderItemPos.size());
            for (OrderItemPo orderItemPo : orderItemPos) {
                OrderItem orderItem = new OrderItem(orderItemPo);
                this.orderItemList.add(orderItem);
            }
        }
        this.pid=po.getPid();
        this.presaleId=po.getPresaleId();
        this.grouponId=po.getGrouponId();
        this.grouponDiscount=po.getGrouponDiscount();
        this.rebateNum=po.getRebateNum();
        this.shipmentSn=po.getShipmentSn();
        this.mobile=po.getMobile();
        this.shopId=po.getShopId();
        this.regionId=po.getRegionId();
    }

    /**
     * 用vo对象创建更新po对象
     */
    public OrderPo createUpdatePo(OrderSimpleVo vo)
    {
        OrderPo po=new OrderPo();
        po.setId(this.id);
        po.setAddress(vo.getAddress());
        po.setRegionId(vo.getRegionId());
        po.setConsignee(vo.getConsignee());
        po.setMobile(vo.getMobile());
        return po;
    }

    public OrderPo createUpdateMessagePo(OrderMessageVo vo) {
        OrderPo po=new OrderPo();
        po.setMessage(vo.getMessage());
        return po;
    }

    public OrderPo postFreights(OrderFreightSnVo vo) {
        OrderPo po=new OrderPo();
        po.setState(State.ARRIED.getCode().byteValue());
        po.setShipmentSn(vo.getFreightSn());
        return po;
    }


//    /**
//     * 根据现有的orderitem创建core里面的orderitemlist
//     * @return
//     */
//    public List<cn.edu.xmu.ooad.order.bo.OrderItem> createCoreOrderItem()
//    {
//        List<cn.edu.xmu.ooad.order.bo.OrderItem> orderItems=new ArrayList<>(this.orderItemList.size());
//        for (OrderItem orderitem:this.orderItemList)
//        {
//            cn.edu.xmu.ooad.order.bo.OrderItem orderItem=new cn.edu.xmu.ooad.order.bo.OrderItem();
//            orderItem.setCouponActivityId(orderitem.getCouponActivityId());
//            orderItem.setDiscount(orderitem.getDiscount());
//            orderItem.setQuantity(orderitem.getQuantity());
//            orderItem.setSkuId(orderitem.getGoodsSkuId());
//        }
//        return orderItems;
//    }
//
//    /**
//     * 根据计算好的orderitemlist设置自己的
//     * @param orderItemList
//     */
//    public void setOrderItemListByCore(List<cn.edu.xmu.ooad.order.bo.OrderItem> orderItemList) {
//
//        for(int i=0;i<orderItemList.size();i++)
//        {
//            Long discount=orderItemList.get(i).getDiscount();
//            this.orderItemList.get(i).setDiscount(discount);
//            this.discountPrice+=discount;
//        }
//    }
}
