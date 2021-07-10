package cn.edu.xmu.order.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 订单明细传值对象
 * @author jwy
 * @date Created in 2020/11/7 21:24
 **/
@Data
@ApiModel(description ="订单明细传值对象")
public class OrderItemVo {

    @NotBlank(message = "商品id不能为空")
    @ApiModelProperty(value = "商品sku_id")
    private Long skuId;

    @ApiModelProperty(value = "商品数量")
    private Integer quantity;

    @ApiModelProperty(value = "优惠活动id")
    private Long couponActId;
}
