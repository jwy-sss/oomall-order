package cn.edu.xmu.order.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.bo.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

@Service
public class OrderInsertService {

    private Logger logger = LoggerFactory.getLogger(OrderInsertService.class);

    private RocketMQTemplate rocketMQTemplate;
    /**
     * 新建订单
     * @author jwy
     * @param userId 用户id
     * @param order 订单bo
     * @return ReturnObject<VoObject> 订单返回视图
     * createdBy jwy 2020/11/04 13:57
     * modifiedBy jwy 2020/11/7 19:20
     */
    @Transactional
    public ReturnObject<VoObject> insertOrder(Long userId, Order order) {
        ReturnObject<VoObject> retOrder = null;
        String json= JacksonUtil.toJson(order);
        logger.info("send insert order Message b: message="+json);

        rocketMQTemplate.asyncSend("order-insert-topic", MessageBuilder.withPayload(json).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                logger.info("sendOrderInsertMessage: onSuccess result = "+ sendResult+" time ="+ LocalDateTime.now());
            }

            @Override
            public void onException(Throwable throwable) {
                logger.info("sendOrderPayMessage: onException e = "+ throwable.getMessage()+" time ="+LocalDateTime.now());
            }
        });

        return retOrder;
    }

    public void sendOrderInsertMessage(Order order){

    }
}
