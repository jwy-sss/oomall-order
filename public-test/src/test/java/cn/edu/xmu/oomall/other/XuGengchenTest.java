package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * 其他模块测试-广告、时间段服务
 * @author  24320182203305 徐庚辰
 * @date 2020/12/14 10:19
 */

@SpringBootTest(classes = PublicTestApp.class)
public class XuGengchenTest {

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
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    /**
     * 管理员新增广告时间段列表-时间段设置不合法
     *@throws Exception
     * @author 徐庚辰
     **/
    public void postTimeTest2() throws Exception {

        String admintoken = this.adminlogin("13088admin", "123456");
        String roleJson = "{\"beginTime\": \"2021-01-23 11:00:00\",\"endTime\": \"2020-01-20 21:10:27\"}";
        byte[] responseString1 = manageClient.post().uri("/shops/0/advertisement/timesegments")
                .header("authorization", admintoken)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }
    /**
     * 管理员新增广告时间段列表
     *@throws Exception
     * @author 徐庚辰
     **/
    public void postTimeTest1() throws Exception {

        String admintoken = this.adminlogin("13088admin", "123456");
        String roleJson = "{\"beginTime\": \"2021-01-23 11:00:00\",\"endTime\": \"2020-11-28 21:10:27\"}";
        byte[] responseString1 = manageClient.post().uri("/shops/0/advertisement/timesegments")
                .header("authorization", admintoken)
                .bodyValue(roleJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1, new String(responseString1, StandardCharsets.UTF_8), false);


        byte[] responseString2 = manageClient.get().uri("/shops/0/advertisement/advertisement")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"errmsg\":\"成功\"," +
                "\"data\":" +
                "{\"page\":1," +
                "\"pageSize\":10," +
                "\"total\":1," +
                "\"pages\":1," +
                "\"list\":" +
                "[{\"id\":28," +
                "\"beginTime\":\"2020-12-15\","+
                "\"endTime\":\"2021-10-10\","+
                "\"gmtCreate\":\"2020-12-07T21:47:25\","+
                "\"gmtModified\":\"2020-12-07T21:47:25\"}]}}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /**
     * 管理员新增广告时间段列表-登录失败，无权限
     *@throws Exception
     * @author 徐庚辰
     **/
    public void postTimeTest3() throws Exception {

        String admintoken = this.adminlogin("13088admin", "1234");
        String roleJson = "{\"beginTime\": \"2021-01-23 11:00:00\",\"endTime\": \"2020-11-28 21:10:27\"}";
        byte[] responseString1 = manageClient.post().uri("/shops/0/advertisement/timesegments")
                .header("authorization", admintoken)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员新增广告时间段列表-店铺id不存在
     *@throws Exception
     * @author 徐庚辰
     **/
    public void postTimeTest4() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        String roleJson = "{\"beginTime\": \"2021-01-23 11:00:00\",\"endTime\": \"2020-11-28 21:10:27\"}";
        byte[] responseString1 = manageClient.post().uri("/shops/65785/advertisement/timesegments")
                .header("authorization", admintoken)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员获取广告时间段列表-未拥有管理员权限
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void GetTimeTest1() throws Exception {
        String admintoken = this.adminlogin("130276admin", "1234567");
        byte[] responseString2 = manageClient.get().uri("/shops/0/advertisement/timesegments")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员获取广告时间段列表
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void GetTimeTest2() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString2 = manageClient.get().uri("/shops/0/advertisement/timesegments")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"errmsg\":\"成功\"," +
                "\"data\":" +
                "{\"page\":1," +
                "\"pageSize\":10," +
                "\"total\":1," +
                "\"pages\":1," +
                "\"list\":" +
                "[{\"id\":3," +
                "\"beginTime\":\"2021-01-03T10:00:00\","+
                "\"endTime\":\"2021-01-03T16:00:00\","+
                "\"gmtCreate\":\"2020-11-28T21:01:01\","+
                "\"gmtModified\":\"2020-11-28T21:01:01\"}]}}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }

    /**
     * 管理员获取广告时间段列表-店铺id不合法
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void GetTimeTest4() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString2 = manageClient.get().uri("/shops/-1/advertisement/timesegments")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员删除广告时间段-权限不够
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void modifyTimeTest4() throws Exception {
        byte[] responseString1 = manageClient.delete().uri("/shops/0/advertisement/timesegments/27")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员删除广告时间段
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void modifyTimeTest3() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString1 = manageClient.delete().uri("/shops/0/advertisement/timesegments/27")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] responseString2 = manageClient.get().uri("/shops/0/timesegments/27/advertisement")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"errmsg\":\"成功\"," +
                "\"data\":" +
                "{\"page\":0," +
                "\"pageSize\":0," +
                "\"total\":0," +
                "\"pages\":0," +
                "\"list\":null";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);

        //恢复数据库
        String roleJson = "{\"beginTime\": \"2021-01-22 10:00:00\",\"endTime\": \"2020-11-28 21:10:27\"}";
        byte[] responseString = manageClient.post().uri("/shops/0/advertisement/timesegments")
                .header("authorization", admintoken)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员删除广告时间段-店铺不存在
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void modifyTimeTest1() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString1 = manageClient.delete().uri("/shops/7263/advertisement/timesegments/2")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员删除广告时间段-时间段不存在
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void modifyTimeTest2() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString1 = manageClient.delete().uri("/shops/0/advertisement/timesegments/837432")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 获得广告的所有状态
     *@throws Exception
     * @author 徐庚辰
     **/
    @Test
    public void getAdStateTest1() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/advertisement/states")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"," +
                "\"data\":" +
                "[{\"code\":0,\"name\":\"待审核\"}," +
                "{\"code\":4,\"name\":\"上架\"}," +
                "{\"code\":6,\"name\":\"下架\"}]}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 获得广告的所有状态-管理员未登录，无权限
     *@throws Exception
     * @author 徐庚辰
     **/
    @Test
    public void getAdStateTest2() throws Exception {
        byte[] responseString = manageClient.get().uri("/advertisement/states")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员查看某一个时间段的广告,输入均合法，没有符合的条件,返回值为空
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void GetAdTest4() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString2 = manageClient.get().uri("/shops/0/timesegments/4/advertisement?endDate=2080-12-12")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员查看某一个时间段的广告
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void GetAdTest1() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString2 = manageClient.get().uri("/shops/0/timesegments/4/advertisement")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"errmsg\":\"成功\"," +
                "\"data\":" +
                "{\"page\":1," +
                "\"pageSize\":10," +
                "\"total\":1," +
                "\"pages\":1," +
                "\"list\":" +
                "[{\"id\":124," +
                "\"link\":null,"+
                "\"imagePath\":\"http://47.52.88.176/file/images/201610/1475992167803037996.jpg\","+
                "\"content\":null,"+
                "\"segId\":4,"+
                "\"state\":4,"+
                "\"weight\":null,"+
                "\"beDefault\":false,"+
                "\"beginDate\":\"2020-12-15\","+
                "\"endDate\":\"2021-10-10\","+
                "\"repeat\":true,"+
                "\"gmtCreate\":\"2020-12-07T21:47:25\","+
                "\"gmtModified\":\"2020-12-07T21:47:25\"}]}}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }

    /**
     * 管理员查看某一个时间段的广告,页码输入有误
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void GetAdTest5() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString2 = manageClient.get().uri("/shops/0/timesegments/4/advertisement?page=-1")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员查看某一个时间段的广告-时间段不存在
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void GetAdTest2() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString2 = manageClient.get().uri("/shops/0/timesegments/87264/advertisement")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员查看某一个时间段的广告-店铺不存在
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void GetAdTest3() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString2 = manageClient.get().uri("/shops/-2/timesegments/4/advertisement")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员设置默认广告
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void modifyAdTest3() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString1 = manageClient.put().uri("/shops/0/advertisement/122/default")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] responseString2 = manageClient.get().uri("/shops/0/timesegments/2/advertisement")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"errmsg\":\"成功\"," +
                "\"data\":" +
                "{\"page\":1," +
                "\"pageSize\":10," +
                "\"total\":1," +
                "\"pages\":1," +
                "\"list\":" +
                "[{\"id\":122," +
                "\"link\":null,"+
                "\"imagePath\":\"http://47.52.88.176/file/images/201610/1475991949547324589.jpg\","+
                "\"content\":null,"+
                "\"segId\":2,"+
                "\"state\":4,"+
                "\"weight\":null,"+
                "\"beDefault\":true,"+
                "\"beginDate\":\"2020-12-15\","+
                "\"endDate\":\"2021-10-10\","+
                "\"repeat\":true,"+
                "\"gmtCreate\":\"2020-12-07T21:47:25\","+
                "\"gmtModified\":\"2020-12-07T21:47:25\"}]}}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }


    /**
     * 管理员设置默认广告-管理员密码错误，登录失败
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void modifyAdTest4() throws Exception {
        String admintoken = this.adminlogin("13088admin", "1234");
        byte[] responseString1 = manageClient.put().uri("/shops/0/advertisement/122/default")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员设置默认广告-不存在该广告
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void modifyAdTest5() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString1 = manageClient.put().uri("/shops/0/advertisement/872642/default")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 管理员修改广告内容-未登录
     *@throws Exception
     * @author 徐庚辰
     **/
    public void modifyAuthUserTest1() throws Exception {
        String roleJson = "{\"content\": \"加油\",\"beginDate\": \"2020-12-15\",\"endDate\": \"2021-10-10\",\"weight\": 0,\"repeat\": true,\"link\": \"\"}";
        byte[] responseString1 = manageClient.put().uri("/shops/0/advertisement/123")
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     *@throws Exception
     * @author 徐庚辰
     **/
    public void modifyAuthUserTest2() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        String roleJson = "{\"content\": \"加油\",\"beginDate\": \"2020-12-15\",\"endDate\": \"2021-10-10\",\"repeat\": true,\"link\": \"\"}";
        byte[] responseString1 = manageClient.put().uri("/shops/0/advertisement/123")
                .header("authorization", admintoken)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse1 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1, new String(responseString1, StandardCharsets.UTF_8), false);

        String admintoken2 = this.adminlogin("8131600001", "123456");
        byte[] responseString2 = manageClient.get().uri("/shops/0/timesegments/3/advertisement")
                .header("authorization", admintoken2)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"errmsg\":\"成功\"," +
                "\"data\":" +
                "{\"page\":1," +
                "\"pageSize\":10," +
                "\"total\":1," +
                "\"pages\":1," +
                "\"list\":" +
                "[{\"id\":123," +
                "\"link\":null,"+
                "\"imagePath\":\"http://47.52.88.176/file/images/201610/1475991949547324589.jpg\","+
                "\"content\":\"加油\","+
                "\"segId\":3,"+
                "\"state\":4,"+
                "\"weight\":null,"+
                "\"beDefault\":false,"+
                "\"beginDate\":\"2020-12-15\","+
                "\"endDate\":\"2021-10-10\","+
                "\"repeat\":true,"+
                "\"gmtCreate\":\"2020-12-07T21:47:25\","+
                "\"gmtModified\":\"2020-12-07T21:47:25\"}]}}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }

    /**
     * 管理员修改广告内容-输入日期不合法
     *@throws Exception
     * @author 徐庚辰
     **/
    public void modifyAuthUserTest3() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        String roleJson = "{\"content\": \"加油\",\"beginDate\": \"2020-12-15\",\"endDate\": \"2021-12-40\",\"weight\": 0,\"repeat\": true,\"link\": \"\"}";
        byte[] responseString1 = manageClient.put().uri("/shops/0/advertisement/123")
                .header("authorization", admintoken)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容-输入权重不合法
     *@throws Exception
     * @author 徐庚辰
     **/
    public void modifyAuthUserTest4() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        String roleJson = "{\"content\": \"加油\",\"beginDate\": \"2020-12-15\",\"endDate\": \"2021-12-40\",\"weight\": -1,\"repeat\": true,\"link\": \"\"}";
        byte[] responseString1 = manageClient.put().uri("/shops/0/advertisement/123")
                .header("authorization", admintoken)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 管理员删除某一个广告-管理员用户名密码错误
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void deleteTimeTest2() throws Exception {
        String admintoken = this.adminlogin("13023dmin", "123456");
        byte[] responseString1 = manageClient.delete().uri("/shops/0/advertisement/147")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员删除某一个广告-店铺ID不存在
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void deleteTimeTest3() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString1 = manageClient.delete().uri("/shops/5426/advertisement/147")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员删除某一个广告
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void deleteTimeTest1() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString1 = manageClient.delete().uri("/shops/0/advertisement/147")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] responseString2 = manageClient.get().uri("/shops/0/timesegments/27/advertisement")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

        //恢复数据库
        String roleJson = "{\"beginDate\": \"2020-12-15\",\"endDate\": \"2021-10-10\",\"repeat\": true}";
        byte[] responseString = manageClient.post().uri("/shops/0/timesegments/27/advertisement")
                .header("authorization", admintoken)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 管理员删除某一个广告-广告不存在
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void deleteTimeTest4() throws Exception {
        String admintoken = this.adminlogin("13088admin", "123456");
        byte[] responseString1 = manageClient.delete().uri("/shops/0/advertisement/23421")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员删除某一个广告-不是他店铺的广告
     *@throws Exception
     * @author 徐庚辰
     */
    @Test
    public void deleteTimeTest5() throws Exception {
        String admintoken = this.adminlogin("9943200016", "123456");
        byte[] responseString1 = manageClient.delete().uri("/shops/0/advertisement/147")
                .header("authorization", admintoken)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /*买家登录*/
    private String userlogin(String userName, String password) throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = manageClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
    }

    /*管理员登录*/
    private String adminlogin(String userName, String password) throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
    }

}
