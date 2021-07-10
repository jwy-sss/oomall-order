package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.goodsservice.model.vo.ShopVo;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.bo.OrderItem;
import cn.edu.xmu.provider.model.vo.CustomerVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单返回VO
 * @author jwy
 * @date Created in 2020/11/7 23:34
 **/
@Data
public class OrderRetVo {

    @ApiModelProperty(value = "订单id")
    private Long id;

    //customer和shop也要添加

//    @ApiModelProperty(value = "购买者信息")
//    private CustomerRetVo customer;

//    @ApiModelProperty(value = "商铺信息")
//    private ShopRetVo shop;

    @ApiModelProperty(value = "母单id")
    private Long pid;

    @ApiModelProperty(value = "订单的类别")
    private Integer orderType;

    @ApiModelProperty(value = "订单状态")
    private Integer state;

    @ApiModelProperty(value = "订单子状态")
    private Integer subState;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    private LocalDateTime confirmTime;

    @ApiModelProperty(value = "原价")
    private Long originPrice;

    @ApiModelProperty(value = "减价")
    private Long discountPrice;

    @ApiModelProperty(value = "运费")
    private Long freightPrice;

    private Integer rebateNum;

    @ApiModelProperty(value = "留言")
    private String message;

    @ApiModelProperty(value = "地区id")
    private Long regionId;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "电话")
    private String mobile;

    @ApiModelProperty(value = "收货人")
    private String consignee;

    @ApiModelProperty(value = "优惠券id")
    private Long couponId;

    @ApiModelProperty(value = "团购id")
    private Long grouponId;

    private Long presaleId;

    private String shipmentSn;

    private String orderSn;

    @ApiModelProperty(value = "订单明细")
    private List<OrderItemRetVo> orderItems;

    /**
     * 用Order对象建立Vo对象
     *
     * @author jwy
     * @param order order
     * @return OrderRetVo
     * createdBy jwy 2020/11/07 13:57
     * modifiedBy jwy 2020/11/7 19:20
     */
    public OrderRetVo(Order order) {
//        customer=new CustomerRetVo();
//        shop=new ShopRetVo();
//        customer.setId(order.getCustomerVo().getId());
//        customer.setName(order.getCustomerVo().getRealName());
//        customer.setUserName(order.getCustomerVo().getUserName());
//        shop.setId(order.getShopVo().getId());
//        shop.setName(order.getShopVo().getName());
//        shop.setState(order.getShopVo().getState());
//        shop.setGmtCreate(order.getShopVo().getGmtCreate());
//        shop.setGmtModified(order.getShopVo().getGmtModified());
        this.id = order.getId();
        this.orderSn=order.getOrderSn();
//        this.customerId = order.getCustomerId();
//        this.shopId = order.getShopId();
        this.pid = order.getPid();
        this.gmtCreate = order.getGmtCreate();
        if(order.getOrderType()!=null)
        this.orderType = order.getOrderType().getCode();
        if(order.getState()!=null)
        this.state = order.getState().getCode();
        if(order.getSubstate()!=null)
        this.subState = order.getSubstate().getCode();
        this.gmtModified=order.getGmtModified();
        this.confirmTime=order.getConfirmTime();
        this.originPrice = order.getOriginPrice();
        this.discountPrice = order.getDiscountPrice();
        this.freightPrice = order.getFreightPrice();
        this.rebateNum=order.getRebateNum();
        this.message = order.getMessage();
        this.regionId=order.getRegionId();
        this.address=order.getAddress();
        this.mobile=order.getMobile();
        this.consignee = order.getConsignee();
        this.couponId = order.getCouponId();
        this.grouponId = order.getGrouponId();
        this.presaleId=order.getPresaleId();
        this.shipmentSn=order.getShipmentSn();
        if(null!=order.getOrderItemList()){
            List<OrderItemRetVo> orderItemList=new ArrayList<>(order.getOrderItemList().size());
            for(OrderItem orderItem:order.getOrderItemList())
            {
                OrderItemRetVo orderItemRetVo=new OrderItemRetVo(orderItem);
                orderItemList.add(orderItemRetVo);
            }
            this.orderItems=orderItemList;

        }

        }

}
