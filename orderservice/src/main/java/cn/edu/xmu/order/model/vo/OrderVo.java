package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.bo.OrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建订单传值对象
 * @author jwy
 * @date Created in 2020/11/7 21:24
 **/
@Data
@ApiModel(description ="创建订单传值对象")
public class OrderVo {

    @ApiModelProperty(value = "订单明细")
    private List<OrderItemVo> orderItemList;

    @ApiModelProperty(value = "收货人")
    private String consignee;

    @ApiModelProperty(value = "地区id")
    private Long regionId;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "电话")
    private String mobile;

    @ApiModelProperty(value = "留言")
    private String message;

    @ApiModelProperty(value = "优惠券id")
    private Long couponId;

    @ApiModelProperty(value = "预售id")
    private Long presaleId;

    @ApiModelProperty(value = "团购id")
    private Long grouponId;

    /**
     * 使用传入的VO创建Order对象
     *
     * @author jwy
     * @return Order
     * createdBy
     * modifiedBy
     */
    public Order createOrder() {
        Order order = new Order();
        order.setRegionId(this.regionId);
        order.setConsignee(this.consignee);
        order.setAddress(this.address);
        order.setMobile(this.mobile);
        order.setMessage(this.message);
        order.setCouponId(this.couponId);
        order.setPresaleId(this.presaleId);
        order.setGrouponId(this.grouponId);
        if(null!=this.orderItemList){
            List<OrderItem> orderItemList=new ArrayList<>(this.orderItemList.size());
            for(OrderItemVo orderItemVo:this.orderItemList)
            {
                OrderItem orderItem=new OrderItem(orderItemVo);
                orderItem.setGmtCreate(LocalDateTime.now());
                orderItemList.add(orderItem);
            }
            order.setOrderItemList(orderItemList);

        }
        return order;
    }
}
