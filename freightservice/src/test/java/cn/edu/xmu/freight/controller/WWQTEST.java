package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.model.vo.FreightVo;
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
 * @author wwq
 * @date Created in 2020/12/11 19:15
 **/
@SpringBootTest(classes = FreightServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional //（回滚）
public class WWQTEST {
    @Autowired
    private MockMvc mvc;

    private String content;

    private static final Logger logger = LoggerFactory.getLogger(FreightControllerTest.class);

    /**
     * 创建测试用token
     *
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     * @author 24320182203281 王纬策
     */
    private final String createTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }
    /**
     * 定义重量运费模板
     */
    @Test  //店家新建重量模板明细定义，正确情况
    public void insertWeightFreightTest1() throws Exception{
        WeightFreightVo vo=new WeightFreightVo();
        vo.setRegionId(11L);
        vo.setFirstWeight(5L);
        vo.setFirstWeightFreight(10L);
        vo.setTenPrice(1L);
        vo.setFiftyPrice(1L);
        vo.setHundredPrice(1L);
        vo.setTrihunPrice(1L);
        vo.setAbovePrice(1L);
        String token = createTestToken(3L, 1L, 1000);
        String weightfreightJson = JacksonUtil.toJson(vo);
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
    }

    @Test //重量运费模板中该地区已经定义
    public void insertWeightFreightTest2() throws Exception{
        WeightFreightVo vo=new WeightFreightVo();
        vo.setRegionId(12L);
        vo.setFirstWeight(5L);
        vo.setFirstWeightFreight(10L);
        vo.setTenPrice(1L);
        vo.setFiftyPrice(1L);
        vo.setHundredPrice(1L);
        vo.setTrihunPrice(1L);
        vo.setAbovePrice(1L);
        String token = createTestToken(1L, 0L, 1000);
        String freightJson = JacksonUtil.toJson(vo);
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/freight/shops/1/freightmodels/1/weightItems").header("authorization", token).contentType("application/json;charset=UTF-8").content(freightJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);

    }

    @Test //在件数模板上新建重量，错误404/504
    public void insertWeightFreightTest3() throws Exception{
        WeightFreightVo vo=new WeightFreightVo();
        vo.setRegionId(188L);
        vo.setFirstWeight(5L);
        vo.setFirstWeightFreight(10L);
        vo.setTenPrice(1L);
        vo.setFiftyPrice(1L);
        vo.setHundredPrice(1L);
        vo.setTrihunPrice(1L);
        vo.setAbovePrice(1L);
        String token = createTestToken(1L, 1L, 1000);
        String freightJson = JacksonUtil.toJson(vo);
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/freight/shops/1/freightmodels/2/weightItems").header("authorization", token).contentType("application/json;charset=UTF-8").content(freightJson))
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

    @Test //抵达地区码为空，错误404/503
    public void insertWeightFreightTest4() throws Exception{
        WeightFreightVo vo=new WeightFreightVo();
        //vo.setRegionId(10L);
        vo.setFirstWeight(5L);
        vo.setFirstWeightFreight(10L);
        vo.setTenPrice(1L);
        vo.setFiftyPrice(1L);
        vo.setHundredPrice(1L);
        vo.setTrihunPrice(1L);
        vo.setAbovePrice(1L);
        String token = createTestToken(1L, 1L, 1000);
        String freightJson = JacksonUtil.toJson(vo);
        String expectedResponse = "{\"errno\":503,\"errmsg\":\"抵达地区码不能为空;\"}";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/freight/shops/1/freightmodels/3/weightItems").header("authorization", token).contentType("application/json;charset=UTF-8").content(freightJson))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("response: "+responseString);

    }

    /**
     * 定义运费模板
     */
    @Test //插入成功 插入运费模板
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

    @Test //插入失败 运费模板名重复
    public void insertFreightTest2() throws Exception{
        FreightVo vo=new FreightVo();
        vo.setDefaultModel(false);
        vo.setName("5566");
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

    
}