package cn.edu.xmu.freight.service;

import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.freight.model.bo.Freight;
import cn.edu.xmu.freight.model.bo.PieceFreight;
import cn.edu.xmu.freight.model.bo.WeightFreight;
import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.freight.model.vo.FreightVo;
import cn.edu.xmu.freight.model.vo.PieceFreightVo;
import cn.edu.xmu.freight.model.vo.WeightFreightVo;
import cn.edu.xmu.goodsservice.client.IGoodsService;
import cn.edu.xmu.goodsservice.model.vo.InfoVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import model.GoodsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运费模板服务类
 *
 * @author wwq
 * @date Created in 2020/11/28 19:08
 * Modified
 **/
@DubboService
@Service
public class FreightService {
    private Logger logger = LoggerFactory.getLogger(FreightService.class);

    @Autowired
    private FreightDao freightDao;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @DubboReference(check = false)
    private IGoodsService goodsService;


    /**
     * 计算运费
     * @author wwq
     * @return
     * createdBy wwq 2020/12/18 19:37
     * modifiedBy
     */
    @Transactional
    public ReturnObject<Long> caculateFreightpub(List<GoodsVo> goodsVos, Long rid) {
        Long freightResult=0l;
        //总重量
        Integer sumWeight=0;
        //总件数
        Integer sumPiece=0;

        //初始化shopId 然后在循环中校验
        Long shopId=0L;
        //shopId= goodsService.getShopBySkuId(goodsVos.get(0).getSkuId()).getId();

        List<InfoVo> infoVos=new ArrayList<>();
        List<Long> skuIds=new ArrayList<>(goodsVos.size());
        for(GoodsVo vos:goodsVos)
        {
            skuIds.add(vos.getSkuId());
            //计算总件数
            sumPiece+=vos.getCount();
            infoVos.add(goodsService.getWeightBySkuId(vos.getSkuId()));

            //校验前端传进来的是不是一个店铺的
//            if(shopId!=goodsService.getShopBySkuId(vos.getSkuId()).getId())
//                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }

        List<Long> freightModelIds=new ArrayList<>(goodsVos.size());
        for(InfoVo vos:infoVos)
        {
            //如果不存在才插入 存在就不插入了免得重复计算
            //得到所有的FreightModel 在List中
            Long freightModelId=0l;
            if(vos.getFreightModelId()!=null)
                freightModelId=vos.getFreightModelId();
            else {
                //插入默认运费模板
                //shopId= goodsService.getShopBySkuId(goodsVos.get(0).getSkuId()).getId();
                int loc=infoVos.indexOf(vos);
                shopId=goodsService.getShopBySkuId(goodsVos.get(loc).getSkuId()).getId();
                if(shopId==null){
                    return new ReturnObject<>(ResponseCode.SHOP_ID_NOTEXIST);
                }
                String id= redisTemplate.opsForValue().get("sf_"+shopId).toString();
                if(id==null){
                    return new ReturnObject<>(ResponseCode.MODEL_ID_NOTEXIST);
                }
                freightModelId=Long.parseLong(id);
            }
            //不重复添加
            if(!freightModelIds.contains(freightModelId))
                freightModelIds.add(freightModelId);

            //计算总重量
            int loc=infoVos.indexOf(vos);
            sumWeight+=vos.getWeight()*goodsVos.get(loc).getCount();
        }
        ReturnObject<Long> returnObject;

        returnObject=findMaxFreight(freightModelIds,sumWeight,sumPiece,rid);
        if(returnObject.getCode()!=ResponseCode.OK)
            return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
        else
        return new ReturnObject<>(returnObject.getData());
    }
    //按unit克计算
    private Long cacuByWeight(Integer freightModelUnit,WeightFreight wf,Integer sumWeight){
//        Long cacuByWeightResult=0L;
//        if(sumWeight<=wf.getFirstWeight())
//            cacuByWeightResult=wf.getFirstWeightFreight();
//        else if((sumWeight-wf.getFirstWeight())<=10000){
//            cacuByWeightResult+=wf.getFirstWeightFreight();
//            Long leftWeight=sumWeight-wf.getFirstWeight();
//            Long additionalWeightUnit=0L;
//            if(leftWeight%freightModelUnit==0)
//                additionalWeightUnit=leftWeight/freightModelUnit;
//            else
//                additionalWeightUnit=leftWeight/freightModelUnit+1;
//            cacuByWeightResult+=additionalWeightUnit*wf.getTenPrice();
//        }
//        else if((sumWeight-wf.getFirstWeight())<=50000){
//            cacuByWeightResult+=wf.getFirstWeightFreight();
//            cacuByWeightResult+=10000L/freightModelUnit*wf.getTenPrice();
//            Long leftWeight=sumWeight-wf.getFirstWeight()-10000L;
//            Long additionalWeightUnit=0L;
//            if(leftWeight%freightModelUnit==0)
//                additionalWeightUnit=leftWeight/freightModelUnit;
//            else
//                additionalWeightUnit=leftWeight/freightModelUnit+1;
//            cacuByWeightResult+=additionalWeightUnit*wf.getFiftyPrice();
//        }
//        else if((sumWeight-wf.getFirstWeight())<=100000){
//            cacuByWeightResult+=wf.getFirstWeightFreight();
//            cacuByWeightResult+=10000L/freightModelUnit*wf.getTenPrice();
//            cacuByWeightResult+=40000/freightModelUnit*wf.getFiftyPrice();
//            Long leftWeight=sumWeight-wf.getFirstWeight()-50000L;
//            Long additionalWeightUnit=0L;
//            if(leftWeight%freightModelUnit==0)
//                additionalWeightUnit=leftWeight/freightModelUnit;
//            else
//                additionalWeightUnit=leftWeight/freightModelUnit+1;
//            cacuByWeightResult+=additionalWeightUnit*wf.getHundredPrice();
//        }
//        else if((sumWeight-wf.getFirstWeight())<=300000){
//            cacuByWeightResult+=wf.getFirstWeightFreight();
//            cacuByWeightResult+=10000/freightModelUnit*wf.getTenPrice();
//            cacuByWeightResult+=40000/freightModelUnit*wf.getFiftyPrice();
//            cacuByWeightResult+=50000/freightModelUnit*wf.getHundredPrice();
//            Long leftWeight=sumWeight-wf.getFirstWeight()-100000L;
//            Long additionalWeightUnit=0L;
//            if(leftWeight%freightModelUnit==0)
//                additionalWeightUnit=leftWeight/freightModelUnit;
//            else
//                additionalWeightUnit=leftWeight/freightModelUnit+1;
//            cacuByWeightResult+=additionalWeightUnit*wf.getTrihunPrice();
//        }
//        else if((sumWeight-wf.getFirstWeight())>300000){
//            cacuByWeightResult+=wf.getFirstWeightFreight();
//            cacuByWeightResult+=10000L/freightModelUnit*wf.getTenPrice();
//            cacuByWeightResult+=40000/freightModelUnit*wf.getFiftyPrice();
//            cacuByWeightResult+=50000/freightModelUnit*wf.getHundredPrice();
//            cacuByWeightResult+=200000/freightModelUnit*wf.getTrihunPrice();
//            Long leftWeight=sumWeight-wf.getFirstWeight()-300000L;
//            Long additionalWeightUnit=0L;
//            if(leftWeight%freightModelUnit==0)
//                additionalWeightUnit=leftWeight/freightModelUnit;
//            else
//                additionalWeightUnit=leftWeight/freightModelUnit+1;
//            cacuByWeightResult+=additionalWeightUnit*wf.getAbovePrice();
//        }
//
//        return cacuByWeightResult;
        Long cacuByWeightResult=0L;
        if(sumWeight<=wf.getFirstWeight())
            cacuByWeightResult=wf.getFirstWeightFreight();
        else if(sumWeight<=10000){
            cacuByWeightResult+=wf.getFirstWeightFreight();
            Long leftWeight=sumWeight-wf.getFirstWeight();
            Long additionalWeightUnit=0L;
            if(leftWeight%freightModelUnit==0)
                additionalWeightUnit=leftWeight/freightModelUnit;
            else
                additionalWeightUnit=leftWeight/freightModelUnit+1;
            cacuByWeightResult+=additionalWeightUnit*wf.getTenPrice();
        }
        else if(sumWeight<=50000){
            cacuByWeightResult+=wf.getFirstWeightFreight();
            cacuByWeightResult+=(10000L-wf.getFirstWeight())/freightModelUnit*wf.getTenPrice();
            Long leftWeight=sumWeight-10000L;
            Long additionalWeightUnit=0L;
            if(leftWeight%freightModelUnit==0)
                additionalWeightUnit=leftWeight/freightModelUnit;
            else
                additionalWeightUnit=leftWeight/freightModelUnit+1;
            cacuByWeightResult+=additionalWeightUnit*wf.getFiftyPrice();
        }
        else if(sumWeight<=100000){
            cacuByWeightResult+=wf.getFirstWeightFreight();
            cacuByWeightResult+=(10000L-wf.getFirstWeight())/freightModelUnit*wf.getTenPrice();
            cacuByWeightResult+=40000/freightModelUnit*wf.getFiftyPrice();
            Long leftWeight=sumWeight-50000L;
            Long additionalWeightUnit=0L;
            if(leftWeight%freightModelUnit==0)
                additionalWeightUnit=leftWeight/freightModelUnit;
            else
                additionalWeightUnit=leftWeight/freightModelUnit+1;
            cacuByWeightResult+=additionalWeightUnit*wf.getHundredPrice();
        }
        else if((sumWeight-wf.getFirstWeight())<=300000){
            cacuByWeightResult+=wf.getFirstWeightFreight();
            cacuByWeightResult+=(10000L-wf.getFirstWeight())/freightModelUnit*wf.getTenPrice();
            cacuByWeightResult+=40000/freightModelUnit*wf.getFiftyPrice();
            cacuByWeightResult+=50000/freightModelUnit*wf.getHundredPrice();
            Long leftWeight=sumWeight-100000L;
            Long additionalWeightUnit=0L;
            if(leftWeight%freightModelUnit==0)
                additionalWeightUnit=leftWeight/freightModelUnit;
            else
                additionalWeightUnit=leftWeight/freightModelUnit+1;
            cacuByWeightResult+=additionalWeightUnit*wf.getTrihunPrice();
        }
        else if((sumWeight-wf.getFirstWeight())>300000){
            cacuByWeightResult+=wf.getFirstWeightFreight();
            cacuByWeightResult+=(10000L-wf.getFirstWeight())/freightModelUnit*wf.getTenPrice();
            cacuByWeightResult+=40000/freightModelUnit*wf.getFiftyPrice();
            cacuByWeightResult+=50000/freightModelUnit*wf.getHundredPrice();
            cacuByWeightResult+=200000/freightModelUnit*wf.getTrihunPrice();
            Long leftWeight=sumWeight-300000L;
            Long additionalWeightUnit=0L;
            if(leftWeight%freightModelUnit==0)
                additionalWeightUnit=leftWeight/freightModelUnit;
            else
                additionalWeightUnit=leftWeight/freightModelUnit+1;
            cacuByWeightResult+=additionalWeightUnit*wf.getAbovePrice();
        }

        return cacuByWeightResult;
    }
    //按unit个计算
    private Long cacuByPiece(Integer freightModelUnit,PieceFreight pf, Integer sumPiece){
        Long cacuByPieceResult=0L;
        //logger.debug(String.valueOf(pf.getFirstItem()));
        if(sumPiece<=pf.getFirstItem())
            cacuByPieceResult=pf.getFirstItemPrice();
        else{
            cacuByPieceResult+=pf.getFirstItemPrice();
            Integer leftPiece=sumPiece-pf.getFirstItem();
            Integer additionalPieceItems=0;
            if(leftPiece%(pf.getAdditionalItems())==0)
                additionalPieceItems=leftPiece/pf.getAdditionalItems();
            else
                additionalPieceItems=leftPiece/pf.getAdditionalItems()+1;
            cacuByPieceResult+=additionalPieceItems*pf.getAdditionalItemsPrice();
        }
        return cacuByPieceResult;
    }
    //计算最大运费
    private ReturnObject<Long> findMaxFreight(List<Long> freightModelIds,Integer sumWeight,Integer sumPiece,Long rid){
        Long result=0l;
        Long temp=0l;
        for(Long id:freightModelIds)
        {
            FreightModelPo po=null;
            po=freightDao.getFreightModelById(id);
            if(po.getType()==0){
                WeightFreight wf;
                ReturnObject<WeightFreight> retObj;
                retObj=freightDao.getWeightFreightModelByFreightModelId(id,rid);
                if(retObj.getCode()!=ResponseCode.OK)
                    return new ReturnObject<>(retObj.getCode(),retObj.getErrmsg());
                wf=retObj.getData();
                temp=cacuByWeight(po.getUnit(),wf,sumWeight);
            }
            else{
                PieceFreight pf;
                ReturnObject<PieceFreight> retObj;
                retObj=freightDao.getPieceFreightModelByFreightModelId(id,rid);

                if(retObj.getCode()!=ResponseCode.OK)
                    return new ReturnObject<>(retObj.getCode(),retObj.getErrmsg());
                pf=retObj.getData();
                temp=cacuByPiece(po.getUnit(),pf,sumPiece);
            }
            if(temp>result){
                result=temp;
            }
        }
        return new ReturnObject<>(result);
    }


    /**
     * 定义运费模板
     * @author wwq
     * @param shopId 商铺id
     * @param vo 视图
     * @return ReturnObject<VoObject> 订单返回视图
     * createdBy wwq 2020/11/30 20:57
     * modifiedBy
     */
    @Transactional
    public ReturnObject<VoObject> insertFreight(Long shopId,FreightVo vo) {
        ReturnObject<VoObject> retVo=null;

        Freight freight = vo.createFreight();
        freight.setShopId(shopId);
        DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        freight.setGmtCreate(LocalDateTime.parse(LocalDateTime.now().format(DATETIME)));
        ReturnObject<VoObject> retObj = freightDao.insertFreight(freight);
        if(retObj.getCode().equals(ResponseCode.OK)){
            retVo=new ReturnObject<>(retObj.getData());
        }else {
            retVo=new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
        }
        return retVo;
    }

    /**
     * 获得运费模板概要。
     * @param departId: 店铺id
     * @param id:  运费明细id
     * @return Object
     * @author wxh
     * @date Created in 2020/12/1 21:00
     */
    @Transactional
    public ReturnObject<VoObject> getFreightModelSimple(Long id,Long departId){
        ReturnObject retobj=null;
        retobj=freightDao.checkFreightModel(id,departId);
        if (retobj == null) {
            retobj=freightDao.getFreightModelSimple(id,departId);
            logger.debug("getFreightModelSimple:"+retobj.toString());
        }
        return retobj;
    }

    /**
     * 管理员修改店铺的运费模板
     *
     * @param id
     * @param shopId
     * @param vo
     * @return ReturnObject<Object>
     * @author wxh
     * @date Created in 2020/12/1 21:44
     * Modified in 2020/12/11 8:06
     */
    @Transactional
    public ReturnObject<VoObject> changeFreight(Long id,Long shopId,FreightVo vo){
        ReturnObject<VoObject> RetObj=null;
        RetObj=freightDao.checkFreightModel(id,shopId);
        if(RetObj==null){
            Freight freight= vo.createFreight();
            freight.setGmtModified(LocalDateTime.now());
            freight.setShopId(shopId);
            freight.setId(id);
            if(vo.getType()!=null) freight.setType(null);
            ReturnObject<VoObject> returnObject=freightDao.changeFreight(freight);
            if (returnObject.getCode().equals(ResponseCode.OK)) {
                RetObj = new ReturnObject<>(returnObject.getData());
            } else {
                RetObj = new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg());
            }
        }
        return RetObj;
    }

