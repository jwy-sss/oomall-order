package cn.edu.xmu.order.discount.impl;

import cn.edu.xmu.order.discount.BaseCouponDiscount;
import cn.edu.xmu.order.discount.BaseCouponLimitation;
import cn.edu.xmu.order.model.bo.OrderItem;

import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-18
 */
public class PriceCouponDiscount extends BaseCouponDiscount {

	public PriceCouponDiscount(){}

	public PriceCouponDiscount(BaseCouponLimitation limitation, long value) {
		super(limitation, value);
	}

	@Override
	public void calcAndSetDiscount(List<OrderItem> orderItems) {
		long total = 0L;
		for (OrderItem oi : orderItems) {
			total += oi.getPrice() * oi.getQuantity();
		}

		for (OrderItem oi : orderItems) {
			long discount = (long) ((1.0 * oi.getQuantity() * oi.getPrice() / total) * value / oi.getQuantity());
			oi.setDiscount(discount);
		}
	}
}
