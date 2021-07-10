package cn.edu.xmu.order.service.mq;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.dao.OrderDao;
import cn.edu.xmu.order.mapper.OrderPoMapper;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.po.OrderItemPo;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.service.OrderService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单消费者
 * @author jwy
 */
@Service
@RocketMQMessageListener(topic = "order-insert-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 30, consumerGroup = "insert-group")
public class OrderConsumerListener implements RocketMQListener<String>{

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderPoMapper orderPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumerListener.class);

    /*
    执行插入数据库的任务
     */
    @Override
    public void onMessage(String message) {
        Order order = JacksonUtil.toObj(message, Order.class);
        //logger.debug("onMessage: got message order =" + order);
        orderDao.insertOrder(order);
    }
}
