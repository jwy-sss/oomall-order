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
 * create 2020/12/15 22:12
 * 测试用户：id为356，用户名3835711724，密码123456
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LiDongRuTest {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private String consigneeAddressVoString;

    private WebTestClient manageClient;

    private WebTestClient mallClient;
    @BeforeEach
    public void setUp() throws JSONException {

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        JSONObject testVo = new JSONObject();
        testVo.put("consignee", "陈");
        testVo.put("mobile", "18500000000");
        testVo.put("regionId", 1L);
        testVo.put("address", "福建省厦门市思明区厦大学生公寓");
        this.consigneeAddressVoString = JacksonUtil.toJson(testVo);
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
     * 买家标记（已发货订单）确认收货 成功
     * @author snow
     * create 2020/12/02 09:20
     * modified 2020/12/02 20:16
     */
    @Test
    public void confirmOrderReceivedSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68068L;
        String putURL = "/orders/" + orderId + "/confirm";
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家标记（不属于自己的订单）确认收货 操作的资源id不是自己的对象
     * @author snow
     * create 2020/12/14 10:39
     */
    @Test
    public void confirmOrderReceivedIdOutScope() throws Exception {
        String token = this.login("98970287664", "123456");
        Long orderId = 68086L;
        String putURL = "/orders/" + orderId + "/confirm";
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isForbidden()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 505);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家标记（处于新订单状态的订单）确认收货 订单状态禁止
     * @author snow
     * create 2020/12/02 09:20
     * modified 2020/12/02 20:16
     * modified 2020/12/14 10:44
     */
    @Test
    public void confirmOrderReceivedStatusForbiddenNewOrder() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68050L;
        String putURL = "/orders/" + orderId + "/confirm";
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 801);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家标记（处于待支付尾款状态的订单）确认收货 订单状态禁止
     * @author snow
     * create 2020/12/14 10:50
     */
    @Test
    public void confirmOrderReceivedStatusForbiddenFinalPaymentUnpaid() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68056L;
        String putURL = "/orders/" + orderId + "/confirm";
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 801);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家标记（处于付款完成状态的订单）确认收货 订单状态禁止
     * @author snow
     * create 2020/12/14 10:52
     */
    @Test
    public void confirmOrderReceivedStatusForbiddenPaidFinished() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68058L;
        String putURL = "/orders/" + orderId + "/confirm";
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 801);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家标记（处于待成团状态的订单）确认收货 订单状态禁止
     * @author snow
     * create 2020/12/14 10:53
     */
    @Test
    public void confirmOrderReceivedStatusForbiddenGrouping() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68064L;
        String putURL = "/orders/" + orderId + "/confirm";
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 801);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家标记（处于未成团状态的订单）确认收货 订单状态禁止
     * @author snow
     * create 2020/12/14 10:54
     */
    @Test
    public void confirmOrderReceivedStatusForbiddenGroupFailed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68066L;
        String putURL = "/orders/" + orderId + "/confirm";
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 801);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家标记（不存在的订单）确认收货 操作的资源id不存在
     * @author snow
     * create 2020/12/02 10:06
     * modified 2020/12/02 20:16
     * modified 2020/12/14 10:56
     */
    @Test
    public void confirmOrderReceivedOrderIdNotExist() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 500000L;
        String putURL = "/orders/" + orderId + "/confirm";
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 504);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家标记（已被逻辑删除的订单）确认收货 操作的资源id不存在
     * created By snow 2020/12/02 15:16
     *  modified by snow 2020/12/02 20:17
     */
    @Test
    public void confirmOrderReceivedOrderBeenDeleted() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68069L;
        String putURL = "/orders/" + orderId + "/confirm";
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 504);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家修改名下（处于新订单状态的）订单 成功
     * @author snow
     * create 2020/12/02 13:14
     * modified 2020/12/14 11:20
     */
    @Test
    public void alertOrderAddressNewOrderStatusSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68050L;
        String putURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .bodyValue(this.consigneeAddressVoString)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家修改名下（处于待支付尾款状态的）订单 成功
     * @author snow
     * create 2020/12/02 13:21
     * modified 2020/12/14 11:24
     */
    @Test
    public void alertOrderAddressFinalPaymentUnpaidStatusSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68054L;
        String putURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .bodyValue(this.consigneeAddressVoString)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家修改名下（处于付款完成状态的）订单 成功
     * @author snow
     * create 2020/12/02 13:22
     * modified 2020/12/14 11:25
     */
    @Test
    public void alertOrderAddressPaidFinishedStatusSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68058L;
        String putURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .bodyValue(this.consigneeAddressVoString)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家修改名下（处于待成团状态的）订单 成功
     * @author snow
     * create 2020/12/14 11:26
     */
    @Test
    public void alertOrderAddressGroupingStatusStatusSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68064L;
        String putURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .bodyValue(this.consigneeAddressVoString)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家修改名下（处于未成团状态的）订单 成功
     * @author snow
     * create 2020/12/14 11:27
     */
    @Test
    public void alertOrderAddressGroupFailedStatusSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68066L;
        String putURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .bodyValue(this.consigneeAddressVoString)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家修改名下（处于已发货状态的）订单 订单状态禁止
     * @author snow
     * create 2020/12/02 13:22
     * modified 2020/12/02 20:18
     * modified 2020/12/14 11:28
     */
    @Test
    public void alertOrderAddressStatusForbiddenDelivered() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68086L;
        String putURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .bodyValue(this.consigneeAddressVoString)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 801);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家修改名下（处于已完成状态的）订单 订单状态禁止
     * @author snow
     * create 2020/12/14 11:33
     */
    @Test
    public void alertOrderAddressStatusForbiddenOrderFinished() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68074L;
        String putURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .bodyValue(this.consigneeAddressVoString)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 801);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家修改名下（处于已取消状态的）订单 订单状态禁止
     * @author snow
     * create 2020/12/14 11:34
     */
    @Test
    public void alertOrderAddressStatusForbiddenOrderCanceled() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68080L;
        String putURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .bodyValue(this.consigneeAddressVoString)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 801);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家修改名下（订单id不存在的）订单 操作的资源id不存在
     * @author snow
     * create 2020/12/02 13:22
     * modified 2020/12/02 20:19
     * modified 2020/12/14 11:36
     */
    @Test
    public void alertOrderAddressIdNotExist() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 500000L;
        String putURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .bodyValue(this.consigneeAddressVoString)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 504);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家修改名下（已被逻辑删除的）订单 操作的资源id不存在
     * @author snow
     * create 2020/12/02 15:21
     * modified 2020/12/02 20:19
     * modified 2020/12/14 11:37
     */
    @Test
    public void alertOrderAddressOrderBeenDeleted() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68051L;
        String putURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .bodyValue(this.consigneeAddressVoString)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 504);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家修改名下（不属于自己的）订单 操作的资源id不是自己的对象
     * @author snow
     * create 2020/12/15 23:05
     */
    @Test
    public void alertOrderAddressOrderIdOutScope() throws Exception {
        String token = this.login("98970287664", "123456");
        Long orderId = 68050L;
        String putURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
                    .bodyValue(consigneeAddressVoString)
                    .exchange()
                    .expectStatus().isForbidden()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 505);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家取消（处于新订单状态的）订单 成功
     * @author snow
     * create 2020/12/02 15:50
     * modified 2020/12/14 16:04
     */
    @Test
    public void cancelOrderNewStatusSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68050L;
        String deleteURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家取消（处于付款完成状态的普通）订单 成功
     * @author snow
     * create 2020/12/02 15:53
     * modified 2020/12/14 16:07
     */
    @Test
    public void cancelOrderPaidFinishedStatusNormalOrderSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68058L;
        String deleteURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家取消（处于付款完成状态的团购）订单 成功
     * @author snow
     * create 2020/12/02 15:54
     * modified 2020/12/14 16:09
     */
    @Test
    public void cancelOrderPaidFinishedStatusGrouponOrderSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68060L;
        String deleteURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家取消（处于待成团状态的团购）订单 成功
     * @author snow
     * create 2020/12/14 16:11
     */
    @Test
    public void cancelOrderGroupingStatusGrouponOrderSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68064L;
        String deleteURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家取消（处于未成团状态的团购）订单 成功
     * @author snow
     * create 2020/12/14 16:12
     */
    @Test
    public void cancelOrderGroupFailedStatusGrouponOrderSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68066L;
        String deleteURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家取消（处于待支付尾款状态的预售）订单 订单状态禁止
     * @author snow
     * create 2020/12/02 15:57
     * modified 2020/12/02 20:21
     * modified 2020/12/14 16:16
     */
    @Test
    public void cancelOrderStatusForbiddenFinalPaymentUnpaid() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68056L;
        String deleteURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 801);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家取消（处于已发货状态的）订单 订单状态禁止
     * @author snow
     * create 2020/12/14 16:19
     */
    @Test
    public void cancelOrderStatusForbiddenDelivered() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68086L;
        String deleteURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 801);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家逻辑删除已完成订单 成功
     * @author snow
     * create 2020/12/02 15:55
     * modified 2020/12/14 16:13
     */
    @Test
    public void deleteOrderFinishedStatusSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68074L;
        String deleteURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家逻辑删除已取消订单 成功
     * @author snow
     * create 2020/12/14 16:24
     */
    @Test
    public void deleteOrderCanceledStatusSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68080L;
        String deleteURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家取消/逻辑删除（订单id不存在的）订单 操作的资源id不存在
     * @author snow
     * create 2020/12/02 15:57
     * modified 2020/12/02 20:21
     * modified 2020/12/14 16:20
     */
    @Test
    public void cancelOrDeleteOrderIdNotExist() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 500000L;
        String deleteURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 504);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家取消/逻辑删除（已被逻辑删除的）订单 操作的资源id不存在
     * @author snow
     * create 2020/12/02 15:57
     * modified 2020/12/02 20:21
     * modified 2020/12/14 16:23
     */
    @Test
    public void cancelOrDeleteOrderBeenDeleted() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68073L;
        String deleteURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 504);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家取消/逻辑删除（已被逻辑删除的）订单 操作的资源id不是自己的对象
     * @author snow
     * create 2020/12/15 23:10
     */
    @Test
    public void cancelOrDeleteOrderIdOutScope() throws Exception {
        String token = this.login("98970287664", "123456");
        Long orderId = 68050L;
        String deleteURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isForbidden()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 505);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家将（处于待成团状态的）团购订单转化普通订单 成功
     * @author snow
     * create 2020/12/02 19:54
     * modified 2020/12/14 11:41
     */
    @Test
    public void transferGrouponOrderToNormalOrderGroupingStatusSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68064L;
        String postURL = "/orders/" + orderId + "/groupon-normal";
        try {
            byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家将（处于未成团状态的）团购订单转化普通订单 成功
     * @author snow
     * create 2020/12/02 20:02
     * modified 2020/12/14 11:45
     */
    @Test
    public void transferGrouponOrderToNormalOrderGroupFailedStatusSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68066L;
        String postURL = "/orders/" + orderId + "/groupon-normal";
        try {
            byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家将（处于新订单状态的）团购订单转化普通订单 成功
     * @author snow
     * create 2020/12/02 20:02
     * modified 2020/12/15 10:49
     */
    @Test
    public void transferGrouponOrderToNormalOrderNewOrderStatusSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68052L;
        String postURL = "/orders/" + orderId + "/groupon-normal";
        try {
            byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 0);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家将（已成团的）团购订单转化普通订单 订单状态禁止
     * @author snow
     * create 2020/12/02 20:06
     * modified 2020/12/15 10:36
     */
    @Test
    public void transferGrouponOrderToNormalOrderStatusForbiddenPaidFinished() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68060L;
        String postURL = "/orders/" + orderId + "/groupon-normal";
        try {
            byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 801);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家将普通订单转化普通订单 订单状态禁止
     * @author snow
     * create 2020/12/02 20:10
     * modified 2020/12/15 10:38
     */
    @Test
    public void transferNormalOrderToNormalOrderStatusForbidden() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68058L;
        String postURL = "/orders/" + orderId + "/groupon-normal";
        try {
            byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 801);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家将团购订单转化普通订单 订单状态禁止
     * @author snow
     * create 2020/12/15 10:39
     */
    @Test
    public void transferPresaleOrderToNormalOrderStatusForbidden() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68062L;
        String postURL = "/orders/" + orderId + "/groupon-normal";
        try {
            byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 801);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家将（不存在的）团购订单转化普通订单 操作的资源id不存在
     * @author snow
     * create 2020/12/02 20:11
     * modified 2020/12/15 10:39
     */
    @Test
    public void transferGrouponOrderToNormalOrderIdNotExist() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 500000L;
        String postURL = "/orders/" + orderId + "/groupon-normal";
        try {
            byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 504);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家将（被逻辑删除团购的）订单转化普通订单 操作的资源id不存在
     * @author snow
     * create 2020/12/02 20:14
     * modified 2020/12/15 10:40
     */
    @Test
    public void transferGrouponOrderToNormalOrderBeenDeleted() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68065L;
        String postURL = "/orders/" + orderId + "/groupon-normal";
        try {
            byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 504);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家将（被逻辑删除团购的）订单转化普通订单 操作的资源id不是自己的对象
     * @author snow
     * create 2020/12/15 23:12
     */
    @Test
    public void transferGrouponOrderToNormalOrderIdOutScope() throws Exception {
        String token = this.login("98970287664", "123456");
        Long orderId = 68064L;
        String postURL = "/orders/" + orderId + "/groupon-normal";
        try {
            byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isForbidden()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 505);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家查询订单完整信息（普通，团购，预售） 成功
     * @author snow
     * create 2020/12/03 15:09
     * modified 2020/12/03 17:25
     * modified 2020/12/15 11:26
     */
    @Test
    public void getOrderCompleteInfoSucceed() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68050L;
        String getURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject customerInfo = new JSONObject();
            customerInfo.put("id", 356);
            customerInfo.put("userName", null);
            customerInfo.put("realName", null);
            JSONObject orderItemInfo1 = new JSONObject();
            orderItemInfo1.put("skuId", 680);
            orderItemInfo1.put("orderId", 68050);
            orderItemInfo1.put("name", null);
            orderItemInfo1.put("quantity", 2);
            orderItemInfo1.put("price", 87800);
            JSONObject orderItemInfo2 = new JSONObject();
            orderItemInfo2.put("skuId", 681);
            orderItemInfo2.put("orderId", 68050);
            orderItemInfo2.put("name", null);
            orderItemInfo2.put("quantity", 3);
            orderItemInfo2.put("price", 99900);
            JSONObject orderItemInfo3 = new JSONObject();
            orderItemInfo3.put("skuId", 682);
            orderItemInfo3.put("orderId", 68050);
            orderItemInfo3.put("name", null);
            orderItemInfo3.put("quantity", 4);
            orderItemInfo3.put("price", 12340);
            JSONArray orderItemsInfo = new JSONArray();
            orderItemsInfo.add(orderItemInfo1);
            orderItemsInfo.add(orderItemInfo2);
            orderItemsInfo.add(orderItemInfo3);
            JSONObject responseData = new JSONObject();
            responseData.put("id", 68050);
            responseData.put("shop", null);
            responseData.put("pid", null);
            responseData.put("orderType", 0);
            responseData.put("state", 1);
            responseData.put("subState", 11);
            responseData.put("originPrice", null);
            responseData.put("discountPrice", null);
            responseData.put("freightPrice", null);
            responseData.put("regionId", null);
            responseData.put("couponId", null);
            responseData.put("grouponDiscount", null);
            responseData.put("message", null);
            responseData.put("consignee", "李");
            responseData.put("mobile", "18088888888");
            responseData.put("address", null);
            responseData.put("shop", null);
            responseData.put("customer", customerInfo);
            responseData.put("orderItems", orderItemsInfo);
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
     * 买家查询（不属于自己的）订单完整信息（普通，团购，预售） 操作的资源id不是自己的对象
     * @author snow
     * create 2020/12/15 11:30
     */
    @Test
    public void getOrderCompleteInfoIdOutScope() throws Exception {
        String token = this.login("98970287664", "123456");
        Long orderId = 68050L;
        String getURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isForbidden()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 505);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家查询（不存在的）订单完整信息（普通，团购，预售） 操作的资源id不存在
     * @author snow
     * create 2020/12/03 17:20
     * modified 2020/12/15 11:28
     */
    @Test
    public void getOrderCompleteInfoIdNotExist() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 500000L;
        String getURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 504);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家查询（已被逻辑删除的）订单完整信息（普通，团购，预售） 操作的资源id不存在
     * created By snow 2020/12/03 17:21
     */
    @Test
    public void getOrderCompleteInfoOrderBeenDeleted() throws Exception {
        String token = this.login("3835711724", "123456");
        Long orderId = 68051L;
        String getURL = "/orders/" + orderId;
        try {
            byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            JSONObject response = new JSONObject();
            response.put("errno", 504);
            String expectedResponse = response.toString();
            JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
