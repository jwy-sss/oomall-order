package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.model.vo.FreightVo;
import cn.edu.xmu.freight.model.vo.PieceFreightVo;
import cn.edu.xmu.freight.model.vo.WeightFreightVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.freight.FreightServiceApplication;
import org.json.JSONException;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author wxh
 * @date Created in 2020/11/30 21:21
 **/
@SpringBootTest(classes = FreightServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FreightControllerTest {
    @Autowired
    private MockMvc mvc;

    private String content;

    private static final Logger logger = LoggerFactory.getLogger(FreightControllerTest.class);

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

    @Test
    public void changeFreightTest1(){
        String token = createTestToken(1L, 1L, 1000);
        String expectedResponse = "response: {\"errno\":0,\"errmsg\":\"成功\"}";
        String responseString = null;
        FreightVo vo=new FreightVo();
        vo.setName("test");
        vo.setUnit(300);
        String piecefreightJson = JacksonUtil.toJson(vo);
        try {
            responseString = this.mvc.perform(put("/freight/shops/1/freightmodels/1").header("authorization", token).contentType("application/json;charset=UTF-8").content(piecefreightJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    @Test
    public void changeFreightTest2(){
        String token = createTestToken(1L, 0L, 1000);
        String expectedResponse = "response: {\"errno\":0,\"errmsg\":\"成功\"}";
        String responseString = null;
        FreightVo vo=new FreightVo();
        vo.setName("test2");
        vo.setUnit(300);
        String piecefreightJson = JacksonUtil.toJson(vo);
        try {
            responseString = this.mvc.perform(put("/freight/shops/1/freightmodels/1").header("authorization", token).contentType("application/json;charset=UTF-8").content(piecefreightJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    @Test
    public void deleteFreightTest1(){
        String token = createTestToken(1L, 1L, 1000);
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(delete("/freight/shops/1/freightmodels/1").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }
    @Test
    public void deleteFreightTest2(){
        String token = createTestToken(1L, 2L, 1000);
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的不是自己的资源\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(delete("/freight/shops/2/freightmodels/1").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    @Test
    public void GetFreightSimpleTest1(){
        String token = createTestToken(1L, 1L, 1000);
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":2,\"name\":\"2233\",\"type\":1,\"defaultModel\":false,\"gmtCreate\":\"2020-11-26T10:02:35\",\"gmtModified\":\"2020-11-26T21:51:45\",\"unit\":null},\"errmsg\":\"成功\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(get("/freight/freightmodels/2").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);

    }

    @Test
    public void GetFreightSimpleTest2(){
        String token = createTestToken(1L, 0L, 1000);
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":2,\"name\":\"2233\",\"type\":1,\"defaultModel\":false,\"gmtCreate\":\"2020-11-26T10:02:35\",\"gmtModified\":\"2020-11-26T21:51:45\",\"unit\":null},\"errmsg\":\"成功\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(get("/freight/freightmodels/2").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);

    }
    @Test
    public void CreateDefaultTest1(){
        String token = createTestToken(1L, 1L, 1000);
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/freight/shops/1/freight_models/2/default").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }


    @Test
    public void insertPieceFreightTest1() throws Exception{
        //店家新建件数模板明细定义，正确情况
        PieceFreightVo vo=new PieceFreightVo();
        vo.setRegionId(10L);
        vo.setFirstItem(10);
        vo.setFirstItemPrice(2L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String token = createTestToken(1L, 1L, 1000);
        String piecefreightJson = JacksonUtil.toJson(vo);
        String expectedResponse = "errno\":0,\"data\":{\"id\":3,\"freightModelId\":2,\"regionId\":10,\"firstItem\":10,\"firstItemPrice\":2,\"additionalItems\":1,\"additionalItemsPrice\":5,\"gmtCreate\":\"2020-12-10T15:39:44.3959963\",\"gmtModify\":null},\"errmsg\":\"成功\"";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/freight/shops/1/freightmodels/2/pieceItems").header("authorization", token).contentType("application/json;charset=UTF-8").content(piecefreightJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    @Test
    public void insertPieceFreightTest2() throws Exception{
        //管理员新建件数模板明细定义，正确情况
        PieceFreightVo vo=new PieceFreightVo();
        vo.setRegionId(15L);
        vo.setFirstItem(5);
        vo.setFirstItemPrice(1L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String token = createTestToken(1L, 0L, 1000);
        String freightJson = JacksonUtil.toJson(vo);
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":3,\"freightModelId\":2,\"regionId\":15,\"firstItem\":5,\"firstItemPrice\":1,\"additionalItems\":1,\"additionalItemsPrice\":5,\"gmtCreate\":\"2020-12-10T15:47:04.7613746\",\"gmtModify\":null},\"errmsg\":\"成功\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/freight/shops/1/freightmodels/2/pieceItems").header("authorization", token).contentType("application/json;charset=UTF-8").content(freightJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    @Test
    public void insertPieceFreightTest3() throws Exception{
        //在重量模板上新建件数明细，错误404/504
        PieceFreightVo vo=new PieceFreightVo();
        vo.setRegionId(12L);
        vo.setFirstItem(5);
        vo.setFirstItemPrice(1L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String token = createTestToken(1L, 1L, 1000);
        String freightJson = JacksonUtil.toJson(vo);
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源不存在\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/freight/shops/1/freightmodels/1/pieceItems").header("authorization", token).contentType("application/json;charset=UTF-8").content(freightJson))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    @Test
    public void insertPieceFreightTest4() throws Exception{
        //抵达地区码为空，错误404/503
        PieceFreightVo vo=new PieceFreightVo();
        vo.setFirstItem(5);
        vo.setFirstItemPrice(1L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String token = createTestToken(1L, 1L, 1000);
        String freightJson = JacksonUtil.toJson(vo);
        String expectedResponse = "{\"errno\":503,\"errmsg\":\"抵达地区码不能为空;\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/freight/shops/1/freightmodels/2/pieceItems").header("authorization", token).contentType("application/json;charset=UTF-8").content(freightJson))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    @Test
    public void getPieceFreightTest1(){
        String token = createTestToken(1L, 1L, 1000);
        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":1,\"regionId\":1,\"firstItem\":2,\"firstItemPrice\":1,\"additionalItems\":1,\"additionalItemsPrice\":2,\"gmtCreate\":\"2020-11-26T10:02:35\",\"gmtModify\":\"2020-11-26T21:51:45\"},{\"id\":2,\"regionId\":2,\"firstItem\":2,\"firstItemPrice\":1,\"additionalItems\":1,\"additionalItemsPrice\":2,\"gmtCreate\":\"2020-11-26T10:02:35\",\"gmtModify\":\"2020-11-26T21:51:45\"}],\"errmsg\":\"成功\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(get("/freight/shops/1/freightmodels/2/pieceItems").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getPieceFreightTest2(){
        String token = createTestToken(1L, 2L, 1000);
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的不是自己的资源\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(get("/freight/shops/2/freightmodels/2/pieceItems").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    @Test
    public void getPieceFreightTest3(){
        String token = createTestToken(1L, 1L, 1000);
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源不存在\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(get("/freight/shops/1/freightmodels/22/pieceItems").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    @Test
    public void changePieceFreightTest1(){
        String token = createTestToken(1L, 1L, 1000);
        logger.debug(token);
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        String responseString = null;
        PieceFreightVo vo=new PieceFreightVo();
        vo.setRegionId(4L);
        vo.setFirstItem(10);
        vo.setFirstItemPrice(2L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String piecefreightJson = JacksonUtil.toJson(vo);
        try {
            responseString = this.mvc.perform(put("/freight/shops/1/pieceItems/2").header("authorization", token).contentType("application/json;charset=UTF-8").content(piecefreightJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    @Test
    public void changePieceFreightTest2(){
        String token = createTestToken(1L, 1L, 1000);
        logger.debug(token);
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源不存在\"}";
        String responseString = null;
        PieceFreightVo vo=new PieceFreightVo();
        vo.setRegionId(4L);
        vo.setFirstItem(10);
        vo.setFirstItemPrice(2L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String piecefreightJson = JacksonUtil.toJson(vo);
        try {
            responseString = this.mvc.perform(put("/freight/shops/1/pieceItems/33").header("authorization", token).contentType("application/json;charset=UTF-8").content(piecefreightJson))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }
    @Test
    public void changePieceFreightTest3(){
        String token = createTestToken(1L, 2L, 1000);
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的不是自己的资源\"}";
        String responseString = null;
        PieceFreightVo vo=new PieceFreightVo();
        vo.setRegionId(4L);
        vo.setFirstItem(10);
        vo.setFirstItemPrice(2L);
        vo.setAdditionalItems(1);
        vo.setAdditionalItemsPrice(5L);
        String piecefreightJson = JacksonUtil.toJson(vo);
        try {
            responseString = this.mvc.perform(put("/freight/shops/2/pieceItems/2").header("authorization", token).contentType("application/json;charset=UTF-8").content(piecefreightJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    @Test
    public void changePieceFreightTest4(){
        String token = createTestToken(1L, 0L, 1000);
        String expectedResponse = "{\"errno\":803,\"errmsg\":\"运费模板中该地区已经定义：1\"}";
        String responseString = null;
        PieceFreightVo vo=new PieceFreightVo();
        vo.setRegionId(1L);
        vo.setFirstItem(1);
        vo.setFirstItemPrice(2L);
        vo.setAdditionalItems(2);
        vo.setAdditionalItemsPrice(5L);
        String piecefreightJson = JacksonUtil.toJson(vo);
        try {
            responseString = this.mvc.perform(put("/freight/shops/1/pieceItems/2").header("authorization", token).contentType("application/json;charset=UTF-8").content(piecefreightJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    @Test
    public void changePieceFreightTest5(){
        String token = createTestToken(1L, 2L, 1000);
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的不是自己的资源\"}";
        String responseString = null;
        PieceFreightVo vo=new PieceFreightVo();
        vo.setRegionId(14L);
        vo.setFirstItem(1);
        vo.setFirstItemPrice(2L);
        vo.setAdditionalItems(2);
        vo.setAdditionalItemsPrice(5L);
        String piecefreightJson = JacksonUtil.toJson(vo);
        try {
            responseString = this.mvc.perform(put("/freight/shops/2/pieceItems/2").header("authorization", token).contentType("application/json;charset=UTF-8").content(piecefreightJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    @Test
    public void deletePieceFreightTest1(){
        String token = createTestToken(1L, 1L, 1000);
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(delete("/freight/shops/1/pieceItems/2").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    @Test
    public void deletePieceFreightTest2(){
        String token = createTestToken(1L, 0L, 1000);
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源不存在\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(delete("/freight/shops/1/pieceItems/25").header("authorization",token))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);

//        expectedResponse = "{\"errno\":504,\"errmsg\":\"id不存在:5\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, true);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void deletePieceFreightTest3(){
        String token = createTestToken(1L, 2L, 1000);
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的不是自己的资源\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(delete("/freight/shops/2/pieceItems/2").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    //插入成功 插入运费模板
    @Test
    public void insertFreightTest1() throws Exception{
        FreightVo vo=new FreightVo();
        vo.setDefaultModel(false);
        vo.setName("test1");
        vo.setUnit(500);
        vo.setDefaultModel(false);

        String token = createTestToken(1L, 0L, 1000);
        String freightJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/freight/shops/1/freightmodels").header("authorization", token).contentType("application/json;charset=UTF-8").content(freightJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);


    }

    //增加重量运费模板明细
    @Test
    public void insertWeightFreightTest1() throws Exception{
        WeightFreightVo vo=new WeightFreightVo();
        vo.setRegionId(56677L);
        vo.setFirstWeight(5L);
        vo.setFirstWeightFreight(10L);
        vo.setTenPrice(1L);
        vo.setFiftyPrice(1L);
        vo.setHundredPrice(1L);
        vo.setTrihunPrice(1L);
        vo.setAbovePrice(1L);
        String token = createTestToken(1L, 1L, 1000);
        String weightfreightJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/freight/shops/1/freightmodels/1/weightItems").header("authorization", token).contentType("application/json;charset=UTF-8").content(weightfreightJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);


        try {
            responseString = this.mvc.perform(post("/freight/shops/1/freightmodels/1/weightItems").header("authorization", token).contentType("application/json;charset=UTF-8").content(weightfreightJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);

    }

    //增加重量运费模板明细 但是regionId为空 报错404
    @Test
    public void insertWeightFreightTest2() throws Exception{
        WeightFreightVo vo=new WeightFreightVo();
        //regionId不能为空！
        //vo.setRegionId(5L);
        vo.setFirstWeight(5L);
        String token = createTestToken(1L, 0L, 1000);
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/freight/shops/1/freightmodels/1/weightItems").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
//        expectedResponse = "{\"errno\":0,\"data\":{\"id\":88,\"name\":\"test\",\"creatorId\":1,\"describe\":\"test\"},\"errmsg\":\"成功\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    //删除重量运费模板明细
    @Test
    public void deleteWeightFreightTest1(){
        String token = createTestToken(1L, 1L, 1000);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(delete("/freight/shops/1/weightItems/1").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    //查询重量运费模板明细
    @Test
    public void getWeightFreightTest1(){
        String token = createTestToken(1L, 0L, 1000);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(get("/freight/shops/1/freightmodels/1/weightItems").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    //修改重量运费模板
    @Test
    public void changeWeightFreightTest1(){
        String token = createTestToken(1L, 1L, 1000);
        logger.debug(token);
        String expectedResponse = "";
        String responseString = null;
        WeightFreightVo vo=new WeightFreightVo();
        vo.setRegionId(4L);
        vo.setFirstWeight(1L);
        vo.setFirstWeightFreight(1L);
        vo.setTenPrice(1L);
        String weightfreightJson = JacksonUtil.toJson(vo);
        try {
            responseString = this.mvc.perform(put("/freight/shops/1/weightItems/1").header("authorization", token).contentType("application/json;charset=UTF-8").content(weightfreightJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }

    //查询商铺运费模板
    @Test
    public void getAllFreights() throws Exception {
        String responseString=null;
        String token=createTestToken(1L,0L,10000);

        try{
            content=new String(Files.readAllBytes(Paths.get("src/test/resources/getAllFreights")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.debug("content: "+content);

        String responseSuccess=this.mvc.perform(get("/freight/shops/1/freightmodels?page=1&pageSize=3").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: "+responseSuccess);

        JSONAssert.assertEquals(responseSuccess,content,true);
    }

    //克隆运费模板
    @Test
    public void cloneFreightTest1(){
        String token = createTestToken(1L, 0L, 1000);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/freight/shops/1/freightmodels/2/clone").header("authorization",token))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);
    }
}
