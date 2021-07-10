package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.OrderItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单明细返回VO
 * @author jwy
 * @date Created in 2020/11/3 23:34
 **/
@Data
public class OrderItemRetVo {

    @ApiModelProperty(value = "商品skuId")
    private Long skuId;

    @ApiModelProperty(value = "订单id")
    private Long  orderId;

    @ApiModelProperty(value = "商品名")
    private String name ;

    @ApiModelProperty(value = "商品数量")
    private Integer quantity;

    @ApiModelProperty(value = "价格")
    private Long price;

    @ApiModelProperty(value = "减免")
    private Long discount;

    @ApiModelProperty(value = "优惠活动id")
    private Long couponActId;

    @ApiModelProperty(value = "分享id")
    private Long beSharedId;


    /**
     * 由PO对象创建RetVo对象
     */
    public OrderItemRetVo(OrderItem orderItem)
    {
        this.skuId=orderItem.getGoodsSkuId();
        this.beSharedId=orderItem.getBeSharedId();
        this.couponActId=orderItem.getCouponActivityId();
        this.discount=orderItem.getDiscount();
        this.name=orderItem.getName();
        this.price=orderItem.getPrice();
        this.quantity=orderItem.getQuantity();
        this.orderId=orderItem.getOrderId();
    }
}
