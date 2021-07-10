package cn.edu.xmu.order.service.impl;

import Service.OrderForOtherService;
import cn.edu.xmu.order.dao.OrderDao;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class OrderCaculateServiceImpl implements OrderForOtherService {

    @Autowired
    private OrderDao orderDao;
    @Override
    public Long caculateAmount(Long orderId) {
        return orderDao.caculateAmount(orderId);
    }

    @Override
    public Long getShopIdByOrderId(Long orderId) {
        return orderDao.getShopIdByOrderId(orderId);
    }

    @Override
    public int getCustomerIdByOrderId(Long orderId,Long userId) {
        return orderDao.getCustomerIdByOrderId(orderId,userId);
    }

    @Override
    public int getOrderState(Long orderId) {
        return orderDao.getOrderState(orderId);
    }


}
