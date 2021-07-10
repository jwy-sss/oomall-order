package cn.edu.xmu.order.service.mq;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.dao.OrderDao;
import cn.edu.xmu.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.order.mapper.OrderPoMapper;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.po.OrderPo;
import cn.edu.xmu.order.model.po.OrderPoExample;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RocketMQMessageListener(topic = "order-pay-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 10, consumerGroup = "pay-group")
public class PayConsumerListener implements RocketMQListener<String> {
    @Autowired
    private OrderDao orderDao;

    private static final Logger logger = LoggerFactory.getLogger(PayConsumerListener.class);
    @Override
    public void onMessage(String message) {
        logger.info("onMessage: got message orderSn =" + message +" time = "+ LocalDateTime.now());
        orderDao.JudgePaied(message);//判断是否取消订单
        logger.info("end");
    }
}
