package cn.edu.xmu.order.service;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.order.model.bo.Order;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.time.LocalDateTime;

public class OrderConsumerListenerTest {

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumerListenerTest.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * @description 插入订单及订单明细测试
     * @return void
     * @author jwy
     * created at 11/18/20 1:08 PM
     */
    @Test
    public void sendOrderMessageTest(){
        Order order = new Order();
        order.setCustomerId(1L);
        order.setGmtCreate(LocalDateTime.now());
        //order.setOrderSn();

        String json = JacksonUtil.toJson(order);
        Message message = MessageBuilder.withPayload(json).build();
        logger.info("sendLogMessage: message = " + message);
        rocketMQTemplate.sendOneWay("log-topic", message);
    }
}
