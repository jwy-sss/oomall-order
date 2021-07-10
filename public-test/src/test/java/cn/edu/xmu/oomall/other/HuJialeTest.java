package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;


/**
 * 其他模块-时间段测试用例
 * @Author 胡佳乐 24320182203198
 * @Created 2020/12/15
 **/
@SpringBootTest(classes = PublicTestApp.class)
public class HuJialeTest {
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

    private String adminLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] responseString = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(responseString, "UTF-8"), "data");
    }




    /**
     * 1.平台管理员获取广告时间段列表--page、pageSize为空--成功
     **/
    @Test
    @Order(1)
    public void selectAdTimeTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/advertisement/timesegments").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                //"    \"total\": 14,\n" +
                "    \"pages\": 2,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1,\n" +
                "        \"beginTime\": \"2021-01-01T00:00:00\",\n" +
                "        \"endTime\": \"2021-01-01T01:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 2,\n" +
                "        \"beginTime\": \"2021-01-02T01:00:00\",\n" +
                "        \"endTime\": \"2021-01-02T02:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3,\n" +
                "        \"beginTime\": \"2021-01-03T02:00:00\",\n" +
                "        \"endTime\": \"2021-01-03T03:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 4,\n" +
                "        \"beginTime\": \"2021-01-04T03:00:00\",\n" +
                "        \"endTime\": \"2021-01-04T04:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 5,\n" +
                "        \"beginTime\": \"2021-01-05T04:00:00\",\n" +
                "        \"endTime\": \"2021-01-05T05:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 6,\n" +
                "        \"beginTime\": \"2021-01-06T05:00:00\",\n" +
                "        \"endTime\": \"2021-01-06T06:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 7,\n" +
                "        \"beginTime\": \"2021-01-07T06:00:00\",\n" +
                "        \"endTime\": \"2021-01-07T07:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 16,\n" +
                "        \"beginTime\": \"2021-01-15T15:00:00\",\n" +
                "        \"endTime\": \"2021-01-15T16:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:24\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:24\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 17,\n" +
                "        \"beginTime\": \"2021-01-15T16:00:00\",\n" +
                "        \"endTime\": \"2021-01-15T17:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:24\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:24\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 18,\n" +
                "        \"beginTime\": \"2021-01-16T17:00:00\",\n" +
                "        \"endTime\": \"2021-01-16T18:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:24\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:24\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }




    /**
     * 2.平台管理员获取广告时间段列表--page=2&pageSize=5--成功
     **/
    @Test
    @Order(2)
    public void selectAdTimeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/advertisement/timesegments?page=2&pageSize=5").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
               // "    \"total\": 14,\n" +
                "    \"pages\": 3,\n" +
                "    \"pageSize\": 5,\n" +
                "    \"page\": 2,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 6,\n" +
                "        \"beginTime\": \"2021-01-06T05:00:00\",\n" +
                "        \"endTime\": \"2021-01-06T06:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 7,\n" +
                "        \"beginTime\": \"2021-01-07T06:00:00\",\n" +
                "        \"endTime\": \"2021-01-07T07:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 16,\n" +
                "        \"beginTime\": \"2021-01-15T15:00:00\",\n" +
                "        \"endTime\": \"2021-01-15T16:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:24\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:24\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 17,\n" +
                "        \"beginTime\": \"2021-01-15T16:00:00\",\n" +
                "        \"endTime\": \"2021-01-15T17:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:24\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:24\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 18,\n" +
                "        \"beginTime\": \"2021-01-16T17:00:00\",\n" +
                "        \"endTime\": \"2021-01-16T18:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:24\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:24\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 3.平台管理员新增广告时间段--成功
     */
    @Test
    @Order(3)
    public void insertAdTimeTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 22:00:00");
        body.put("endTime", "2020-12-15 22:30:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/advertisement/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 28,\n" +
                "    \"beginTime\": \"2020-12-15T22:00:00\",\n" +
                "    \"endTime\": \"2020-12-15T22:30:00\",\n" +
//                "    \"gmtCreate\": \"2020-12-15T16:15:09.2234023\",\n" +
                "    \"gmtModified\": null\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);


        //重新查询进行验证
        byte[] responseString2 = manageClient.get().uri("/shops/0/advertisement/timesegments?page=2&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                //"    \"total\": 15,\n" +
                "    \"pages\": 2,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 2,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 19,\n" +
                "        \"beginTime\": \"2021-01-17T18:00:00\",\n" +
                "        \"endTime\": \"2021-01-17T19:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:25\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:25\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 20,\n" +
                "        \"beginTime\": \"2021-01-17T19:00:00\",\n" +
                "        \"endTime\": \"2021-01-17T20:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:25\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:25\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 21,\n" +
                "        \"beginTime\": \"2021-01-18T20:00:00\",\n" +
                "        \"endTime\": \"2021-01-18T21:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:26\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:26\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 22,\n" +
                "        \"beginTime\": \"2021-01-18T21:00:00\",\n" +
                "        \"endTime\": \"2021-01-18T21:30:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:26\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:26\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 28,\n" +
                "        \"beginTime\": \"2020-12-15T22:00:00\",\n" +
                "        \"endTime\": \"2020-12-15T22:30:00\",\n" +
//                "        \"gmtCreate\": \"2020-12-15T16:15:09\",\n" +
                "        \"gmtModified\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);

    }


    /**
     * 4.平台管理员新增广告时间段-开始时间为空
     */
    @Test
    @Order(4)
    public void insertAdTimeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "");
        body.put("endTime", "2020-12-15 22:30:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/advertisement/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_BEGIN_NULL.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 611,\n" +
//                "  \"errmsg\": \"开始时间不能为空\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }


    /**
     * 5.平台管理员新增广告时间段-结束时间为空
     */
    @Test
    @Order(5)
    public void insertAdTimeTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 20:00:00");
        body.put("endTime", "");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/advertisement/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_END_NULL.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 612,\n" +
//                "  \"errmsg\": \"结束时间不能为空\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * 6.平台管理员新增广告时间段-开始时间大于结束时间
     */
    @Test
    @Order(6)
    public void insertAdTimeTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 22:00:00");
        body.put("endTime", "2020-12-15 20:00:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/advertisement/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_Bigger.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 610,\n" +
//                "  \"errmsg\": \"开始时间大于结束时间\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * 7.平台管理员新增广告时间段-时段冲突
     */
    @Test
    @Order(7)
    public void insertAdTimeTest5() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 00:00:00");
        body.put("endTime", "2020-12-15 02:00:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/advertisement/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.TIMESEG_CONFLICT.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 604,\n" +
//                "  \"errmsg\": \"时段冲突\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 8.平台管理员删除广告时间段--成功
     */
    @Test
    @Order(8)
    public void deleteAdTimeTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/timesegments/5").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

        //重新查询进行验证
        byte[] responseString2 = manageClient.get().uri("/shops/0/advertisement/timesegments?page=1&pageSize=5").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                //"    \"total\": 14,\n" +
                "    \"pages\": 3,\n" +
                "    \"pageSize\": 5,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1,\n" +
                "        \"beginTime\": \"2021-01-01T00:00:00\",\n" +
                "        \"endTime\": \"2021-01-01T01:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 2,\n" +
                "        \"beginTime\": \"2021-01-02T01:00:00\",\n" +
                "        \"endTime\": \"2021-01-02T02:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3,\n" +
                "        \"beginTime\": \"2021-01-03T02:00:00\",\n" +
                "        \"endTime\": \"2021-01-03T03:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 4,\n" +
                "        \"beginTime\": \"2021-01-04T03:00:00\",\n" +
                "        \"endTime\": \"2021-01-04T04:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 6,\n" +
                "        \"beginTime\": \"2021-01-06T05:00:00\",\n" +
                "        \"endTime\": \"2021-01-06T06:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);

    }


    /**
     * 9.平台管理员删除广告时间段--时间段id不存在
     */
    @Test
    @Order(9)
    public void deleteAdTimeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/timesegments/40").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 504,\n" +
//                "  \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }


    /**
     * 10.平台管理员删除广告时间段--给定的时段id不属于广告时段
     */
    @Test
    @Order(10)
    public void deleteAdTimeTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/timesegments/10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 505,\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }





    /**
     * 11.平台管理员获取秒杀时间段列表--page、pageSize为空--成功
     **/
    @Test
    @Order(11)
    public void selectFsTimeTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/flashsale/timesegments").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                //"    \"total\": 13,\n" +
                "    \"pages\": 2,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 8,\n" +
                "        \"beginTime\": \"2021-01-08T07:00:00\",\n" +
                "        \"endTime\": \"2021-01-08T08:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:02\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:02\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 9,\n" +
                "        \"beginTime\": \"2021-01-09T08:00:00\",\n" +
                "        \"endTime\": \"2021-01-09T09:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:02\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:02\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 10,\n" +
                "        \"beginTime\": \"2021-01-10T09:00:00\",\n" +
                "        \"endTime\": \"2021-01-10T10:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:02\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:02\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 11,\n" +
                "        \"beginTime\": \"2021-01-11T10:00:00\",\n" +
                "        \"endTime\": \"2021-01-11T11:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:02\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:02\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 12,\n" +
                "        \"beginTime\": \"2021-01-12T11:00:00\",\n" +
                "        \"endTime\": \"2021-01-12T12:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:03\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:03\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 13,\n" +
                "        \"beginTime\": \"2021-01-13T12:00:00\",\n" +
                "        \"endTime\": \"2021-01-13T13:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:01:03\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:01:03\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 14,\n" +
                "        \"beginTime\": \"2021-01-14T13:00:00\",\n" +
                "        \"endTime\": \"2021-01-14T14:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:24\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:24\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 15,\n" +
                "        \"beginTime\": \"2021-01-14T14:00:00\",\n" +
                "        \"endTime\": \"2021-01-14T15:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:24\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:24\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 23,\n" +
                "        \"beginTime\": \"2021-01-19T21:30:00\",\n" +
                "        \"endTime\": \"2021-01-19T22:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:26\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:26\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 24,\n" +
                "        \"beginTime\": \"2021-01-19T22:00:00\",\n" +
                "        \"endTime\": \"2021-01-19T22:30:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:26\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:26\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }




    /**
     * 12.平台管理员获取秒杀时间段列表--page=3&pageSize=5--成功
     **/
    @Test
    @Order(12)
    public void selectFsTimeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/flashsale/timesegments?page=3&pageSize=5").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                //"    \"total\": 13,\n" +
                "    \"pages\": 3,\n" +
                "    \"pageSize\": 5,\n" +
                "    \"page\": 3,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 25,\n" +
                "        \"beginTime\": \"2021-01-20T22:30:00\",\n" +
                "        \"endTime\": \"2021-01-20T23:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:27\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:27\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 26,\n" +
                "        \"beginTime\": \"2021-01-21T23:00:00\",\n" +
                "        \"endTime\": \"2021-01-21T23:30:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:27\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:27\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 27,\n" +
                "        \"beginTime\": \"2021-01-22T23:30:00\",\n" +
                "        \"endTime\": \"2021-01-22T00:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:27\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:27\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 13.平台管理员新增秒杀时间段--成功
     */
    @Test
    @Order(13)
    public void insertFsTimeTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 00:00:00");
        body.put("endTime", "2020-12-15 00:01:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/flashsale/timesegments").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 29,\n" +
                "    \"beginTime\": \"2020-12-15T00:00:00\",\n" +
                "    \"endTime\": \"2020-12-15T00:01:00\",\n" +
//                "    \"gmtCreate\": \"2020-12-15T19:43:08.0384696\",\n" +
                "    \"gmtModified\": null\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);


        //重新查询进行验证
        byte[] responseString2 = manageClient.get().uri("/shops/0/flashsale/timesegments?page=2&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                //"    \"total\": 14,\n" +
                "    \"pages\": 2,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 2,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 25,\n" +
                "        \"beginTime\": \"2021-01-20T22:30:00\",\n" +
                "        \"endTime\": \"2021-01-20T23:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:27\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:27\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 26,\n" +
                "        \"beginTime\": \"2021-01-21T23:00:00\",\n" +
                "        \"endTime\": \"2021-01-21T23:30:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:27\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:27\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 27,\n" +
                "        \"beginTime\": \"2021-01-22T23:30:00\",\n" +
                "        \"endTime\": \"2021-01-22T00:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:27\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:27\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 29,\n" +
                "        \"beginTime\": \"2020-12-15T00:00:00\",\n" +
                "        \"endTime\": \"2020-12-15T00:01:00\",\n" +
//                "        \"gmtCreate\": \"2020-12-15T19:43:08\",\n" +
                "        \"gmtModified\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);

    }


    /**
     * 14.平台管理员新增秒杀时间段-开始时间为空
     */
    @Test
    @Order(14)
    public void insertFsTimeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "");
        body.put("endTime", "2020-12-15 00:01:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/flashsale/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_BEGIN_NULL.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 611,\n" +
//                "  \"errmsg\": \"开始时间不能为空\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }


    /**
     * 15.平台管理员新增秒杀时间段-结束时间为空
     */
    @Test
    @Order(15)
    public void insertFsTimeTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 00:00:00");
        body.put("endTime", "");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/flashsale/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_END_NULL.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 612,\n" +
//                "  \"errmsg\": \"结束时间不能为空\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * 16.平台管理员新增秒杀时间段-开始时间大于结束时间
     */
    @Test
    @Order(16)
    public void insertFsTimeTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 01:30:00");
        body.put("endTime", "2020-12-15 00:30:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/flashsale/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_Bigger.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 610,\n" +
//                "  \"errmsg\": \"开始时间大于结束时间\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * 17.平台管理员新增秒杀时间段-时段冲突
     */
    @Test
    @Order(17)
    public void insertFsTimeTest5() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 10:00:00");
        body.put("endTime", "2020-12-15 10:10:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/flashsale/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.TIMESEG_CONFLICT.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 604,\n" +
//                "  \"errmsg\": \"时段冲突\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 18.平台管理员删除秒杀时间段--成功
     */
    @Test
    @Order(18)
    public void deleteFsTimeTest1() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/flashsale/timesegments/25").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

        //重新查询进行验证
        byte[] responseString2 = manageClient.get().uri("/shops/0/flashsale/timesegments?page=2&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                //"    \"total\": 13,\n" +
                "    \"pages\": 2,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 2,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 26,\n" +
                "        \"beginTime\": \"2021-01-21T23:00:00\",\n" +
                "        \"endTime\": \"2021-01-21T23:30:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:27\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:27\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 27,\n" +
                "        \"beginTime\": \"2021-01-22T23:30:00\",\n" +
                "        \"endTime\": \"2021-01-22T00:00:00\",\n" +
                "        \"gmtCreate\": \"2020-11-28T21:10:27\",\n" +
                "        \"gmtModified\": \"2020-11-28T21:10:27\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 29,\n" +
                "        \"beginTime\": \"2020-12-15T00:00:00\",\n" +
                "        \"endTime\": \"2020-12-15T00:01:00\",\n" +
//                "        \"gmtCreate\": \"2020-12-15T19:43:08\",\n" +
                "        \"gmtModified\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);

    }


    /**
     * 19.平台管理员删除秒杀时间段--时间段id不存在
     */
    @Test
    @Order(19)
    public void deleteFsTimeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/flashsale/timesegments/40").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 504,\n" +
//                "  \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }


    /**
     * 20.平台管理员删除广告时间段--给定的时段id不属于秒杀时段
     */
    @Test
    @Order(20)
    public void deleteFsTimeTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/flashsale/timesegments/1").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 505,\n" +
//                "  \"errmsg\": \"操作的资源id不是自己的对象\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }



}
