package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.bo.OrderItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单概要返回VO
 * @author jwy
 * @date Created in 2020/11/28 23:34
 **/
@Data
public class OrderSimpleRetVo {

    private Long id;
    private Long customerId;
    private Long shopId;
    private Long pid;
    private Integer orderType;
    private Integer state;
    private Integer subState;
    private LocalDateTime gmtCreate;
    private Long originPrice;
    private Long discountPrice;
    private Long freightPrice;
    private Long grouponId;
    private Long presaleId;
    private String shipmentSn;

    //根据bo构建vo
    public OrderSimpleRetVo(Order order)
    {
        this.id=order.getId();
        this.customerId=order.getCustomerId();
        this.shopId=order.getShopId();
        this.pid=order.getPid();
        if(order.getOrderType()!=null)
        this.orderType=order.getOrderType().getCode();
        if(order.getState()!=null)
        this.state=order.getState().getCode();
        if(order.getSubstate()!=null)
        this.subState=order.getSubstate().getCode();
        this.gmtCreate=order.getGmtCreate();
        this.originPrice=order.getOriginPrice();
        this.discountPrice=order.getDiscountPrice();
        this.freightPrice=order.getFreightPrice();
        this.grouponId=order.getGrouponId();
        this.presaleId=order.getPresaleId();
        this.shipmentSn=order.getShipmentSn();

    }
}

