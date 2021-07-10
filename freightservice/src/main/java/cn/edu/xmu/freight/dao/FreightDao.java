package cn.edu.xmu.freight.dao;

import cn.edu.xmu.freight.mapper.FreightModelPoMapper;
import cn.edu.xmu.freight.mapper.PieceFreightModelPoMapper;
import cn.edu.xmu.freight.mapper.WeightFreightModelPoMapper;
import cn.edu.xmu.freight.model.bo.Freight;
import cn.edu.xmu.freight.model.bo.PieceFreight;
import cn.edu.xmu.freight.model.bo.WeightFreight;
import cn.edu.xmu.freight.model.po.*;
import cn.edu.xmu.freight.model.vo.FreightVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.provider.server.OtherForOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import service.FreightForOtherService;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

@Repository
public class FreightDao {

    private  static  final Logger logger = LoggerFactory.getLogger(FreightDao.class);

    @Autowired
    private FreightModelPoMapper freightModelPoMapper;

    @Autowired
    private PieceFreightModelPoMapper pieceFreightModelPoMapper;

    @Autowired
    private WeightFreightModelPoMapper weightFreightModelPoMapper;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @DubboReference(check = false)
    private OtherForOrderService otherForOrderService;

    /**
     * 定义运费模板
     *
     * @author wwq
     * @param  freight 运费模板bo
     * @return ReturnObject<Role> 新增结果
     * createdBy wwq 2020/12/1 14:47
     * modifiedBy wwq
     */
    public ReturnObject<VoObject> insertFreight(Freight freight) {
        //先由bo创建po
        FreightModelPo freightModelPo = freight.gotFreightPo();
        ReturnObject<VoObject> retObj = null;
        if ((freightModelPo.getType()!=1)&&(freightModelPo.getType()!=0)){
            logger.debug("insertFreight: insert freight failed");
            retObj = new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("字段不合法：" + freightModelPo.getType()));
            return retObj;
        }
        FreightModelPoExample example=new FreightModelPoExample();
        FreightModelPoExample.Criteria criteria= example.createCriteria();
        //criteria.andShopIdEqualTo(freightModelPo.getShopId());
        criteria.andNameEqualTo(freightModelPo.getName());
        List<FreightModelPo> FreightModelPos=freightModelPoMapper.selectByExample(example);
        if (FreightModelPos.size()!=0){
            logger.debug("insertFreight: insert freight failed");
            retObj = new ReturnObject<>(ResponseCode.FREIGHTNAME_SAME, String.format("运费模板名重复：" + freightModelPo.getName()));
            return retObj;
        }
        try{
            int ret = freightModelPoMapper.insertSelective(freightModelPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertFreight: insert freight fail " + freightModelPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + freightModelPo.getId()));
            } else {
                //插入成功
                logger.debug("insertFreight: insert freight = " + freightModelPo.toString());
                freight.setId(freightModelPo.getId());
                retObj = new ReturnObject<>(freight);
            }
        }
        catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * 获得运费模板概要。
     * @param departId: 店铺id
     * @param id:  运费明细id
     * @return Object
     */
    public ReturnObject<VoObject> getFreightModelSimple(Long id,Long departId){
        try {
            FreightModelPo po=freightModelPoMapper.selectByPrimaryKey(id);
            if(po!=null){
                Freight freight=new Freight(po);
                return new ReturnObject<>(freight);
            }else{
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("id不存在: id="+id));
            }

        }
        catch (DataAccessException e){
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e){
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }

    }

    /**
     *
     */
    public ReturnObject changeFreight(Freight freight){
        FreightModelPo po=freight.gotFreightPo();
        ReturnObject retObj=null;
        if(po.getName()!=null){
            FreightModelPoExample example=new FreightModelPoExample();
            FreightModelPoExample.Criteria criteria= example.createCriteria();
            criteria.andNameEqualTo(po.getName());
            criteria.andShopIdEqualTo(po.getShopId());
            List<FreightModelPo> pieceFreightModelPos=freightModelPoMapper.selectByExample(example);
            if (pieceFreightModelPos.size()>0){
                logger.debug("changeFreight: change freight failed");
                retObj = new ReturnObject<>(ResponseCode.FREIGHTNAME_SAME, String.format("运费模板名重复" + po.getName()));
                return retObj;
            }
        }

        try{
            int ret=freightModelPoMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                //修改失败
                logger.debug("changeFreight: update freight fail : " + po.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("件数模板id不存在：" +po.getId()));
            } else {
                //修改成功
                logger.debug("changeFreight: update freight = " + po.toString());
                retObj = new ReturnObject<>();
            }
        }
        catch (DataAccessException e){
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

        }
        catch (Exception e){
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * 删除运费模板，需同步删除与商品的
     * @param id
     * @param shopId
     * @return ReturnObject<Object>
     * @author wxh 2020/12/2 16:45
     */
    public ReturnObject<Object> deleteFreight(Long id, Long shopId){
        //商品删除未完成
        ReturnObject<Object> returnObject=null;
        try {
            FreightModelPo po = freightModelPoMapper.selectByPrimaryKey(id);

            if (po.getType() == 1) {
                PieceFreightModelPoExample example = new PieceFreightModelPoExample();
                PieceFreightModelPoExample.Criteria criteria = example.createCriteria();
                criteria.andFreightModelIdEqualTo(id);
                int del = pieceFreightModelPoMapper.deleteByExample(example);
                if (del != 0) {
                List<PieceFreightModelPo> list=pieceFreightModelPoMapper.selectByExample(example);
                    logger.debug("件数模板明细级联删除完成");
                }
            } else {
                WeightFreightModelPoExample example = new WeightFreightModelPoExample();
                WeightFreightModelPoExample.Criteria criteria = example.createCriteria();
                criteria.andFreightModelIdEqualTo(id);
                int del = weightFreightModelPoMapper.deleteByExample(example);
                if (del != 0) {
                    logger.debug("重量模板明细级联删除完成");
                }
            }
            int ret= freightModelPoMapper.deleteByPrimaryKey(id);
            if(ret==0){
                logger.debug("deleteFreight: id not exist ="+id);
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("id不存在：" + id));
            }else {
                logger.debug("deleteFreight: delete piecefreight id ="+ id);
                returnObject=new ReturnObject<>();
            }
        }catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject;
    }

    /**
     * 店家或管理员查找商铺默认运费模板
     * @param shopId
     * @return int
     * @author wxh
     * @date 2020/12/2 1707
     */
    public Long findDefaultFreightModelId(Long shopId){
        Long defaultFreightModelId=0L;
        FreightModelPoExample example=new FreightModelPoExample();
        FreightModelPoExample.Criteria criteria= example.createCriteria();
        criteria.andDefaultModelEqualTo((byte)1);
        criteria.andShopIdEqualTo(shopId);
        List<FreightModelPo> poList=freightModelPoMapper.selectByExample(example);
        if(poList.size()==1){
            defaultFreightModelId= poList.get(0).getId();
        }
        else if(poList.size()>1){
            defaultFreightModelId= poList.get(0).getId();
            logger.debug("该店铺下的默认运费模板有多个，以第一个为准，请检查数据库数据");
        }
        return defaultFreightModelId;
    }

    /**
     *管理员定义重量模板明细
     *
     * @param weightFreight 件数模板明细bo
     * @return ReturnObject<WeightFreight>
     * @author wwq
     * @date Created in 2020/11/28 19:12
     * Modified in  2020/12/1 15:46
     */
    public ReturnObject<WeightFreight> insertWeightFreight(WeightFreight weightFreight){
        WeightFreightModelPo po=weightFreight.gotWeightFreightPo();
        if(freightModelPoMapper.selectByPrimaryKey(po.getFreightModelId()).getType()==1){
            logger.debug("运费模板类型不匹配");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("操作的资源不存在"));
        }
        if(otherForOrderService.getParentRegionIdByson(po.getRegionId())<0){
            logger.debug("该地区不可达");
            return new ReturnObject<>(ResponseCode.REGION_NOT_REACH,String.format("该地区不可达"));
        }
        ReturnObject<WeightFreight> retObj=null;
        WeightFreightModelPoExample example=new WeightFreightModelPoExample();
        WeightFreightModelPoExample.Criteria criteria= example.createCriteria();
        criteria.andRegionIdEqualTo(po.getRegionId());
        criteria.andFreightModelIdEqualTo(po.getFreightModelId());
        List<WeightFreightModelPo> weightFreightModelPos=weightFreightModelPoMapper.selectByExample(example);
        if (weightFreightModelPos.size()!=0){
            logger.debug("insertWeightFreight: insert weightfreight failed");
            retObj = new ReturnObject<>(ResponseCode.REGION_SAME, String.format("运费模板中该地区已经定义：" + po.getRegionId()));
            return retObj;
        }
        try {
            int ret=weightFreightModelPoMapper.insertSelective(po);
            if(ret==0){
                logger.debug("insertWeightFreight: insert weightfreight failed");
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + po.getId()));
            } else{
                logger.debug("insertWeightFreight: insert weightfreight = " + po.toString());
                weightFreight.setId(po.getId());
                retObj=new ReturnObject<>(weightFreight);
            }
        }
        catch (DataAccessException e){
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

        }
        catch (Exception e){
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * 店家或管理员删掉重量运费模板明细
     *
     * @param id
     * @param shopId
     * @author wwq
     * @date Created in 2020/11/28 23:15
     * Modified in  2020/12/1 15:58
     */
    public ReturnObject<Object> deleteWeightFreight(Long id, Long shopId){
        ReturnObject<Object> returnObject=null;
        //修改
        WeightFreightModelPo wfPo=weightFreightModelPoMapper.selectByPrimaryKey(id);
        FreightModelPo freightModelPo=freightModelPoMapper.selectByPrimaryKey(wfPo.getFreightModelId());
        if(wfPo==null){
            logger.debug("getFreight failed: id不存在。 id = "+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("操作的资源不存在"));
        }
        if((!freightModelPo.getShopId().equals(shopId))&&(shopId!=0L)){
            logger.debug("getFreight failed: id不存在。 id = "+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("操作的不是自己的资源"));
        }
        //修改
        int ret= weightFreightModelPoMapper.deleteByPrimaryKey(id);
        if(ret==0){
            logger.debug("deleteWeightFreight: id not exist ="+id);
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("id不存在：" + id));
        }else {
            logger.debug("deleteWeightFreight: delete weightfreight id ="+ id);
            returnObject=new ReturnObject<>();
        }
        return returnObject;
    }

    /**
     * 店家或管理员查询重量运费模板的明细。
     *
     * @param id
     * @param shopId
     * @return ReturnObject<List<VoObject>>
     *
     * @author wwq
     * @date Created in 2020/11/29 09:42
     */
    public ReturnObject<List> getWeightFreight(Long id,Long shopId){
        List<VoObject> weightfreights=null;
        WeightFreightModelPoExample example=new WeightFreightModelPoExample();
        WeightFreightModelPoExample.Criteria criteria=example.createCriteria();
        criteria.andFreightModelIdEqualTo(id);
        try {
            List<WeightFreightModelPo> weightFreightModelPos = weightFreightModelPoMapper.selectByExample(example);
            if (weightFreightModelPos.size() == 0) {
                logger.debug("getWeightFreight failed: id不存在。 id = " + id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("操作的资源不存在"));
            } else {
                weightfreights = new ArrayList<>(weightFreightModelPos.size());
                for (WeightFreightModelPo po : weightFreightModelPos) {
                    WeightFreight weightFreight = new WeightFreight(po);
                    weightfreights.add(weightFreight);
                }
            }
            return new ReturnObject<>(weightfreights);
        }
        catch (DataAccessException e){
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

        }
        catch (Exception e){
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 店家或管理员修改重量运费模板明细
     * @author wwq
     * @date Created in 2020/12/1 15:59
     */
    public ReturnObject<WeightFreight> changeWeightFreight(WeightFreight weightFreight){
        WeightFreightModelPo po=weightFreight.gotWeightFreightPo();
        WeightFreightModelPo orig=weightFreightModelPoMapper.selectByPrimaryKey(po.getId());
        if (orig == null) {
            logger.info("运费模板不存在或已被删除：id = " + po.getId());
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        ReturnObject<WeightFreight> retObj=null;

        if(po.getRegionId()!=null){
            if(otherForOrderService.getParentRegionIdByson(po.getRegionId())<0){
                logger.debug("该地区不可达");
                return new ReturnObject<>(ResponseCode.REGION_NOT_REACH,String.format("该地区不可达"));
            }
            WeightFreightModelPoExample example=new WeightFreightModelPoExample();
            WeightFreightModelPoExample.Criteria criteria= example.createCriteria();
            criteria.andRegionIdEqualTo(po.getRegionId());
            criteria.andIdNotEqualTo(po.getId());
            criteria.andFreightModelIdEqualTo(orig.getFreightModelId());
            List<WeightFreightModelPo> weightFreightModelPos=weightFreightModelPoMapper.selectByExample(example);
            if (weightFreightModelPos.size()!=0){
                logger.debug("changeWeightFreight: change weightfreight failed");
                retObj = new ReturnObject<>(ResponseCode.REGION_SAME, String.format("运费模板中该地区已经定义：" + po.getRegionId()));
                return retObj;
            }
        }


        try {
            int ret=weightFreightModelPoMapper.updateByPrimaryKeySelective(po);

            if (ret == 0) {
                //修改失败
                logger.debug("updateWeightFreight: update weightfreight fail : " + po.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("运费模板id不存在：" +po.getId()));
            } else {
                //修改成功
                logger.debug("updateWeightFreight: update weightfreight = " + po.toString());
                retObj = new ReturnObject<>();
            }
        }
        catch (DataAccessException e){
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

        }
        catch (Exception e){
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        WeightFreightModelPo result=weightFreightModelPoMapper.selectByPrimaryKey(po.getId());
        return retObj;
    }

    /**
     *管理员定义件数模板明细
     *
     * @param pieceFreight 件数模板明细bo
     * @return ReturnObject<PieceFreight>
     * @author wxh
     * @date Created in 2020/11/27 10:39
     * Modified in  2020/12/1 9:08
     */
    public ReturnObject<PieceFreight> insertPieceFreight(PieceFreight pieceFreight){

        PieceFreightModelPo po=pieceFreight.gotPieceFreightPo();
        if(freightModelPoMapper.selectByPrimaryKey(po.getFreightModelId()).getType()==0){
            logger.debug("运费模板类型不匹配");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("操作的资源不存在"));
        }
        if(otherForOrderService.getParentRegionIdByson(po.getRegionId())<0){
            logger.debug("该地区不可达");
            return new ReturnObject<>(ResponseCode.REGION_NOT_REACH,String.format("该地区不可达"));
        }
        ReturnObject<PieceFreight> retObj=null;
        PieceFreightModelPoExample example=new PieceFreightModelPoExample();
        PieceFreightModelPoExample.Criteria criteria= example.createCriteria();
        criteria.andRegionIdEqualTo(po.getRegionId());
        criteria.andFreightModelIdEqualTo(po.getFreightModelId());
        List<PieceFreightModelPo> pieceFreightModelPos=pieceFreightModelPoMapper.selectByExample(example);
        if (pieceFreightModelPos.size()!=0){
            logger.debug("insertPieceFreight: insert piecefreight failed");
            retObj = new ReturnObject<>(ResponseCode.REGION_SAME, String.format("运费模板中该地区已经定义：" + po.getRegionId()));
            return retObj;
        }
        try {
            int ret=pieceFreightModelPoMapper.insertSelective(po);
            if(ret==0){
                logger.debug("insertPieceFreight: insert piecefreight failed");
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + po.getId()));
            } else{
                logger.debug("insertPieceFreight: insert piecefreight = " + po.toString());
                pieceFreight.setId(po.getId());
                retObj=new ReturnObject<>(pieceFreight);
            }
        }
        catch (DataAccessException e){
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

        }
        catch (Exception e){
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * 店家或管理员删掉件数运费模板明细
     *
     * @param id
     * @param shopId
     * @author wxh
     * @date Created in 2020/11/29 11:28
     */
    public ReturnObject<Object> deletePieceFreight(Long id, Long shopId){
        ReturnObject<Object> returnObject=null;
        int ret= pieceFreightModelPoMapper.deleteByPrimaryKey(id);
        if(ret==0){
            logger.debug("deletePieceFreight: id not exist ="+id);
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("id不存在：" + id));
        }else {
            logger.debug("deletePieceFreight: delete piecefreight id ="+ id);
            returnObject=new ReturnObject<>();
        }
        return returnObject;
    }

    /**
     * 店家或管理员查询件数运费模板的明细。
     *
     * @param id
     * @param shopId
     * @return ReturnObject<List<VoObject>>
     *
     * @author wxh
     * @date Created in 2020/11/29 16:23
     * Modified in 2020/12/10 12:45
     */
    public ReturnObject<List> getPieceFreight(Long id,Long shopId){
        //查找详细信息
        List<VoObject> piecefreights=null;
        PieceFreightModelPoExample example=new PieceFreightModelPoExample();
        PieceFreightModelPoExample.Criteria criteria=example.createCriteria();
        criteria.andFreightModelIdEqualTo(id);
        try {
            List<PieceFreightModelPo> pieceFreightModelPos=pieceFreightModelPoMapper.selectByExample(example);
            if(pieceFreightModelPos.size()==0){
                logger.debug("getPieceFreight failed: id不存在。 id = "+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("操作的资源不存在"));
            }else {
                piecefreights=new ArrayList<>(pieceFreightModelPos.size());
                for (PieceFreightModelPo po:pieceFreightModelPos){
                    PieceFreight pieceFreight=new PieceFreight(po);
                    piecefreights.add(pieceFreight);
                }
            }
            return new ReturnObject<>(piecefreights);
        }
        catch (DataAccessException e){
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

        }
        catch (Exception e){
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 店家或管理员修改件数运费模板明细
     * @author wxh
     * @date Created in 2020/12/1 10:59
     */
    public ReturnObject<PieceFreight> changePieceFreight(PieceFreight pieceFreight){
        PieceFreightModelPo po=pieceFreight.gotPieceFreightPo();
        PieceFreightModelPo orig=pieceFreightModelPoMapper.selectByPrimaryKey(po.getId());
        if (orig == null) {
            logger.info("件数模板不存在或已被删除：id = " + po.getId());
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        ReturnObject<PieceFreight> retObj=null;

        if(po.getRegionId()!=null){
            if(otherForOrderService.getParentRegionIdByson(po.getRegionId())<0){
                logger.debug("该地区不可达");
                return new ReturnObject<>(ResponseCode.REGION_NOT_REACH,String.format("该地区不可达"));
            }
            PieceFreightModelPoExample example=new PieceFreightModelPoExample();
            PieceFreightModelPoExample.Criteria criteria= example.createCriteria();
            criteria.andRegionIdEqualTo(po.getRegionId());
            criteria.andIdNotEqualTo(po.getId());
            criteria.andFreightModelIdEqualTo(orig.getFreightModelId());
            List<PieceFreightModelPo> pieceFreightModelPos=pieceFreightModelPoMapper.selectByExample(example);
            if (pieceFreightModelPos.size()!=0){
                logger.debug("changePieceFreight: change piecefreight failed");
                retObj = new ReturnObject<>(ResponseCode.REGION_SAME, String.format("运费模板中该地区已经定义：" + po.getRegionId()));
                return retObj;
            }
        }


        try {
            int ret=pieceFreightModelPoMapper.updateByPrimaryKeySelective(po);

            if (ret == 0) {
                //修改失败
                logger.debug("updatePieceFreight: update piecefreight fail : " + po.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("件数模板id不存在：" +po.getId()));
            } else {
                //修改成功
                logger.debug("updatePieceFreight: update piecefreight = " + po.toString());
                retObj = new ReturnObject<>();
            }
        }
        catch (DataAccessException e){
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

        }
        catch (Exception e){
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        PieceFreightModelPo result=pieceFreightModelPoMapper.selectByPrimaryKey(po.getId());
        return retObj;
    }
    /**
     * 通过shopId查找FreightModelId
     * @param shopId
     * @return Long
     * @author wxh
     * @date Created in 2020/11/30 20:49
     */

    public Long findFreightModelByshopId(Long shopId){
        Long freightmodelid=-1L;
        FreightModelPoExample example=new FreightModelPoExample();
        FreightModelPoExample.Criteria criteria=example.createCriteria();
        criteria.andShopIdEqualTo(shopId);
        List<FreightModelPo> freightmodel=freightModelPoMapper.selectByExample(example);
        for(FreightModelPo po: freightmodel){
            if(po.getShopId().equals(shopId)){
                freightmodelid=0L;
            }
        }
        return freightmodelid;
    }

    /**
     * 获得店铺中商品的运费模板
     *
     * @author wwq
     * @param shopId 商店编号
     * @param name 运费模板名称
     * @param page 分页
     * @param pageSize 分页大小
     * @return ReturnObject<PageInfo<VoObject>> 查询的分页结果
     * createdBy
     * modifiedBy  2020/12/2 15:22
     */
    public ReturnObject<PageInfo<VoObject>> findSimpleFreightSelective(Long shopId,String name, Integer page, Integer pageSize){
        //DateTimeFormatter df=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        FreightModelPoExample example=new FreightModelPoExample();
        FreightModelPoExample.Criteria criteria=example.createCriteria();
        criteria.andShopIdEqualTo(shopId);
        if(name!=null)
            criteria.andNameEqualTo(name);
        PageHelper.startPage(page,pageSize);
        try {
            List<FreightModelPo> freightPos = freightModelPoMapper.selectByExample(example);
            logger.debug("numbers"+freightPos.size());
            List<VoObject> freights = new ArrayList<>(freightPos.size());
            PageInfo<FreightModelPo> freightPoPageInfo = new PageInfo<>(freightPos);
            for (FreightModelPo po : freightPos) {
                logger.debug("get po freightName"+po.getName());
                Freight freight = new Freight(po);
                freights.add(freight);
            }
            PageInfo<VoObject> freightPage=new PageInfo<>(freights);
            freightPage.setPages(freightPoPageInfo.getPages());
            freightPage.setPageNum(freightPoPageInfo.getPageNum());
            freightPage.setPageSize(freightPoPageInfo.getPageSize());
            freightPage.setTotal(freightPoPageInfo.getTotal());
            return new ReturnObject<>(freightPage);
        }
        catch (DataAccessException e){
            logger.error("selectFreights: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch(Exception e)
        {
            logger.error("other exception:"+e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 克隆运费模板。
     * @param shopId: 店铺id
     * @param id:  运费明细id
     * @return Object
     */
    public ReturnObject<VoObject> cloneFreightModel(Long id,Long shopId) {
        try {//先查询
            FreightModelPo freightModelPo = freightModelPoMapper.selectByPrimaryKey(id);
            if(freightModelPo==null){
                logger.debug("getPieceFreight failed: id不存在。 id = "+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("操作的资源不存在"));
            }
            if((!freightModelPo.getShopId().equals(shopId))&&(shopId!=0L)){
                logger.debug("getFreight failed: id不存在。 id = "+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("操作的不是自己的资源"));
            }
            //原加随机数
            String oldname=freightModelPo.getName();
            Random r=new Random();
            String ran = String.valueOf(r.nextInt(100));
            Freight freight=new Freight(freightModelPo);
            DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            freight.setGmtCreate(LocalDateTime.parse(LocalDateTime.now().format(DATETIME)));
            freight.setName(oldname+ran);
            freight.setDefaultModel(false);
            ReturnObject<VoObject> retObj=insertFreight(freight);
            return retObj;
            //                   null;

//            try{
//                int ret = freightModelPoMapper.insertSelective(freightModelPo);
//                if (ret == 0) {
//                    //插入失败
//                    logger.debug("cloneFreight: clone freight fail " + freightModelPo.toString());
//                    retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("克隆失败：" + freightModelPo.getId()));
//                } else {
//                    //插入成功
//                    logger.debug("cloneFreight: clone freight = " + freightModelPo.toString());
//                    freight.setId(freightModelPo.getId());
//                    retObj = new ReturnObject<>(freight);
//                }
//            }
//            catch (DataAccessException e) {
//                // 其他数据库错误
//                logger.debug("other sql exception : " + e.getMessage());
//                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
//            }
//            catch (Exception e) {
//                // 其他Exception错误
//                logger.error("other exception : " + e.getMessage());
//                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
//            }
//            return retObj;
        } catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }


    /**
     * FreightModel校验
     * @param id 运费模板号
     * @param shopId 店铺号
     * @author wxh
     * @date Created in 2020/12/10 10:28
     */
    public ReturnObject checkFreightModel(Long id,Long shopId){
        ReturnObject retobj=null;
        //验证id是否是自己的资源
        try{
            FreightModelPo freightModelPo=freightModelPoMapper.selectByPrimaryKey(id);
            if(freightModelPo==null){
                logger.debug("getFreight failed: id不存在。 id = "+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("操作的资源id不存在"));
            }
            if(!freightModelPo.getShopId().equals(shopId) && !shopId.equals(0L)){
                logger.debug("getFreight failed: id不存在。 id = "+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("操作的不是自己的资源"));
            }
        }catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retobj;
    }

    /**
     * PieceFreight-FreightModel校验
     * @author  wxh
     * @date Created in 2020/12/13
     */
    public ReturnObject checkPieceFreight(Long id,Long shopId){
        ReturnObject retobj=null;
        try{
            PieceFreightModelPo pieceFreightModelPo= pieceFreightModelPoMapper.selectByPrimaryKey(id);
            if(pieceFreightModelPo==null){
                logger.debug("getPieceFreight failed: id不存在。 id = "+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("操作的资源不存在"));
            }else {
                Long modelId=pieceFreightModelPo.getFreightModelId();
                retobj=checkFreightModel(modelId,shopId);
            }
        }catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retobj;
    }

    /**
     * PieceFreight-FreightModel校验
     * @author  wwq
     * @date Created in 2020/12/14
     */
    public ReturnObject checkWeightFreight(Long id,Long shopId){
        ReturnObject retobj=null;
        try{
            WeightFreightModelPo weightFreightModelPo= weightFreightModelPoMapper.selectByPrimaryKey(id);
            if(weightFreightModelPo==null){
                logger.debug("getWeightFreight failed: id不存在。 id = "+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("操作的资源不存在"));
            }
            FreightModelPo freightModelPo=freightModelPoMapper.selectByPrimaryKey(weightFreightModelPo.getFreightModelId());
            if((!freightModelPo.getShopId().equals(shopId))&&(shopId!=0L)){
                logger.debug("getFreight failed: id不存在。 id = "+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("操作的不是自己的资源"));
            }
            else {
                Long modelId=weightFreightModelPo.getFreightModelId();
                retobj=checkFreightModel(modelId,shopId);
            }
        }catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retobj;
    }
    /**
     * FreightModel按ID查找
     * @param freightModelId 运费模板号
     * @author wwq
     * @date Created in 2020/12/12 20:38
     */
    public FreightModelPo getFreightModelById(Long freightModelId) {
        FreightModelPo freightModelPo = freightModelPoMapper.selectByPrimaryKey(freightModelId);
        return freightModelPo;
    }
    /**
     * WeightFreightModel按FreightModelID查找
     * @param freightModelId 运费模板号
     * @author wwq
     * @date Created in 2020/12/12 20:38
     */
    public ReturnObject<WeightFreight> getWeightFreightModelByFreightModelId(Long freightModelId,Long regionId) {
        WeightFreightModelPoExample example=new WeightFreightModelPoExample();
        WeightFreightModelPoExample.Criteria criteria=example.createCriteria();
        //修改RegionId部分
        Long pid=regionId;
        criteria.andFreightModelIdEqualTo(freightModelId);
        criteria.andRegionIdEqualTo(regionId);
        List<WeightFreightModelPo> weightFreightModelPos = weightFreightModelPoMapper.selectByExample(example);
        while(weightFreightModelPos.size()==0){
            pid=otherForOrderService.getParentRegionIdByson(pid);
            logger.debug(pid.toString());
            if(pid<0){
                logger.debug("地区不可达: regionId不存在。 regionId = "+regionId);
                return new ReturnObject<>(ResponseCode.REGION_NOT_REACH,String.format("该地区不可达"));
            }
            if(pid==0){
                weightFreightModelPos = weightFreightModelPoMapper.selectByExample(example);
                if(weightFreightModelPos.size()==0){
                    logger.debug("地区不可达: regionId不存在。 regionId = "+regionId);
                    return new ReturnObject<>(ResponseCode.REGION_NOT_REACH,String.format("该地区不可达"));
                }
            }
            WeightFreightModelPoExample examplesp=new WeightFreightModelPoExample();
            WeightFreightModelPoExample.Criteria criteriap=examplesp.createCriteria();
            criteriap.andFreightModelIdEqualTo(freightModelId);
            criteriap.andRegionIdEqualTo(pid);
            weightFreightModelPos = weightFreightModelPoMapper.selectByExample(examplesp);
            if(weightFreightModelPos.size()!=0)
                break;
        }
        //
        WeightFreight weightFreight=null;
        for (WeightFreightModelPo po : weightFreightModelPos) {
            weightFreight = new WeightFreight(po);
        }
        return new ReturnObject<>(weightFreight);
    }
    /**
     * 供内部api调用
     * PieceFreightModel按FreightModelID/RegionID查找
     * @param freightModelId 运费模板号
     * @author wwq
     * @date Created in 2020/12/12 21:21
     */
    public ReturnObject<PieceFreight> getPieceFreightModelByFreightModelId(Long freightModelId,Long regionId) {
        PieceFreightModelPoExample example=new PieceFreightModelPoExample();
        PieceFreightModelPoExample.Criteria criteria=example.createCriteria();
        //
        Long pid=regionId;
        criteria.andFreightModelIdEqualTo(freightModelId);
        criteria.andRegionIdEqualTo(regionId);
        List<PieceFreightModelPo> pieceFreightModelPos = pieceFreightModelPoMapper.selectByExample(example);
        while(pieceFreightModelPos.size()==0){
            pid=otherForOrderService.getParentRegionIdByson(pid);
            if(pid<0){
                logger.debug("地区不可达: regionId不存在。 regionId = "+regionId);
                return new ReturnObject<>(ResponseCode.REGION_NOT_REACH,String.format("该地区不可达"));
            }
            if(pid==0){
                pieceFreightModelPos = pieceFreightModelPoMapper.selectByExample(example);
                if(pieceFreightModelPos.size()==0){
                    logger.debug("地区不可达: regionId不存在。 regionId = "+regionId);
                    return new ReturnObject<>(ResponseCode.REGION_NOT_REACH,String.format("该地区不可达"));
                }
            }
            PieceFreightModelPoExample examplesp=new PieceFreightModelPoExample();
            PieceFreightModelPoExample.Criteria criteriap=examplesp.createCriteria();
            criteriap.andFreightModelIdEqualTo(freightModelId);
            criteriap.andRegionIdEqualTo(pid);
            pieceFreightModelPos = pieceFreightModelPoMapper.selectByExample(examplesp);
            if(pieceFreightModelPos.size()!=0)
                break;
        }
        //
        PieceFreight pieceFreight=null;
        for (PieceFreightModelPo po : pieceFreightModelPos) {
            pieceFreight = new PieceFreight(po);
        }
        return new ReturnObject<>(pieceFreight);

    }



    public void initialize()
    {
        FreightModelPoExample example=new FreightModelPoExample();
        FreightModelPoExample.Criteria criteria=example.createCriteria();
        criteria.andDefaultModelEqualTo((byte)1);
        List<FreightModelPo> freightModelPos=new ArrayList<>();
        freightModelPos=freightModelPoMapper.selectByExample(example);
        for(FreightModelPo freightModelPo:freightModelPos) {
            redisTemplate.opsForValue().getAndSet("sf_"+freightModelPo.getShopId().toString(),freightModelPo.getId().toString());
        }
    }

}
