package cn.edu.xmu.payment.controller;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.payment.PaymentServiceApplication;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author jun
 * @date Created in 2020/12/03 20:12
 **/
@SpringBootTest(classes = PaymentServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PaymentControllerTest {
    @Autowired
    private MockMvc mvc;

    private String content;

    private static final Logger logger = LoggerFactory.getLogger(PaymentControllerTest.class);

    /**
     * 创建测试用token
     *
     * @author 24320182203281 王纬策
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    private final String createTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }





    /**
     * 管理员查询订单or售后单的支付信息
     * @param
     * @param
     * @return Object
     * createdBy jun 2020/12/01 18:00
     */
    @Test
    public void adminGetPayment() throws Exception {
        String token=createTestToken(8L,1L,10000);

        try{
            content=new String(Files.readAllBytes(Paths.get("src/test/resources/adminGetPayment.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.debug("content: "+content);

        String responseSuccess=this.mvc.perform(get("/payment/shops/1/payments/38050").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: "+responseSuccess);

        JSONAssert.assertEquals(responseSuccess,content,true);





    }


    /**
     * 买家查询订单or售后单的支付信息
     * @return Object
     * createdBy jun 2020/12/01 18:00
     */

    @Test
    public void customerGetPayment() throws Exception {
        String responseString=null;
        String token=createTestToken(7L,2L,10000);

        try{
            content=new String(Files.readAllBytes(Paths.get("src/test/resources/customerGetPayment.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.debug("content: "+content);

        String responseSuccess=this.mvc.perform(get("/payment/shops/1/payments/38050").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: "+responseSuccess);
        JSONAssert.assertEquals(responseSuccess,content,true);
    }


    /**
     * 获得支付所有状态
     * @author jun
     *
     */
    @Test
    public void getPaymentState() throws Exception {
        String token=createTestToken(8L,1L,10000);
        String responseString=this.mvc.perform(get("/payment/payments/states").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\": 0,\"data\": [{\"name\": \"未支付\",\"code\": 0},{\"name\": \"已支付\",\"code\": 1},{\"name\": \"支付失败\",\"code\": 2}],\"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }
    /**
     * 获得支付所有类型
     * @author jun
     *
     */
    @Test
    public void getPaymentPattern() throws Exception {
        String token=createTestToken(8L,1L,10000);
        String responseString=this.mvc.perform(get("/payment/payments/patterns").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\": 0,\"data\": [{\"paymentPattern\": \"001\",\"name\": \"返点\"},{\"paymentPattern\": \"002\",\"name\": \"正常渠道\"}],\"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 管理员查询退款信息
     * @param
     * @param
     * @return Object
     * createdBy jun 2020/12/01 18:00
     */
    @Test
    public void adminGetRefund() throws Exception {
        String token=createTestToken(8L,1L,10000);

        try{
            content=new String(Files.readAllBytes(Paths.get("src/test/resources/getRefunds.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.debug("content: "+content);

        String responseSuccess=this.mvc.perform(get("/refund/shops/1/refunds/38040").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: "+responseSuccess);

        JSONAssert.assertEquals(responseSuccess,content,true);
        

    }


    /**
     * 买家查询退款信息
     * @return Object
     * createdBy jun 2020/12/01 18:00
     */

    @Test
    public void customerGetRefund() throws Exception {
        String token = createTestToken(7L, 2L, 10000);

        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/getRefunds2.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.debug("content: " + content);

        String responseSuccess = this.mvc.perform(get("/refund/refunds/2").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccess);
        JSONAssert.assertEquals(responseSuccess, content, true);


    }





}
