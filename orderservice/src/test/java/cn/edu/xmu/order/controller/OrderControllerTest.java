package cn.edu.xmu.order.controller;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.order.OrderServiceApplication;
import cn.edu.xmu.order.model.vo.OrderSimpleVo;
import org.json.JSONException;
import org.junit.jupiter.api.Order;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author jwy
 * @date Created in 2020/11/27 20:18
 **/
@SpringBootTest(classes = OrderServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    private String content;

    private static final Logger logger = LoggerFactory.getLogger(OrderControllerTest.class);

    /**
     * 创建测试用token
     *
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * createdBy
     */
    private final String createTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    /**
     * 获取订单概要，某人全部
     * @throws Exception
     * @author yujiawei
     */
    @Test
    public void customerGetAllSimpleOrders1() throws Exception {

        String token = createTestToken(59L, 2L, 10000);

        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/getSimpleOrders.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        content="{\"errno\":0,\"data\":{\"total\":13,\"pages\":3,\"pageSize\":5,\"page\":1,\"list\":[{\"id\":456,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":null,\"subState\":null,\"gmtCreateTime\":\"2020-12-10T19:29:33\",\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":1174,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":null,\"subState\":null,\"gmtCreateTime\":\"2020-12-10T19:29:33\",\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":1175,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":null,\"subState\":null,\"gmtCreateTime\":\"2020-12-10T19:29:33\",\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":1423,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":null,\"subState\":null,\"gmtCreateTime\":\"2020-12-10T19:29:33\",\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":1424,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":null,\"subState\":null,\"gmtCreateTime\":\"2020-12-10T19:29:33\",\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]},\"errmsg\":\"成功\"}\n";
        logger.debug("content: " + content);

        String responseSuccess = this.mvc.perform(get("/order/orders?page=1&pageSize=5").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccess);

        JSONAssert.assertEquals(responseSuccess, content, false);
    }

    /**
     * 获取订单概要，13月时间超限
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    public void customerGetAllSimpleOrders5() throws Exception {
        //userid=58
        String token = createTestToken(58L, 2L, 10000);
        //String token=this.login("79734441805", "123456");
        String content = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":5,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";

        logger.debug("content: " + content);

        String responseSuccess = this.mvc.perform(get("/order/orders?beginTime=2021-12-10T19:29:33&page=1&pageSize=5").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccess);

        JSONAssert.assertEquals(responseSuccess, content, true);
    }


    /**
     * 获取订单概要，根据状态查
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    public void customerGetAllSimpleOrders6() throws Exception {
        //userid=58
        String token = createTestToken(58L, 2L, 10000);
        //String token=this.login("79734441805", "123456");
        String content = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":5,\"page\":1,\"list\":[{\"id\":38057,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":4,\"subState\":null,\"gmtCreateTime\":\"2020-11-28T17:48:46\",\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]},\"errmsg\":\"成功\"}\n";

        logger.debug("content: " + content);

        String responseSuccess = this.mvc.perform(get("/order/orders?beginTime=2020-11-29T17:48:47&endTime=2020-11-30T17:48:47&page=1&pageSize=5").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccess);

        JSONAssert.assertEquals(responseSuccess, content, true);
    }


    /**
     * 获取订单概要，根据orderSn,但不是自己的,故查不到
     * @throws Exception
     * @author yujiawei
     */
    @Test
    public void customerGetAllSimpleOrders2() throws Exception {
        String token = createTestToken(7L, 2L, 10000);

        String content = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":5,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";

        logger.debug("content: " + content);

        String responseSuccess = this.mvc.perform(get("/order/orders?orderSn=2016102333120&page=1&pageSize=5").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccess);

        JSONAssert.assertEquals(responseSuccess, content, true);
    }

    /**
     * 买家修改本人名下订单,订单已发货，无法修改
     *
     * @author yujiawei
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    public void UsermodifyOrder1() throws Exception {
        OrderSimpleVo vo = new OrderSimpleVo();
        vo.setConsignee("adad");
        vo.setRegionId(1L);
        vo.setAddress("曾厝垵");
        String token = createTestToken(7L, 2L, 10000);
        String orderJson = JacksonUtil.toJson(vo);
        String expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/orders/38058").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: " + responseString);

        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 买家取消，逻辑删除本人名下订单,订单已发货，无法取消
     * @author yujiawei
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    public void UserdeleteOrder1() throws Exception {

        String token = createTestToken(7L, 2L, 10000);
        String expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(delete("/order/orders/38055").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: " + responseString);

        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 买家标记确认收货,状态不合法
     * @author yujiawei
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    public void UserconfirmOrder1() throws Exception {

        String token = createTestToken(7L, 2L, 10000);
        String expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/orders/38056").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: " + responseString);

        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 买家将团购订单转为普通订单,状态不合法
     * @author yujiawei
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    public void UserchangeOrder1() throws Exception {

        String token = createTestToken(7L, 2L, 10000);
        String expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/orders/38057/groupon-normal").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: " + responseString);

        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

//    /**
//     * 买家修改本人名下订单,订单已发货，状态禁止
//     * @author yujiawei
//     * createdBy yujiawei 2020/12/3 17:07
//     * modifiedBy
//     */
//    @Test
//    public void UsermodifyOrder2() throws Exception {
//        OrderSimpleVo vo = new OrderSimpleVo();
//        vo.setConsignee("adad");
//        vo.setRegionId(1L);
//        vo.setAddress("曾厝垵");
//        String token = createTestToken(7L, 2L, 10000);
//        String orderJson = JacksonUtil.toJson(vo);
//        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";;
//        String responseString = null;
//        try {
//            responseString = this.mvc.perform(put("/order/13").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        logger.debug("response: "+responseString);
//
//        JSONAssert.assertEquals(expectedResponse, responseString, true);
//    }

    /**
     * 店家查询商户所有订单 (概要) 仅输入shopId
     * @author cyx
     * createdBy cyx 2020/11/30 9:12
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersGet0() throws Exception {
        String token = createTestToken(8L, 1L, 10000);

        content = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 8,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 48050,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 1,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": null,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 11,\n" +
                "        \"gmtCreateTime\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48051,\n" +
                "        \"customerId\": 2,\n" +
                "        \"shopId\": 1,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": null,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 12,\n" +
                "        \"gmtCreateTime\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48052,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 1,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": null,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 21,\n" +
                "        \"gmtCreateTime\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48053,\n" +
                "        \"customerId\": 4,\n" +
                "        \"shopId\": 1,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": null,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 22,\n" +
                "        \"gmtCreateTime\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48054,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 1,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": null,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 23,\n" +
                "        \"gmtCreateTime\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48055,\n" +
                "        \"customerId\": 7,\n" +
                "        \"shopId\": 1,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": null,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 24,\n" +
                "        \"gmtCreateTime\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48056,\n" +
                "        \"customerId\": 7,\n" +
                "        \"shopId\": 1,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": null,\n" +
                "        \"state\": 3,\n" +
                "        \"subState\": 11,\n" +
                "        \"gmtCreateTime\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48057,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 1,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": null,\n" +
                "        \"state\": 4,\n" +
                "        \"subState\": 11,\n" +
                "        \"gmtCreateTime\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";

        logger.debug("content: " + content);

        String responseSuccess = this.mvc.perform(get("/order/shops/1/orders").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccess);

        JSONAssert.assertEquals(responseSuccess, content, true);
    }

    /**
     * 店家查询商户所有订单 (概要) 输入shopId及page pageSize
     * @author cyx
     * createdBy cyx 2020/12/10 22:15
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersGet1() throws Exception {
        String token = createTestToken(8L, 1L, 10000);

        content = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 8,\n" +
                "    \"pages\": 3,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 48050,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 1,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": null,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 11,\n" +
                "        \"gmtCreateTime\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48051,\n" +
                "        \"customerId\": 2,\n" +
                "        \"shopId\": 1,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": null,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 12,\n" +
                "        \"gmtCreateTime\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48052,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 1,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": null,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 21,\n" +
                "        \"gmtCreateTime\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";

        logger.debug("content: " + content);

        String responseSuccess = this.mvc.perform(get("/order/shops/1/orders?page=1&pageSize=3").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccess);

        JSONAssert.assertEquals(responseSuccess, content, true);
    }

    /**
     * 店家查询商户所有订单 (概要) 查找指定买家且page=1,pageSize=3
     * @author cyx
     * createdBy cyx 2020/12/02 14:50
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersGet2() throws Exception {
        String token = createTestToken(8L, 1L, 10000);

        content = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 48051,\n" +
                "        \"customerId\": 2,\n" +
                "        \"shopId\": 1,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": null,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 12,\n" +
                "        \"gmtCreateTime\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";

        logger.debug("content: " + content);

        String responseSuccess = this.mvc.perform(get("/order/shops/1/orders?customerId=2&page=1&pageSize=3").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccess);

        JSONAssert.assertEquals(responseSuccess, content, true);
    }

    /**
     * 店家查询商户所有订单 (概要) 查找指定订单编号
     * @author cyx
     * createdBy cyx 2020/12/02 14:56
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersGet3() throws Exception {
        String token = createTestToken(8L, 1L, 10000);

        content = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":48052,\"customerId\":1,\"shopId\":1,\"pid\":null,\"orderType\":null,\"state\":2,\"subState\":21,\"gmtCreateTime\":\"2020-11-28T17:48:46\",\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]},\"errmsg\":\"成功\"}";
        logger.debug("content: " + content);

        String responseSuccess = this.mvc.perform(get("/order/shops/1/orders?orderSn=2016102363333").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccess);

        JSONAssert.assertEquals(responseSuccess, content, true);
    }

    /**
     * 店家查询商户所有订单 (概要) JWT不合法
     * @author cyx
     * createdBy cyx 2020/12/02 14:56
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersGet4() throws Exception {
        String token = "test";

        content = "{\n" +
                "  \"errno\": 501,\n" +
                "  \"errmsg\": \"JWT不合法\"\n" +
                "}";
        logger.debug("content: " + content);

        String responseSuccess = this.mvc.perform(get("/order/shops/1/orders?orderSn=2016102363333").header("authorization", token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccess);

        JSONAssert.assertEquals(responseSuccess, content, true);
    }
//    /**
//     * 店家查询店内订单完整信息（普通，团购，预售）成功
//     *
//     * @author cyx
//     * createdBy cyx 2020/11/30 19:51
//     * modifiedBy
//     */
//    @Test
//    public void shopsShopIdOrdersIdGet0() throws Exception {
//        String token = createTestToken(8L, 1L, 10000);
//
//        content = "{\n" +
//                "  \"errno\": 0,\n" +
//                "  \"data\": {\n" +
//                "    \"id\": 48050,\n" +
//                "    \"customerId\": 1,\n" +
//                "    \"shopId\": 1,\n" +
//                "    \"pid\": null,\n" +
//                "    \"orderType\": null,\n" +
//                "    \"state\": 6,\n" +
//                "    \"subState\": null,\n" +
//                "    \"gmtCreate\": \"2020-11-28T17:48:46\",\n" +
//                "    \"originPrice\": null,\n" +
//                "    \"discountPrice\": null,\n" +
//                "    \"freightPrice\": null,\n" +
//                "    \"message\": null,\n" +
//                "    \"regionId\": null,\n" +
//                "    \"address\": null,\n" +
//                "    \"mobile\": null,\n" +
//                "    \"consignee\": \"刘勤\",\n" +
//                "    \"couponId\": null,\n" +
//                "    \"grouponId\": null,\n" +
//                "    \"orderItemList\": [\n" +
//                "      {\n" +
//                "        \"skuId\": 185,\n" +
//                "        \"orderId\": 48050,\n" +
//                "        \"name\": null,\n" +
//                "        \"quantity\": 1,\n" +
//                "        \"price\": 4475,\n" +
//                "        \"discount\": null,\n" +
//                "        \"couponActId\": null,\n" +
//                "        \"beSharedId\": null\n" +
//                "      }\n" +
//                "    ]\n" +
//                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
//                "}";
//
//        logger.debug("content: " + content);
//
//        String responseSuccess = this.mvc.perform(get("/order/shops/1/orders/48050").header("authorization", token))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//
//        logger.debug("response: " + responseSuccess);
//
//        JSONAssert.assertEquals(responseSuccess, content, true);
//    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）订单id不存在
     *
     * @author cyx
     * createdBy cyx 2020/12/11 10:38
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdGet1() throws Exception {
        String token = createTestToken(8L, 1L, 10000);

        content = "{\n" +
                "  \"errno\": 504,\n" +
                "  \"errmsg\": \"查询的订单id不存在\"\n" +
                "}";

        logger.debug("content: " + content);

        String responseSuccess = this.mvc.perform(get("/order/shops/1/orders/4000000").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccess);

        JSONAssert.assertEquals(responseSuccess, content, true);
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）查找非本店铺订单
     *
     * @author cyx
     * createdBy cyx 2020/12/02 15:02
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdGet2() throws Exception {
        String token = createTestToken(8L, 1L, 10000);

        content = "{\"errno\":505,\"errmsg\":\"查询的订单id不是本店铺的\"}";

        logger.debug("content: " + content);

        String responseSuccess = this.mvc.perform(get("/order/shops/1/orders/1").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccess);

        JSONAssert.assertEquals(responseSuccess, content, true);
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）查找非本店铺订单
     *
     * @author cyx
     * createdBy cyx 2020/12/02 15:02
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdGet3() throws Exception {
        String token = "test";

        content = "{\n" +
                "  \"errno\": 501,\n" +
                "  \"errmsg\": \"JWT不合法\"\n" +
                "}";

        logger.debug("content: " + content);

        String responseSuccess = this.mvc.perform(get("/order/shops/1/orders/1").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccess);

        JSONAssert.assertEquals(responseSuccess, content, true);
    }
    /**
     * 店家修改订单留言 成功
     *
     * @author cyx
     * createdBy cyx 2020/12/1 16:07
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdPut0() {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token = createTestToken(8L, 1L, 100);
        String expectedResponse = "";
        String responseString = null;
        String confirmString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/48050").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家修改订单留言 订单id不存在
     *
     * @author cyx
     * createdBy cyx 2020/12/11 10:38
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdPut1() {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token = createTestToken(8L, 1L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/4000000").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":504,\"errmsg\":\"修改的订单id不存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家修改订单留言 订单非本店铺订单
     *
     * @author cyx
     * createdBy cyx 2020/12/2 15:16
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdPut2() {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token = createTestToken(8L, 1L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/1").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":505,\"errmsg\":\"修改的订单id不是本店铺的\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家修改订单留言 JWT不合法
     *
     * @author cyx
     * createdBy cyx 2020/12/1 16:07
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdPut3() {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token = "test";
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/48050").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\n" +
                "  \"errno\": 501,\n" +
                "  \"errmsg\": \"JWT不合法\"\n" +
                "}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家对订单标记发货 成功
     *
     * @author cyx
     * createdBy cyx 2020/12/1 20:32
     * modifiedBy
     */
    @Test
    public void postFreights0() {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = createTestToken(8L, 1L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/48052/deliver").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家对订单标记发货 操作的订单id不存在
     *
     * @author cyx
     * createdBy cyx 2020/12/11 10:48
     * modifiedBy
     */
    @Test
    public void postFreights1() {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = createTestToken(8L, 1L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/4000000/deliver").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\n\"errno\": 504,\n\"errmsg\": \"修改的订单id不存在\"\n}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家对订单标记发货 操作的订单不是本店铺的
     *
     * @author cyx
     * createdBy cyx 2020/12/2 15:17
     * modifiedBy
     */
    @Test
    public void postFreights2() {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = createTestToken(8L, 1L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/1/deliver").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":505,\"errmsg\":\"修改的订单id不是本店铺的\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家对订单标记发货 订单状态为新订单不满足待发货
     *
     * @author cyx
     * createdBy cyx 2020/12/2 15:19
     * modifiedBy
     */
    @Test
    public void postFreights3() {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = createTestToken(8L, 1L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/48050/deliver").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家对订单标记发货 订单状态为待支付尾款
     *
     * @author cyx
     * createdBy cyx 2020/12/2 15:19
     * modifiedBy
     */
    @Test
    public void postFreights4() {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = createTestToken(8L, 1L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/48051/deliver").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家对订单标记发货 订单状态为待成团
     *
     * @author cyx
     * createdBy cyx 2020/12/2 15:19
     * modifiedBy
     */
    @Test
    public void postFreights5() {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = createTestToken(8L, 1L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/48053/deliver").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家对订单标记发货 订单状态为未成团
     *
     * @author cyx
     * createdBy cyx 2020/12/2 15:19
     * modifiedBy
     */
    @Test
    public void postFreights6() {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = createTestToken(8L, 1L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/48054/deliver").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家对订单标记发货 订单状态为已发货
     *
     * @author cyx
     * createdBy cyx 2020/12/2 15:19
     * modifiedBy
     */
    @Test
    public void postFreights7() {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = createTestToken(8L, 1L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/48055/deliver").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家对订单标记发货 订单状态为已完成
     *
     * @author cyx
     * createdBy cyx 2020/12/2 15:19
     * modifiedBy
     */
    @Test
    public void postFreights8() {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = createTestToken(8L, 1L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/48056/deliver").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家对订单标记发货 订单状态为已取消
     *
     * @author cyx
     * createdBy cyx 2020/12/2 15:19
     * modifiedBy
     */
    @Test
    public void postFreights9() {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = createTestToken(8L, 1L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/48057/deliver").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 店家对订单标记发货 JWT不合法
     *
     * @author cyx
     * createdBy cyx 2020/12/1 20:32
     * modifiedBy
     */
    @Test
    public void postFreights10() {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = "test";
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/order/shops/1/orders/48052/deliver").header("authorization", token).contentType("application/json;charset=UTF-8").content(orderJson))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\n" +
                "  \"errno\": 501,\n" +
                "  \"errmsg\": \"JWT不合法\"\n" +
                "}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得订单所有状态
     *
     * @author cyx
     * createdBy cyx 2020/12/1 21:03
     * modifiedBy
     */
    @Test
    public void getorderState() throws Exception {
        String token = createTestToken(8L, 1L, 10000);
        String responseString = this.mvc.perform(get("/order/orders/states").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"name\": \"待付款\",\n" +
                "      \"code\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"待收货\",\n" +
                "      \"code\": 2\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"已完成\",\n" +
                "      \"code\": 3\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"已取消\",\n" +
                "      \"code\": 4\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 获得订单所有状态 JWT不合法
     *
     * @author cyx
     * createdBy cyx 2020/12/1 21:03
     * modifiedBy
     */
    @Test
    public void getorderState1() throws Exception {
        String token = "test";
        String responseString = this.mvc.perform(get("/order/orders/states").header("authorization", token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\n" +
                "  \"errno\": 501,\n" +
                "  \"errmsg\": \"JWT不合法\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员取消本店铺订单 订单状态为新订单
     *
     * @author cyx
     * createdBy cyx 2020/12/1 21:25
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdDelete0() throws Exception {
        String token = createTestToken(8L, 1L, 100);
        String responseString = this.mvc.perform(delete("/order/shops/1/orders/48050").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\": 0,\"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员取消本店铺订单 取消的订单id不存在
     *
     * @author cyx
     * createdBy cyx 2020/12/11 10:51
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdDelete1() throws Exception {
        String token = createTestToken(8L, 1L, 100);
        String responseString = this.mvc.perform(delete("/order/shops/1/orders/4000000").header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"取消的订单id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员取消本店铺订单 取消的订单不是本店铺的
     *
     * @author cyx
     * createdBy cyx 2020/12/2 15:24
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdDelete2() throws Exception {
        String token = createTestToken(8L, 1L, 100);
        String responseString = this.mvc.perform(delete("/order/shops/1/orders/1").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"取消的订单id不是本店铺的\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员取消本店铺订单 订单状态为待支付尾款
     *
     * @author cyx
     * createdBy cyx 2020/12/1 21:25
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdDelete3() throws Exception {
        String token = createTestToken(8L, 1L, 100);
        String responseString = this.mvc.perform(delete("/order/shops/1/orders/48051").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\": 0,\"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员取消本店铺订单 订单状态为付款完成
     *
     * @author cyx
     * createdBy cyx 2020/12/1 21:25
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdDelete4() throws Exception {
        String token = createTestToken(8L, 1L, 100);
        String responseString = this.mvc.perform(delete("/order/shops/1/orders/48052").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\": 0,\"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员取消本店铺订单 订单状态为待成团
     *
     * @author cyx
     * createdBy cyx 2020/12/1 21:25
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdDelete5() throws Exception {
        String token = createTestToken(8L, 1L, 100);
        String responseString = this.mvc.perform(delete("/order/shops/1/orders/48053").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\": 0,\"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员取消本店铺订单 订单状态为未成团
     *
     * @author cyx
     * createdBy cyx 2020/12/1 21:25
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdDelete6() throws Exception {
        String token = createTestToken(8L, 1L, 100);
        String responseString = this.mvc.perform(delete("/order/shops/1/orders/48054").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\": 0,\"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员取消本店铺订单 订单状态为已发货
     *
     * @author cyx
     * createdBy cyx 2020/12/1 21:25
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdDelete7() throws Exception {
        String token = createTestToken(8L, 1L, 100);
        String responseString = this.mvc.perform(delete("/order/shops/1/orders/48055").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员取消本店铺订单 订单状态为已完成
     *
     * @author cyx
     * createdBy cyx 2020/12/2 15:27
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdDelete8() throws Exception {
        String token = createTestToken(8L, 1L, 100);
        String responseString = this.mvc.perform(delete("/order/shops/1/orders/48056").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员取消本店铺订单 订单状态为已取消
     *
     * @author cyx
     * createdBy cyx 2020/12/2 15:27
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdDelete9() throws Exception {
        String token = createTestToken(8L, 1L, 100);
        String responseString = this.mvc.perform(delete("/order/shops/1/orders/48057").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":801,\"errmsg\":\"订单状态禁止\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    /**
     * 管理员取消本店铺订单 JWT不合法
     *
     * @author cyx
     * createdBy cyx 2020/12/1 21:25
     * modifiedBy
     */
    @Test
    public void shopsShopIdOrdersIdDelete10() throws Exception {
        String token = "test";
        String responseString = this.mvc.perform(delete("/order/shops/1/orders/48050").header("authorization", token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\n" +
                "  \"errno\": 501,\n" +
                "  \"errmsg\": \"JWT不合法\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

}