package cn.edu.xmu.order.discount.impl;

import cn.edu.xmu.order.discount.BaseCouponDiscount;
import cn.edu.xmu.order.discount.BaseCouponLimitation;
import cn.edu.xmu.order.model.bo.OrderItem;

import java.util.List;

public class CheapestPercentageDiscount extends BaseCouponDiscount {

	public CheapestPercentageDiscount() {}

	public CheapestPercentageDiscount(BaseCouponLimitation limitation, long value) {
		super(limitation, value);
	}

	@Override
	public void calcAndSetDiscount(List<OrderItem> orderItems) {
		int min = Integer.MAX_VALUE;
		int total = 0;
		for (int i = 0; i < orderItems.size(); i++) {
			OrderItem oi = orderItems.get(i);
			total += oi.getPrice() * oi.getQuantity();
			if (oi.getPrice() < min) {
				min = i;
			}
		}

		long discount = (long) ((1.0 * value / 100) * orderItems.get(min).getPrice());

		for (OrderItem oi : orderItems) {
			oi.setDiscount((long) ((1.0 * oi.getPrice() * oi.getQuantity()) / total * discount / oi.getQuantity()));
		}
	}
}
