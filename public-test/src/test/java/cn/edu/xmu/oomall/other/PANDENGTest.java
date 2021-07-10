package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * 组及相关测试类
 *
 * @author 潘登 24320182203249
 * createdBy 潘登 2020/12/15 12:10
 * modifiedBy 潘登 2020/12/15 22:10
 */
@SpringBootTest(classes = PublicTestApp.class)
public class PANDENGTest {
    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    private static final Logger logger = LoggerFactory.getLogger(PANDENGTest.class);

    /*@Autowired
    private MockMvc mvc;*/


    private String adminLogin(String userName, String password) throws Exception{
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                //.jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }


    @BeforeEach
    public void setUp(){
        manageClient = WebTestClient.bindToServer()
                .baseUrl(managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        mallClient = WebTestClient.bindToServer()
                .baseUrl(mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }


    /**
     * 管理员用id查找售后，成功
     * @throws Exception
     */
    @Test
    public void adminSearchByIdTest1() throws Exception {
        String token = this.adminLogin("537300010", "123456");

        byte[] ret2=this.manageClient.get().uri("/shops/0/aftersales/66")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expected="{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"id\": 66,\n" +
                "        \"orderId\": null,\n" +
                "        \"orderItemId\": 39,\n" +
                "        \"skuId\": 341,\n" +
                "        \"skuName\":\"+\",\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": \"0\",\n" +
                "        \"serviceSn\": null,\n" +
                "        \"type\": 1,\n" +
                "        \"reason\": \"七天无理由\",\n" +
                "        \"refund\": 20,\n" +
                "        \"quantity\": 1,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"厦大学生公寓\",\n" +
                "        \"consignee\": \"Chen\",\n" +
                "        \"mobile\": \"12345678900\",\n" +
                "        \"customerLogSn\": null,\n" +
                "        \"shopLogSn\": null,\n" +
                "        \"state\": 0\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";

        JSONAssert.assertEquals(expected,new String(ret2, "UTF-8"), false);
        //logger.debug(new String(ret2));
    }

    /**
     * 管理员查找自己商店之外的售后
     */
    @Test
    public void adminSearchByIdTest2() throws Exception{
        String token = this.adminLogin("537300010", "123456");

        byte[] ret2=this.manageClient.get().uri("/shops/1/aftersales/51")
                .header("authorization", token)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 管理员查找不存在的售后
     */
    @Test
    public void adminSearchByIdTest3() throws Exception {
        String token = this.adminLogin("537300010", "123456");

        byte[] ret2=this.manageClient.get().uri("/shops/0/aftersales/23333")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 管理员同意售后请求，成功
     * @throws Exception
     */
    @Test
    public void AdminConfirmTest1() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requireJson="{\n" +
                "  \"confirm\":true, \n" +
                "  \"price\": 101,\n" +
                "  \"conclusion\": \"new_conclusion\",\n" +
                "  \"type\": 0\n" +
                "}";

        byte[] ret=this.manageClient.put().uri("/shops/0/aftersales/51/confirm")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        logger.debug(new String(ret));

        byte[] ret2=this.manageClient.get().uri("/shops/0/aftersales/51")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expected="{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"id\": 51,\n" +
                "        \"orderId\": null,\n" +
                "        \"orderItemId\": 39,\n" +
                "        \"skuId\": 341,\n" +
                "        \"skuName\":\"+\",\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": \"0\",\n" +
                "        \"serviceSn\": null,\n" +
                "        \"type\": 0,\n" +
                "        \"reason\": \"七天无理由\",\n" +
                "        \"refund\": 101,\n" +
                "        \"quantity\": 1,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"厦大学生公寓\",\n" +
                "        \"consignee\": \"Chen\",\n" +
                "        \"mobile\": \"12345678900\",\n" +
                "        \"customerLogSn\": null,\n" +
                "        \"shopLogSn\": null,\n" +
                "        \"state\": 1\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";

        JSONAssert.assertEquals(expected,new String(ret2, "UTF-8"), false);
        //logger.debug(new String(ret2));
    }

    /**
     * 管理员不同意售后申请，成功
     * @throws Exception
     */
    @Test
    public void AdminConfirmTest2() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requireJson="{\n" +
                "  \"confirm\":false, \n" +
                "  \"price\": 0,\n" +
                "  \"conclusion\": \"new_conclusion\",\n" +
                "  \"type\": 0\n" +
                "}";

        byte[] ret=this.manageClient.put().uri("/shops/0/aftersales/52/confirm")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        //logger.debug(new String(ret));

        byte[] ret2=this.manageClient.get().uri("/shops/0/aftersales/52")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expected="{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"id\": 52,\n" +
                "        \"orderId\": null,\n" +
                "        \"orderItemId\": 39,\n" +
                "        \"skuId\": 341,\n" +
                "        \"skuName\":\"+\",\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": \"0\",\n" +
                "        \"serviceSn\": null,\n" +
                "        \"type\": 0,\n" +
                "        \"reason\": \"七天无理由\",\n" +
                "        \"refund\": 0,\n" +
                "        \"quantity\": 1,\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"厦大学生公寓\",\n" +
                "        \"consignee\": \"Chen\",\n" +
                "        \"mobile\": \"12345678900\",\n" +
                "        \"customerLogSn\": null,\n" +
                "        \"shopLogSn\": null,\n" +
                "        \"state\": 6\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";

        JSONAssert.assertEquals(expected,new String(ret2, "UTF-8"), false);
        //logger.debug(new String(ret2));
    }

    /**
     * 管理员试图confirm不属于改店铺的售后，失败
     * @throws Exception
     */
    @Test
    public void adminConfirmTest3() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requireJson="{\n" +
                "  \"confirm\":true, \n" +
                "  \"price\": 100,\n" +
                "  \"conclusion\": \"new_conclusion\",\n" +
                "  \"type\": 0\n" +
                "}";

        byte[] ret=this.manageClient.put().uri("/shops/1/aftersales/53/confirm")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
        //logger.debug(new String(ret));
    }

    /**
     * confirm一个不存在的aftersale，失败
     * @throws Exception
     */
    @Test
    public void adminConfirmTest4() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requireJson="{\n" +
                "  \"confirm\":true, \n" +
                "  \"price\": 100,\n" +
                "  \"conclusion\": \"new_conclusion\",\n" +
                "  \"type\": 0\n" +
                "}";

        byte[] ret=this.manageClient.put().uri("/shops/0/aftersales/233/confirm")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        //logger.debug(new String(ret));
    }

    /**
     * 管理员在不正确的状态下confirm，失败
     * @throws Exception
     */
    @Test
    public void adminConfirmTest5() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requireJson="{\n" +
                "  \"confirm\":true, \n" +
                "  \"price\": 100,\n" +
                "  \"conclusion\": \"new_conclusion\",\n" +
                "  \"type\": 0\n" +
                "}";

        byte[] ret=this.manageClient.put().uri("/shops/0/aftersales/55/confirm")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AFTERSALE_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
        //logger.debug(new String(ret));
    }

