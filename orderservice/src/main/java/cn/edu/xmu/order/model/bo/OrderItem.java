package cn.edu.xmu.order.model.bo;

import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.vo.OrderItemVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderItem implements Serializable {

    private Long id;

    private Long orderId;

    private Long goodsSkuId;

    private Integer quantity;

    private Long price;

    private Long discount;

    private String name;

    private Long couponActivityId;

    private Long beSharedId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
    /**
     * 默认构造函数
     */
    public OrderItem() {

    }

    /*
    根据vo创建bo
     */
    public OrderItem(OrderItemVo orderItemVo)
    {
        this.goodsSkuId=orderItemVo.getSkuId();
        this.quantity=orderItemVo.getQuantity();
        this.couponActivityId=orderItemVo.getCouponActId();
    }

    /**
     * 根据po构建bo
     * @param orderItemPo
     */
    public OrderItem(OrderItemPo orderItemPo) {
        this.id=orderItemPo.getId();
        this.orderId=orderItemPo.getOrderId();
        this.goodsSkuId=orderItemPo.getGoodsSkuId();
        this.quantity=orderItemPo.getQuantity();
        this.price=orderItemPo.getPrice();
        this.discount=orderItemPo.getDiscount();
        this.name=orderItemPo.getName();
        this.couponActivityId=orderItemPo.getCouponActivityId();
        this.beSharedId=orderItemPo.getBeShareId();
        this.gmtCreate=orderItemPo.getGmtCreate();
        this.gmtModified=orderItemPo.getGmtModified();
    }

}
