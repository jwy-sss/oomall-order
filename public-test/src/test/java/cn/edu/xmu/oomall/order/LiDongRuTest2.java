package cn.edu.xmu.oomall.order;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * @author 李东儒 24320182203222
 * create 2020/12/16 12:10
 * 测试用户：id为356，用户名3835711724，密码123456
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LiDongRuTest2 {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private static JSONObject normalUnpaidNewOrder;
    private static JSONObject grouponUnpaidNewOrder;
    private static JSONObject presaleUnpaidNewOrder;
    private static JSONObject presaleFinalPaymentUnpaidOrder;
    private static JSONObject normalPaidFinishedOrder;
    private static JSONObject grouponPaidFinishedOrder;
    private static JSONObject presalePaidFinishedOrder;
    private static JSONObject grouponGroupingOrder;
    private static JSONObject grouponGroupFailedOrder;
    private static JSONObject normalDeliveredOrder;
    private static JSONObject grouponDeliveredOrder;
    private static JSONObject presaleDeliveredOrder;
    private static JSONObject normalFinishedOrder;
    private static JSONObject grouponFinishedOrder;
    private static JSONObject presaleFinishedOrder;
    private static JSONObject normalCanceledOrder;
    private static JSONObject grouponCanceledOrder;
    private static JSONObject presaleCanceledOrder;
    private static JSONObject normalDeliveredOrder2;

    private WebTestClient manageClient;

    private WebTestClient mallClient;
    @BeforeEach
    public void setUp() throws JSONException {

        manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
        initialize();
    }

    private String login(String userName, String password) throws Exception{
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = manageClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

    /**
     * 买家查询（查询条件：页码为1，页号为20页）名下订单（概要） 成功
     * @author snow
     * create 2020/12/15 15:30
     */
    @Test
    public void getOrderBriefInfoSucceedPageEqual1PageSizEqual20() throws Exception {
        String token = login("3835711724", "123456");
        String getURL = "/orders?page=1&pageSize=20";
        try {
            byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONArray orderList = new JSONArray();
            orderList.add(normalUnpaidNewOrder);
            orderList.add(grouponUnpaidNewOrder);
            orderList.add(presaleUnpaidNewOrder);
            orderList.add(presaleFinalPaymentUnpaidOrder);
            orderList.add(normalPaidFinishedOrder);
            orderList.add(grouponPaidFinishedOrder);
            orderList.add(presalePaidFinishedOrder);
            orderList.add(grouponGroupingOrder);
            orderList.add(grouponGroupFailedOrder);
            orderList.add(normalDeliveredOrder);
            orderList.add(grouponDeliveredOrder);
            orderList.add(presaleDeliveredOrder);
            orderList.add(normalFinishedOrder);
            orderList.add(grouponFinishedOrder);
            orderList.add(presaleFinishedOrder);
            orderList.add(normalCanceledOrder);
            orderList.add(grouponCanceledOrder);
            orderList.add(presaleCanceledOrder);
            orderList.add(normalDeliveredOrder2);
            JSONObject responseData = new JSONObject();
            responseData.put("total", 19);
            responseData.put("pages", 1);
            responseData.put("pageSize", 20);
            responseData.put("page", 1);
            responseData.put("list", orderList);
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            response.put("data", responseData);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家查询（查询条件：页码为1，页号为5页）名下订单（概要） 成功
     * @author snow
     * create 2020/12/15 15:40
     */
    @Test
    public void getOrderBriefInfoSucceedPageEqual1PageSizEqual5() throws Exception {
        String token = login("3835711724", "123456");
        String getURL = "/orders?page=1&pageSize=5";
        try {
            byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONArray orderList = new JSONArray();
            orderList.add(normalUnpaidNewOrder);
            orderList.add(grouponUnpaidNewOrder);
            orderList.add(presaleUnpaidNewOrder);
            orderList.add(presaleFinalPaymentUnpaidOrder);
            orderList.add(normalPaidFinishedOrder);
            JSONObject responseData = new JSONObject();
            responseData.put("total", 19);
            responseData.put("pages", 4);
            responseData.put("pageSize", 5);
            responseData.put("page", 1);
            responseData.put("list", orderList);
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            response.put("data", responseData);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家查询（查询条件：页码为2，页号为5页）名下订单（概要） 成功
     * @author snow
     * create 2020/12/15 15:41
     */
    @Test
    public void getOrderBriefInfoSucceedPageEqual2PageSizEqual5() throws Exception {
        String token = login("3835711724", "123456");
        String getURL = "/orders?page=2&pageSize=5";
        try {
            byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONArray orderList = new JSONArray();
            orderList.add(grouponPaidFinishedOrder);
            orderList.add(presalePaidFinishedOrder);
            orderList.add(grouponGroupingOrder);
            orderList.add(grouponGroupFailedOrder);
            orderList.add(normalDeliveredOrder);
            JSONObject responseData = new JSONObject();
            responseData.put("total", 19);
            responseData.put("pages", 4);
            responseData.put("pageSize", 5);
            responseData.put("page", 2);
            responseData.put("list", orderList);
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            response.put("data", responseData);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家查询（查询条件：开始时间为2020-12-09 00:00:00）名下订单（概要） 成功
     * @author snow
     * create 2020/12/15 15:50
     */
    @Test
    public void getOrderBriefInfoSucceedBeginTimeEquals20201209000000() throws Exception {
        String token = login("3835711724", "123456");
        String getURL = "/orders?page=1&pageSize=5&beginTime=2020-12-09 00:00:00";
        try {
            byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONArray orderList = new JSONArray();
            orderList.add(grouponGroupFailedOrder);
            orderList.add(normalDeliveredOrder);
            orderList.add(grouponDeliveredOrder);
            orderList.add(presaleDeliveredOrder);
            orderList.add(normalFinishedOrder);
            JSONObject responseData = new JSONObject();
            responseData.put("total", 11);
            responseData.put("pages", 3);
            responseData.put("pageSize", 5);
            responseData.put("page", 1);
            responseData.put("list", orderList);
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            response.put("data", responseData);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家查询（查询条件：开始时间为2020-12-11 00:00:00，结束时间为2020-12-13 00:00:00）名下订单（概要） 成功
     * @author snow
     * create 2020/12/15 16:06
     */
    @Test
    public void getOrderBriefInfoSucceedBeginTimeEquals20201211000000AndEndTimeEquals20201214000000() throws Exception {
        String token = login("3835711724", "123456");
        String getURL = "/orders?page=1&pageSize=5&beginTime=2020-12-11 00:00:00&endTime=2020-12-13 00:00:00";
        try {
            byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONArray orderList = new JSONArray();
            orderList.add(normalFinishedOrder);
            orderList.add(grouponFinishedOrder);
            orderList.add(presaleFinishedOrder);
            orderList.add(normalCanceledOrder);
            JSONObject responseData = new JSONObject();
            responseData.put("total", 4);
            responseData.put("pages", 1);
            responseData.put("pageSize", 5);
            responseData.put("page", 1);
            responseData.put("list", orderList);
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            response.put("data", responseData);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家查询（查询条件：订单编号为2020022038350）名下订单（概要） 成功
     * @author snow
     * create 2020/12/15 16:15
     */
    @Test
    public void getOrderBriefInfoSucceedOrderSnEquals2020022038350() throws Exception {
        String token = login("3835711724", "123456");
        String getURL = "/orders?page=1&pageSize=5&orderSn=2020022038350";
        try {
            byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONArray orderList = new JSONArray();
            orderList.add(normalUnpaidNewOrder);
            JSONObject responseData = new JSONObject();
            responseData.put("total", 1);
            responseData.put("pages", 1);
            responseData.put("pageSize", 5);
            responseData.put("page", 1);
            responseData.put("list", orderList);
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            response.put("data", responseData);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家查询（查询条件：订单状态为待收货，页码为1）名下订单（概要） 成功
     * @author snow
     * create 2020/12/15 16:15
     */
    @Test
    public void getOrderBriefInfoSucceedOrderStatusEqualsWaitForReceivingPageEquals1() throws Exception {
        String token = login("3835711724", "123456");
        String getURL = "/orders?page=1&pageSize=5&state=2";
        try {
            byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONArray orderList = new JSONArray();
            orderList.add(normalPaidFinishedOrder);
            orderList.add(grouponPaidFinishedOrder);
            orderList.add(presalePaidFinishedOrder);
            orderList.add(grouponGroupingOrder);
            orderList.add(grouponGroupFailedOrder);
            JSONObject responseData = new JSONObject();
            responseData.put("total", 9);
            responseData.put("pages", 2);
            responseData.put("pageSize", 5);
            responseData.put("page", 1);
            responseData.put("list", orderList);
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            response.put("data", responseData);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家查询（查询条件：订单状态为待收货，页码为2）名下订单（概要） 成功
     * @author snow
     * create 2020/12/15 16:20
     */
    @Test
    public void getOrderBriefInfoSucceedOrderStatusEqualsWaitForReceivingPageEquals2() throws Exception {
        String token = login("3835711724", "123456");
        String getURL = "/orders?page=2&pageSize=5&state=2";
        try {
            byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONArray orderList = new JSONArray();
            orderList.add(normalDeliveredOrder);
            orderList.add(grouponDeliveredOrder);
            orderList.add(presaleDeliveredOrder);
            orderList.add(normalDeliveredOrder2);
            JSONObject responseData = new JSONObject();
            responseData.put("total", 9);
            responseData.put("pages", 2);
            responseData.put("pageSize", 5);
            responseData.put("page", 2);
            responseData.put("list", orderList);
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            response.put("data", responseData);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize() throws JSONException {

        normalUnpaidNewOrder = new JSONObject();
        normalUnpaidNewOrder.put("id", 68050);
        normalUnpaidNewOrder.put("customerId", 356);
        normalUnpaidNewOrder.put("orderType", 0);
        normalUnpaidNewOrder.put("state", 1);
        normalUnpaidNewOrder.put("subState", 11);
        normalUnpaidNewOrder.put("shopId", 1);
        normalUnpaidNewOrder.put("pid", null);
        normalUnpaidNewOrder.put("originalPrice", null);
        normalUnpaidNewOrder.put("discountPrice", null);
        normalUnpaidNewOrder.put("freightPrice", null);
        normalUnpaidNewOrder.put("grouponId", null);
        normalUnpaidNewOrder.put("presaleId", null);
        normalUnpaidNewOrder.put("gmtCreate", "2020-12-01T14:38:35");

        grouponUnpaidNewOrder = new JSONObject();
        grouponUnpaidNewOrder.put("id", 68052);
        grouponUnpaidNewOrder.put("customerId", 356);
        grouponUnpaidNewOrder.put("orderType", 1);
        grouponUnpaidNewOrder.put("state", 1);
        grouponUnpaidNewOrder.put("subState", 11);
        grouponUnpaidNewOrder.put("shopId", 1);
        grouponUnpaidNewOrder.put("pid", null);
        grouponUnpaidNewOrder.put("originalPrice", null);
        grouponUnpaidNewOrder.put("discountPrice", null);
        grouponUnpaidNewOrder.put("freightPrice", null);
        grouponUnpaidNewOrder.put("grouponId", null);
        grouponUnpaidNewOrder.put("presaleId", null);
        grouponUnpaidNewOrder.put("gmtCreate", "2020-12-03T14:38:35");

        presaleUnpaidNewOrder = new JSONObject();
        presaleUnpaidNewOrder.put("id", 68054);
        presaleUnpaidNewOrder.put("customerId", 356);
        presaleUnpaidNewOrder.put("orderType", 2);
        presaleUnpaidNewOrder.put("state", 1);
        presaleUnpaidNewOrder.put("subState", 11);
        presaleUnpaidNewOrder.put("shopId", 1);
        presaleUnpaidNewOrder.put("pid", null);
        presaleUnpaidNewOrder.put("originalPrice", null);
        presaleUnpaidNewOrder.put("discountPrice", null);
        presaleUnpaidNewOrder.put("freightPrice", null);
        presaleUnpaidNewOrder.put("grouponId", null);
        presaleUnpaidNewOrder.put("presaleId", null);
        presaleUnpaidNewOrder.put("gmtCreate", "2020-12-05T14:38:35");

        presaleFinalPaymentUnpaidOrder = new JSONObject();
        presaleFinalPaymentUnpaidOrder.put("id", 68056);
        presaleFinalPaymentUnpaidOrder.put("customerId", 356);
        presaleFinalPaymentUnpaidOrder.put("orderType", 3);
        presaleFinalPaymentUnpaidOrder.put("state", 1);
        presaleFinalPaymentUnpaidOrder.put("subState", 12);
        presaleFinalPaymentUnpaidOrder.put("shopId", 1);
        presaleFinalPaymentUnpaidOrder.put("pid", null);
        presaleFinalPaymentUnpaidOrder.put("originalPrice", null);
        presaleFinalPaymentUnpaidOrder.put("discountPrice", null);
        presaleFinalPaymentUnpaidOrder.put("freightPrice", null);
        presaleFinalPaymentUnpaidOrder.put("grouponId", null);
        presaleFinalPaymentUnpaidOrder.put("presaleId", null);
        presaleFinalPaymentUnpaidOrder.put("gmtCreate", "2020-12-06T15:38:35");

        normalPaidFinishedOrder = new JSONObject();
        normalPaidFinishedOrder.put("id", 68058);
        normalPaidFinishedOrder.put("customerId", 356);
        normalPaidFinishedOrder.put("orderType", 0);
        normalPaidFinishedOrder.put("state", 2);
        normalPaidFinishedOrder.put("subState", 21);
        normalPaidFinishedOrder.put("shopId", 1);
        normalPaidFinishedOrder.put("pid", null);
        normalPaidFinishedOrder.put("originalPrice", null);
        normalPaidFinishedOrder.put("discountPrice", null);
        normalPaidFinishedOrder.put("freightPrice", null);
        normalPaidFinishedOrder.put("grouponId", null);
        normalPaidFinishedOrder.put("presaleId", null);
        normalPaidFinishedOrder.put("gmtCreate", "2020-12-07T14:38:35");

        grouponPaidFinishedOrder = new JSONObject();
        grouponPaidFinishedOrder.put("id", 68060);
        grouponPaidFinishedOrder.put("customerId", 356);
        grouponPaidFinishedOrder.put("orderType", 1);
        grouponPaidFinishedOrder.put("state", 2);
        grouponPaidFinishedOrder.put("subState", 21);
        grouponPaidFinishedOrder.put("shopId", 1);
        grouponPaidFinishedOrder.put("pid", null);
        grouponPaidFinishedOrder.put("originalPrice", null);
        grouponPaidFinishedOrder.put("discountPrice", null);
        grouponPaidFinishedOrder.put("freightPrice", null);
        grouponPaidFinishedOrder.put("grouponId", null);
        grouponPaidFinishedOrder.put("presaleId", null);
        grouponPaidFinishedOrder.put("gmtCreate", "2020-12-07T16:38:35");

        presalePaidFinishedOrder = new JSONObject();
        presalePaidFinishedOrder.put("id", 68062);
        presalePaidFinishedOrder.put("customerId", 356);
        presalePaidFinishedOrder.put("orderType", 2);
        presalePaidFinishedOrder.put("state", 2);
        presalePaidFinishedOrder.put("subState", 21);
        presalePaidFinishedOrder.put("shopId", 1);
        presalePaidFinishedOrder.put("pid", null);
        presalePaidFinishedOrder.put("originalPrice", null);
        presalePaidFinishedOrder.put("discountPrice", null);
        presalePaidFinishedOrder.put("freightPrice", null);
        presalePaidFinishedOrder.put("grouponId", null);
        presalePaidFinishedOrder.put("presaleId", null);
        presalePaidFinishedOrder.put("gmtCreate", "2020-12-07T18:38:35");

        grouponGroupingOrder = new JSONObject();
        grouponGroupingOrder.put("id", 68064);
        grouponGroupingOrder.put("customerId", 356);
        grouponGroupingOrder.put("orderType", 1);
        grouponGroupingOrder.put("state", 2);
        grouponGroupingOrder.put("subState", 22);
        grouponGroupingOrder.put("shopId", 1);
        grouponGroupingOrder.put("pid", null);
        grouponGroupingOrder.put("originalPrice", null);
        grouponGroupingOrder.put("discountPrice", null);
        grouponGroupingOrder.put("freightPrice", null);
        grouponGroupingOrder.put("grouponId", null);
        grouponGroupingOrder.put("presaleId", null);
        grouponGroupingOrder.put("gmtCreate", "2020-12-08T14:38:35");

        grouponGroupFailedOrder = new JSONObject();
        grouponGroupFailedOrder.put("id", 68066);
        grouponGroupFailedOrder.put("customerId", 356);
        grouponGroupFailedOrder.put("orderType", 1);
        grouponGroupFailedOrder.put("state", 2);
        grouponGroupFailedOrder.put("subState", 23);
        grouponGroupFailedOrder.put("shopId", 1);
        grouponGroupFailedOrder.put("pid", null);
        grouponGroupFailedOrder.put("originalPrice", null);
        grouponGroupFailedOrder.put("discountPrice", null);
        grouponGroupFailedOrder.put("freightPrice", null);
        grouponGroupFailedOrder.put("grouponId", null);
        grouponGroupFailedOrder.put("presaleId", null);
        grouponGroupFailedOrder.put("gmtCreate", "2020-12-09T14:38:35");

        normalDeliveredOrder = new JSONObject();
        normalDeliveredOrder.put("id", 68068);
        normalDeliveredOrder.put("customerId", 356);
        normalDeliveredOrder.put("orderType", 0);
        normalDeliveredOrder.put("state", 2);
        normalDeliveredOrder.put("subState", 24);
        normalDeliveredOrder.put("shopId", 1);
        normalDeliveredOrder.put("pid", null);
        normalDeliveredOrder.put("originalPrice", null);
        normalDeliveredOrder.put("discountPrice", null);
        normalDeliveredOrder.put("freightPrice", null);
        normalDeliveredOrder.put("grouponId", null);
        normalDeliveredOrder.put("presaleId", null);
        normalDeliveredOrder.put("gmtCreate", "2020-12-10T14:38:35");

        presaleDeliveredOrder = new JSONObject();
        presaleDeliveredOrder.put("id", 68070);
        presaleDeliveredOrder.put("customerId", 356);
        presaleDeliveredOrder.put("orderType", 1);
        presaleDeliveredOrder.put("state", 2);
        presaleDeliveredOrder.put("subState", 24);
        presaleDeliveredOrder.put("shopId", 1);
        presaleDeliveredOrder.put("pid", null);
        presaleDeliveredOrder.put("originalPrice", null);
        presaleDeliveredOrder.put("discountPrice", null);
        presaleDeliveredOrder.put("freightPrice", null);
        presaleDeliveredOrder.put("grouponId", null);
        presaleDeliveredOrder.put("presaleId", null);
        presaleDeliveredOrder.put("gmtCreate", "2020-12-10T16:38:35");

        grouponDeliveredOrder = new JSONObject();
        grouponDeliveredOrder.put("id", 68072);
        grouponDeliveredOrder.put("customerId", 356);
        grouponDeliveredOrder.put("orderType", 2);
        grouponDeliveredOrder.put("state", 2);
        grouponDeliveredOrder.put("subState", 24);
        grouponDeliveredOrder.put("shopId", 1);
        grouponDeliveredOrder.put("pid", null);
        grouponDeliveredOrder.put("originalPrice", null);
        grouponDeliveredOrder.put("discountPrice", null);
        grouponDeliveredOrder.put("freightPrice", null);
        grouponDeliveredOrder.put("grouponId", null);
        grouponDeliveredOrder.put("presaleId", null);
        grouponDeliveredOrder.put("gmtCreate", "2020-12-10T18:38:35");

        normalFinishedOrder = new JSONObject();
        normalFinishedOrder.put("id", 68074);
        normalFinishedOrder.put("customerId", 356);
        normalFinishedOrder.put("orderType", 0);
        normalFinishedOrder.put("state", 3);
        normalFinishedOrder.put("subState", null);
        normalFinishedOrder.put("shopId", 1);
        normalFinishedOrder.put("pid", null);
        normalFinishedOrder.put("originalPrice", null);
        normalFinishedOrder.put("discountPrice", null);
        normalFinishedOrder.put("freightPrice", null);
        normalFinishedOrder.put("grouponId", null);
        normalFinishedOrder.put("presaleId", null);
        normalFinishedOrder.put("gmtCreate", "2020-12-11T14:38:35");

        grouponFinishedOrder = new JSONObject();
        grouponFinishedOrder.put("id", 68076);
        grouponFinishedOrder.put("customerId", 356);
        grouponFinishedOrder.put("orderType", 1);
        grouponFinishedOrder.put("state", 3);
        grouponFinishedOrder.put("subState", null);
        grouponFinishedOrder.put("shopId", 1);
        grouponFinishedOrder.put("pid", null);
        grouponFinishedOrder.put("originalPrice", null);
        grouponFinishedOrder.put("discountPrice", null);
        grouponFinishedOrder.put("freightPrice", null);
        grouponFinishedOrder.put("grouponId", null);
        grouponFinishedOrder.put("presaleId", null);
        grouponFinishedOrder.put("gmtCreate", "2020-12-11T16:38:35");

        presaleFinishedOrder = new JSONObject();
        presaleFinishedOrder.put("id", 68078);
        presaleFinishedOrder.put("customerId", 356);
        presaleFinishedOrder.put("orderType", 2);
        presaleFinishedOrder.put("state", 3);
        presaleFinishedOrder.put("subState", null);
        presaleFinishedOrder.put("shopId", 1);
        presaleFinishedOrder.put("pid", null);
        presaleFinishedOrder.put("originalPrice", null);
        presaleFinishedOrder.put("discountPrice", null);
        presaleFinishedOrder.put("freightPrice", null);
        presaleFinishedOrder.put("grouponId", null);
        presaleFinishedOrder.put("presaleId", null);
        presaleFinishedOrder.put("gmtCreate", "2020-12-12T14:38:35");

        normalCanceledOrder = new JSONObject();
        normalCanceledOrder.put("id", 68080);
        normalCanceledOrder.put("customerId", 356);
        normalCanceledOrder.put("orderType", 0);
        normalCanceledOrder.put("state", 4);
        normalCanceledOrder.put("subState", null);
        normalCanceledOrder.put("shopId", 1);
        normalCanceledOrder.put("pid", null);
        normalCanceledOrder.put("originalPrice", null);
        normalCanceledOrder.put("discountPrice", null);
        normalCanceledOrder.put("freightPrice", null);
        normalCanceledOrder.put("grouponId", null);
        normalCanceledOrder.put("presaleId", null);
        normalCanceledOrder.put("gmtCreate", "2020-12-12T16:38:35");

        grouponCanceledOrder = new JSONObject();
        grouponCanceledOrder.put("id", 68082);
        grouponCanceledOrder.put("customerId", 356);
        grouponCanceledOrder.put("orderType", 1);
        grouponCanceledOrder.put("state", 4);
        grouponCanceledOrder.put("subState", null);
        grouponCanceledOrder.put("shopId", 1);
        grouponCanceledOrder.put("pid", null);
        grouponCanceledOrder.put("originalPrice", null);
        grouponCanceledOrder.put("discountPrice", null);
        grouponCanceledOrder.put("freightPrice", null);
        grouponCanceledOrder.put("grouponId", null);
        grouponCanceledOrder.put("presaleId", null);
        grouponCanceledOrder.put("gmtCreate", "2020-12-13T14:38:35");

        presaleCanceledOrder = new JSONObject();
        presaleCanceledOrder.put("id", 68084);
        presaleCanceledOrder.put("customerId", 356);
        presaleCanceledOrder.put("orderType", 2);
        presaleCanceledOrder.put("state", 4);
        presaleCanceledOrder.put("subState", null);
        presaleCanceledOrder.put("shopId", 1);
        presaleCanceledOrder.put("pid", null);
        presaleCanceledOrder.put("originalPrice", null);
        presaleCanceledOrder.put("discountPrice", null);
        presaleCanceledOrder.put("freightPrice", null);
        presaleCanceledOrder.put("grouponId", null);
        presaleCanceledOrder.put("presaleId", null);
        presaleCanceledOrder.put("gmtCreate", "2020-12-14T14:38:35");

        normalDeliveredOrder2 = new JSONObject();
        normalDeliveredOrder2.put("id", 68086);
        normalDeliveredOrder2.put("customerId", 356);
        normalDeliveredOrder2.put("orderType", 0);
        normalDeliveredOrder2.put("state", 2);
        normalDeliveredOrder2.put("subState", 24);
        normalDeliveredOrder2.put("shopId", 1);
        normalDeliveredOrder2.put("pid", null);
        normalDeliveredOrder2.put("originalPrice", null);
        normalDeliveredOrder2.put("discountPrice", null);
        normalDeliveredOrder2.put("freightPrice", null);
        normalDeliveredOrder2.put("grouponId", null);
        normalDeliveredOrder2.put("presaleId", null);
        normalDeliveredOrder2.put("gmtCreate", "2020-12-16T11:14:16");

    }


}