    /**
     * 店铺收到货物，但是不是本店铺的售后，失败
     * @throws Exception
     */
    @Test
    public void shopReceive1() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requireJson="{\n" +
                "  \"confirm\":true, \n" +
                "  \"conclusion\": \"new_conclusion\"\n" +
                "}";

        byte[] ret=this.manageClient.put().uri("/shops/1/aftersales/56/receive")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店铺收到货物，但是不存在这个售后
     * @throws Exception
     */
    @Test
    public void shopReceive2() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requireJson="{\n" +
                "  \"confirm\":true, \n" +
                "  \"conclusion\": \"new_conclusion\"\n" +
                "}";

        byte[] ret=this.manageClient.put().uri("/shops/0/aftersales/2333/receive")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店铺收货，但是没有处在正确的状态
     * @throws Exception
     */
    @Test
    public void shopReceive3() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requireJson="{\n" +
                "  \"confirm\":true, \n" +
                "  \"conclusion\": \"new_conclusion\"\n" +
                "}";

        byte[] ret=this.manageClient.put().uri("/shops/0/aftersales/57/receive")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AFTERSALE_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货，但是售后id不存在
     * @throws Exception
     */
    @Test
    public void shopSendTest1() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requireJson="{\n" +
                "  \"logSn\":\"20201216xxxx\" \n" +
                "}";
        byte[] ret=this.manageClient.put().uri("/shops/0/aftersales/2333/deliver")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 店家发货，但是商店和售后所对应的商店对不上
     *
     * @throws Exception
     */
    @Test
    public void shopSendTest2() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requireJson="{\n" +
                "  \"logSn\":\"20201216xxxx\" \n" +
                "}";
        byte[] ret=this.manageClient.put().uri("/shops/1/aftersales/59/deliver")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货，但是对应的售后已被删除被删除
     *
     * @throws Exception
     */
    @Test
    public void shopSend3() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requireJson="{\n" +
                "  \"logSn\":\"20201216xxxx\" \n" +
                "}";
        byte[] ret=this.manageClient.put().uri("/shops/0/aftersales/60/deliver")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货，但是状态不允许
     * @throws Exception
     */
    @Test
    public void shopSend4() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        String requireJson="{\n" +
                "  \"logSn\":\"20201216xxxx\" \n" +
                "}";
        byte[] ret=this.manageClient.put().uri("/shops/0/aftersales/61/deliver")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AFTERSALE_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
}