    /**
     * 删除运费模板，需同步删除与商品的
     * @param id
     * @param shopId
     * @return ReturnObject<Object>
     * @author wxh 2020/12/2 16:45
     */
    @Transactional
    public ReturnObject<Object> deleteFreight(Long id,Long shopId){
        ReturnObject RetObj=null;
        RetObj=freightDao.checkFreightModel(id,shopId);
        if(RetObj==null){
//            goodsService.changeGoodsFreightWeight(id);
            return freightDao.deleteFreight(id, shopId);
        }
        return RetObj;
    }

    /**
     * 查找默认运费模板
     * @author wxh
     * @date Created in 2020/12/10 12:12
     */
    private Long findDefaultFreightModelId(Long shopId){
        return freightDao.findDefaultFreightModelId(shopId);
    }

    /**
     * 店家或管理员为商铺定义默认运费模板
     * @param id
     * @param shopId
     * @return ReturnObject<Object>
     * @author wxh
     * @date 2020/12/2 16:42
     */
    @Transactional
    public ReturnObject<Object> defineDefault(Long id,Long shopId){
        ReturnObject<Object> retobj=null;
        retobj=freightDao.checkFreightModel(id,shopId);
        if(retobj==null){
            //检查是否已有默认运费模板
            if(freightDao.findDefaultFreightModelId(shopId)!=0L){
                //将其默认值字段置为false
                Freight freight2=new Freight();
                freight2.setId(freightDao.findDefaultFreightModelId(shopId));
                freight2.setDefaultModel(false);
//                freight2.setGmtModified(LocalDateTime.now());
//                ReturnObject<VoObject> returnObject=freightDao.changeFreight(freight2);
                freight2.setGmtModified(LocalDateTime.now());
                ReturnObject returnObject=freightDao.changeFreight(freight2);
                if (!returnObject.getCode().equals(ResponseCode.OK)) {
                    return new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg());
                }
            }
            //将新的默认运费模板默认值字段置为true
            Freight freight=new Freight();
            freight.setId(id);
            freight.setDefaultModel(true);
            freight.setGmtModified(LocalDateTime.now());
            retobj=freightDao.changeFreight(freight);
            //redisTemplate.opsForValue().getAndSet("sf_"+shopId,id);

        }
        return retobj;

    }

    /**
     * 定义重量模板明细
     *
     * @param vo
     * @param id
     * @param shopId
     * @Return ReturnObject<VoObject> 角色返回视图
     * @author wwq
     * @date Created in 2020/11/29 14:12
     */
    @Transactional
    public ReturnObject<VoObject> insertWeightFreight(Long id, Long shopId, WeightFreightVo vo){
        ReturnObject<VoObject> retVo=null;
        retVo=freightDao.checkFreightModel(id,shopId);
        if(retVo==null){
            retVo=getFreightModelSimple(id,shopId);
            WeightFreight weightFreight=vo.createWeightFreight();
            weightFreight.setFreightModelId(id);
            weightFreight.setGmtCreate(LocalDateTime.now());
//        if(redisTemplate.opsForValue().get("rg_"+weightFreight.getRegionId())==null)
            ReturnObject<WeightFreight> retObj=freightDao.insertWeightFreight(weightFreight);
            if(retObj.getCode().equals(ResponseCode.OK)){
                retVo=new ReturnObject<>(retObj.getData());
            }else {
                retVo=new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
            }
        }
        return retVo;
    }
    /**
     * 店家或管理员查询重量运费模板的明细。
     *
     * @param id
     * @param shopId
     * @return  ReturnObject<List<VoObject>>
     * @author wwq
     * @date Created in 2020/12/1 11:50
     *
     */
    @Transactional
    public ReturnObject<List> getWeightFreight(Long id, Long shopId){
        ReturnObject<List> retObj=null;
        retObj=freightDao.getWeightFreight(id,shopId);
        if(retObj==null){
            retObj=freightDao.getWeightFreight(id,shopId);
            logger.debug("getWeightfreight:"+retObj.toString());
        }
        return retObj;
    }

    /**
     * 店家或管理员修改重量运费模板明细
     * @param id
     * @param shopId
     * @return ReturnObject<Object>
     * @author wwq
     * @date Created in 2020/12/1 15:23
     */
    @Transactional
    public ReturnObject<Object> changeWeightFreight(Long id,Long shopId,WeightFreightVo vo){
        ReturnObject<Object> retobj=null;
        retobj=freightDao.checkWeightFreight(id,shopId);
        if(retobj==null){
            WeightFreight weightFreight=vo.createWeightFreight();
            weightFreight.setId(id);
            weightFreight.setGmtModify(LocalDateTime.now());
            ReturnObject<WeightFreight> weightFreightRetObj= freightDao.changeWeightFreight(weightFreight);
            if (weightFreightRetObj.getCode().equals(ResponseCode.OK)) {
                retobj = new ReturnObject<>(weightFreightRetObj.getData());
            } else {
                retobj = new ReturnObject<>(weightFreightRetObj.getCode(), weightFreightRetObj.getErrmsg());
            }
        }
        return retobj;

    }


    /**
     * 店家或管理员删掉重量运费模板明细
     *
     * @param id
     * @param shopId
     * @return ReturnObject<Object>
     * @author wwq
     * @date Created in 2020/11/29 18:25
     *
     */
    @Transactional
    public  ReturnObject<Object> deleteWeightFreight(Long id,Long shopId){
        ReturnObject<Object> retobj=null;
        retobj=freightDao.checkWeightFreight(id,shopId);
        if(retobj==null){
            retobj=freightDao.deleteWeightFreight(id,shopId);
        }
        return retobj;

    }
    /**
     * 定义件数模板明细
     *
     * @param vo
     * @param id
     * @param shopId
     * @Return ReturnObject<VoObject> 角色返回视图
     * @author WXH
     * @date Created in 2020/11/28 15:32
     */
    @Transactional
    public ReturnObject<VoObject> insertPieceFreight(Long id, Long shopId, PieceFreightVo vo){
        ReturnObject<VoObject> retVo=null;
        retVo=freightDao.checkFreightModel(id,shopId);
        if(retVo==null){
            retVo=getFreightModelSimple(id,shopId);
            PieceFreight pieceFreight=vo.createPieceFreight();
            pieceFreight.setFreightModelId(id);
            pieceFreight.setGmtCreate(LocalDateTime.now());
//        if(redisTemplate.opsForValue().get("rg_"+pieceFreight.getRegionId())==null)
            ReturnObject<PieceFreight> retObj=freightDao.insertPieceFreight(pieceFreight);
            if(retObj.getCode().equals(ResponseCode.OK)){
                retVo=new ReturnObject<>(retObj.getData());
            }else {
                retVo=new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
            }
        }
        return retVo;
    }


    /**
     * 店家或管理员查询件数运费模板的明细。
     *
     * @param id
     * @param shopId
     * @return  ReturnObject<List<VoObject>>
     * @author wxh
     * @date Created in 2020/11/29 11:50
     *
     */
    @Transactional
    public ReturnObject<List> getPieceFreight(Long id, Long shopId){
        ReturnObject<List> retObj=null;
        retObj=freightDao.checkFreightModel(id,shopId);
        if(retObj==null){
            retObj=freightDao.getPieceFreight(id,shopId);
            logger.debug("getPiecefreight:"+retObj.toString());
        }
        return retObj;
    }

    /**
     * 店家或管理员修改件数运费模板明细
     * @param id
     * @param shopId
     * @return ReturnObject<Object>
     * @author wxh
     * @date Created in 2020/12/1 11:02
     * Modified in 2020/12/9 8:46
     */
    @Transactional
    public ReturnObject<Object> changePieceFreight(Long id, Long shopId, PieceFreightVo vo){
        ReturnObject<Object> retobj=null;
        retobj=freightDao.checkPieceFreight(id,shopId);
        if(retobj==null){
            PieceFreight pieceFreight=vo.createPieceFreight();
            pieceFreight.setId(id);
            pieceFreight.setGmtModify(LocalDateTime.now());
            ReturnObject<PieceFreight> pieceFreightRetObj= freightDao.changePieceFreight(pieceFreight);
            if (pieceFreightRetObj.getCode().equals(ResponseCode.OK)) {
                retobj = new ReturnObject<>(pieceFreightRetObj.getData());
            } else {
                retobj = new ReturnObject<>(pieceFreightRetObj.getCode(), pieceFreightRetObj.getErrmsg());
            }
        }
        return retobj;
    }


    /**
     * 店家或管理员删掉件数运费模板明细
     *
     * @param id
     * @param shopId
     * @return ReturnObject<Object>
     * @author wxh
     * @date Created in 2020/11/29 11:25
     *
     */
    @Transactional
    public  ReturnObject<Object> deletePieceFreight(Long id,Long shopId){
        ReturnObject<Object> retobj=null;
        retobj=freightDao.checkPieceFreight(id,shopId);
        if(retobj==null){
            retobj=freightDao.deletePieceFreight(id,shopId);
        }
        return retobj;
    }

    /**
     * 获得店铺中商品的运费模板
     * @author wwq
     * @param name
     * @param page
     * @param pageSize
     * @return ReturnObject<PageInfo<VoObject>> 订单概要返回视图
     * createdBy wwq 2020/12/2 16:20
     * modifiedBy wwq
     */
    public ReturnObject<PageInfo<VoObject>> findSimpleFreightSelective(Long shopId,String name, Integer page, Integer pageSize) {
        return freightDao.findSimpleFreightSelective(shopId,name,page,pageSize);
    }

    /**
     * 克隆运费模板
     * @author wwq
     * @param shopId 商铺id
     * @param id 运费模板id
     * @return ReturnObject<Freight> 订单返回视图
     * createdBy wwq 2020/12/2 19:02
     * modifiedBy
     */
    @Transactional
    public ReturnObject<VoObject> cloneFreight(Long id,Long shopId) {
        ReturnObject<VoObject> retVo=null;
        ReturnObject<VoObject> retobj=freightDao.cloneFreightModel(id,shopId);
        logger.debug("getFreightModelSimple:"+retobj.toString());
        if(retobj.getCode().equals(ResponseCode.OK)){
            retVo=new ReturnObject<>(retobj.getData());
        }else {
            retVo=new ReturnObject<>(retobj.getCode(), retobj.getErrmsg());
        }
        return retobj;
    }


}
