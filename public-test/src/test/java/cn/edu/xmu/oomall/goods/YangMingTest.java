package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;


@SpringBootTest(classes = PublicTestApp.class)
public class YangMingTest {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;


    @BeforeEach
    public void setUp(){
        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .responseTimeout(Duration.ofMillis(40000))
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .responseTimeout(Duration.ofMillis(40000))
                .build();
    }

//    private final String createTestToken(Long userId, Long departId, int expireTime) {
//        String token = new JwtHelper().createToken(userId, departId, expireTime);
//        return token;
//    }

    private String login(String userName, String password) throws Exception {

        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }



    /**
     * skuid不存在
     */
    @Test
    public void customerQueryPresales1() throws Exception {

        byte[] responseString = mallClient.get().uri("/presales?skuId=98765")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        //String str = new String(responseString, "UTF-8");
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":0,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }


    /**
     * skuid不存在
     */
    @Test
    public void adminQueryPresales1() throws Exception {
        String token = this.login("13088admin", "123456");
        //String token = createTestToken(1L, 1L, 100);
        byte[] responseString = manageClient.get().uri("/shops/1/presales?skuId=98765").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":0,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * state字段不合法
     */
    @Test
    public void adminQueryPresales2() throws Exception {
        String token = this.login("13088admin", "123456");
        //String token = createTestToken(1L, 1L, 100);
        byte[] responseString = manageClient.get().uri("/shops/1/presales?state=4").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }



    /**
     * beginTime > EndTime
     */
    @Test
    public void createPresaleOfSKU1() throws Exception {
        String token = this.login("13088admin", "123456");
        //String token = createTestToken(1L, 1L, 100);
        String Json = "{\n" +
                "  \"name\": \"testforcreatePresaleOfSKU\",\n" +
                "  \"advancePayPrice\": 100,\n" +
                "  \"restPayPrice\": 1000,\n" +
                "  \"quantity\": 300,\n" +
                "  \"beginTime\": \"2022-01-09 15:55:18\",\n" +
                "  \"payTime\": \"2022-01-11 15:55:18\",\n" +
                "  \"endTime\": \"2018-01-20 15:55:18\"\n" +
                "}";
        byte[] ret = manageClient.post()
                .uri("/shops/1/skus/3311/presales")
                .header("authorization",token)
                .bodyValue(Json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();

//        String expectedResponse="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(ret, "UTF-8"), true);
    }

    /**
     * EndTime < now
     */
    @Test
    public void createPresaleOfSKU2() throws Exception {
        //String token = createTestToken(1L, 1L, 100);
        String token = this.login("13088admin", "123456");
        String Json = "{\n" +
                "  \"name\": \"testforcreatePresaleOfSKU\",\n" +
                "  \"advancePayPrice\": 100,\n" +
                "  \"restPayPrice\": 1000,\n" +
                "  \"quantity\": 300,\n" +
                "  \"beginTime\": \"2016-01-09 15:55:18\",\n" +
                "  \"payTime\": \"2017-01-11 15:55:18\",\n" +
                "  \"endTime\": \"2018-01-20 15:55:18\"\n" +
                "}";

        byte[] ret = manageClient.post()
                .uri("/shops/1/skus/3311/presales")
                .header("authorization",token)
                .bodyValue(Json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();

//        String expectedResponse="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(ret, "UTF-8"), true);
    }

    /**
     * skuId不存在
     */
    @Test
    public void createPresaleOfSKU3() throws Exception {
        String token = this.login("13088admin", "123456");
        //String token = createTestToken(1L, 1L, 100);
        String Json = "{\n" +
                "  \"name\": \"testforcreatePresaleOfSKU\",\n" +
                "  \"advancePayPrice\": 100,\n" +
                "  \"restPayPrice\": 1000,\n" +
                "  \"quantity\": 300,\n" +
                "  \"beginTime\": \"2022-01-09 15:55:18\",\n" +
                "  \"payTime\": \"2022-01-11 15:55:18\",\n" +
                "  \"endTime\": \"2022-01-20 15:55:18\"\n" +
                "}";

        byte[] ret = manageClient.post()
                .uri("/shops/1/skus/98765/presales")
                .header("authorization",token)
                .bodyValue(Json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

//        String expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(ret, "UTF-8"), true);

    }

    /**
     * skuId存在，但不在此shop中
     */
    @Test
    public void createPresaleOfSKU4() throws Exception {
        String token = this.login("13088admin", "123456");
        //String token = createTestToken(1L, 1L, 100);
        String Json = "{\n" +
                "  \"name\": \"testforcreatePresaleOfSKU\",\n" +
                "  \"advancePayPrice\": 100,\n" +
                "  \"restPayPrice\": 1000,\n" +
                "  \"quantity\": 300,\n" +
                "  \"beginTime\": \"2022-01-09 15:55:18\",\n" +
                "  \"payTime\": \"2022-01-11 15:55:18\",\n" +
                "  \"endTime\": \"2022-01-20 15:55:18\"\n" +
                "}";

        byte[] ret = manageClient.post()
                .uri("/shops/2/skus/3311/presales")
                .header("authorization",token)
                .bodyValue(Json)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

//        String expectedResponse="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(ret, "UTF-8"), true);
    }

    /**
     * 此sku在此时间段内已经参与了其他预售活动
     */
    @Test
    public void createPresaleOfSKU5() throws Exception {
        //String token = createTestToken(1L, 1L, 100);
        String token = this.login("13088admin", "123456");
        String Json = "{\n" +
                "  \"name\": \"testforcreatePresaleOfSKU\",\n" +
                "  \"advancePayPrice\": 100,\n" +
                "  \"restPayPrice\": 1000,\n" +
                "  \"quantity\": 300,\n" +
                "  \"beginTime\": \"2021-01-01 15:55:18\",\n" +
                "  \"payTime\": \"2022-01-10 15:55:18\",\n" +
                "  \"endTime\": \"2023-01-12 15:55:18\"\n" +
                "}";
        byte[] ret = manageClient.post()
                .uri("/shops/1/skus/3311/presales")
                .header("authorization",token)
                .bodyValue(Json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.PRESALE_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();


//        String expectedResponse="{\"errno\":906,\"errmsg\":\"预售活动状态禁止\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(ret, "UTF-8"), true);
    }



    /**
     * state = 已上线，则活动状态禁止修改
     */
    @Test
    public void modifyPresaleofSKU1() throws Exception {

        //String token = createTestToken(1L, 1L, 100);
        String token = this.login("13088admin", "123456");
        String Json= "{\"name\":null,\"advancePayPrice\":null,\"restPayPrice\":null,\"quantity\":null,\"beginTime\":\"2020-12-20 15:55:18\",\"endTime\":\"2022-01-05 15:55:18\",\"payTime\":\"2021-12-20 15:55:18\"}";
        byte[] responseString = manageClient.put().uri("/shops/1/presales/3100").header("authorization",token)
                .bodyValue(Json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.PRESALE_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

//        String expectedResponse="{\"errno\":906,\"errmsg\":\"预售活动状态禁止\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * shopId 无权限操作此 presaleId
     */
    @Test
    public void modifyPresaleofSKU2() throws Exception {
        //String token = createTestToken(1L, 1L, 100);
        String token = this.login("13088admin", "123456");
        String Json= "{\"name\":null,\"advancePayPrice\":null,\"restPayPrice\":null,\"quantity\":null,\"beginTime\":\"2020-12-20 15:55:18\",\"endTime\":\"2022-01-05 15:55:18\",\"payTime\":\"2021-12-20 15:55:18\"}";
        byte[] responseString = manageClient.put().uri("/shops/2/presales/3101").header("authorization",token)
                .bodyValue(Json)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
//        String expectedResponse="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 此预售已被逻辑删除
     */
    @Test
    public void modifyPresaleofSKU3() throws Exception {
        String token = this.login("13088admin", "123456");
        //String token = createTestToken(1L, 1L, 100);
        String Json= "{\"name\":null,\"advancePayPrice\":null,\"restPayPrice\":null,\"quantity\":null,\"beginTime\":\"2020-12-20 15:55:18\",\"endTime\":\"2022-01-05 15:55:18\",\"payTime\":\"2021-12-20 15:55:18\"}";


        byte[] responseString = manageClient.put().uri("/shops/1/presales/3102").header("authorization",token)
                .bodyValue(Json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.PRESALE_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
//        String expectedResponse="{\"errno\":906,\"errmsg\":\"预售活动状态禁止\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 修改的beginTime > endTime
     */
    @Test
    public void modifyPresaleofSKU4() throws Exception {
        //String token = createTestToken(1L, 1L, 100);
        String token = this.login("13088admin", "123456");
        String Json= "{\"name\":null,\"advancePayPrice\":null,\"restPayPrice\":null,\"quantity\":null,\"beginTime\":\"2020-01-20 15:55:18\",\"endTime\":\"2019-01-09 15:55:18\",\"payTime\":\"2020-12-20 15:55:18\"}";

        byte[] responseString = manageClient.put().uri("/shops/1/presales/3103").header("authorization",token)
                .bodyValue(Json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
//        String expectedResponse="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }
//TODO
    /**
     * 修改的endTime早于now
     */
    @Test
    public void modifyPresaleofSKU5() throws Exception {

        //String token = createTestToken(1L, 1L, 100);
        String token = this.login("13088admin", "123456");
        String Json= "{\"name\":null,\"advancePayPrice\":null,\"restPayPrice\":null,\"quantity\":null,\"beginTime\":\"2018-01-20 15:55:18\",\"endTime\":\"2019-01-09 15:55:18\",\"payTime\":\"2018-12-01 15:55:18\"}";

        byte[] responseString = manageClient.put().uri("/shops/1/presales/3104").header("authorization",token)
                .bodyValue(Json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
//        String expectedResponse="{\"errno\":503,\"errmsg\":\"字段不合法\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }



    /**
     * state不为已下线，则无法删除
     */
    @Test
    public void cancelPresaleOfSKU1() throws Exception {
        String token = this.login("13088admin", "123456");
        //String token = createTestToken(1L, 1L, 100);

        byte[] responseString = manageClient.delete().uri("/shops/1/presales/3105").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.PRESALE_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

//        String expectedResponse="{\"errno\":906,\"errmsg\":\"预售活动状态禁止\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);


    }

    /**
     * 此shopId无权操作此presaleId
     */
    @Test
    public void cancelPresaleOfSKU2() throws Exception {
        String token = this.login("13088admin", "123456");
        //String token = createTestToken(1L, 1L, 100);
        byte[] responseString = manageClient.delete().uri("/shops/2/presales/3106").header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
//        String expectedResponse="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }




    /**
     * 若预售状态不为已下线（已上线、已删除），则预售状态不允许上线
     */
    @Test
    public void putPresaleOnShelves1() throws Exception {
        String token = this.login("13088admin", "123456");
        //String token = createTestToken(1L, 1L, 100);
        byte[] responseString = manageClient.put().uri("/shops/1/presales/3107/onshelves").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.PRESALE_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

//        String expectedResponse="{\"errno\":906,\"errmsg\":\"预售活动状态禁止\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 此shopId无权操作此presaleId
     */
    @Test
    public void putPresaleOnShelves2() throws Exception {
        String token = this.login("13088admin", "123456");
        //String token = createTestToken(1L, 1L, 100);
        byte[] responseString = manageClient.put().uri("/shops/2/presales/3108/onshelves").header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
//        String expectedResponse="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }




    /**
     * 若预售状态不为已上线（已下线、已删除），则预售状态不允许下线
     */
    @Test
    public void putPresaleOffShelves1() throws Exception {
        String token = this.login("13088admin", "123456");
        //String token = createTestToken(1L, 1L, 100);
        byte[] responseString = manageClient.put().uri("/shops/1/presales/3109/offshelves").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.PRESALE_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

        // String expectedResponse="{\"errno\":906,\"errmsg\":\"预售活动状态禁止\"}";
        // JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }

    /**
     * 此shopId无权操作此presaleId
     */
    @Test
    public void putPresaleOffShelves2() throws Exception {
        String token = this.login("13088admin", "123456");
        //String token = createTestToken(1L, 1L, 100);
        byte[] responseString = manageClient.put().uri("/shops/2/presales/3110/offshelves").header("authorization",token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

        // String expectedResponse="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        // JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }


}
