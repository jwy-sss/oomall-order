package cn.edu.xmu.goodsservice.model.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * 订单明细传值对象
 * @author jwy
 * @date Created in 2020/11/7 21:24
 **/
@Data
public class OrderItemVo implements Serializable {

    private Long skuId;

    private Integer quantity;

    private Long couponActId;
}
