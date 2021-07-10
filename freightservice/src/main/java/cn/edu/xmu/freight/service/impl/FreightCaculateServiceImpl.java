package cn.edu.xmu.freight.service.impl;

import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.freight.model.bo.PieceFreight;
import cn.edu.xmu.freight.model.bo.WeightFreight;
import cn.edu.xmu.freight.model.po.FreightModelPo;

import cn.edu.xmu.goodsservice.client.IGoodsService;
import cn.edu.xmu.goodsservice.model.vo.InfoVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import model.GoodsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import service.FreightForOtherService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@DubboService
public class FreightCaculateServiceImpl implements FreightForOtherService {

    private Logger logger = LoggerFactory.getLogger(FreightCaculateServiceImpl.class);

    @DubboReference(check = false)
    private IGoodsService goodsService;

    @Autowired
    private FreightDao freightDao;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    Long rid;

    //取订单中所有商品的运费模板，对所有商品使用每个模板计算，取最大值
    @Transactional
    @Override
    public ReturnObject<Long> caculateFreight(List<GoodsVo> goodsVos,Long rid,Long shopId) {
        Long freightResult=0l;
        //总重量
        Integer sumWeight=0;
        //总件数
        Integer sumPiece=0;

        this.rid=rid;
        List<InfoVo> infoVos=new ArrayList<>();
        List<Long> skuIds=new ArrayList<>(goodsVos.size());
        for(GoodsVo vos:goodsVos)
        {
            skuIds.add(vos.getSkuId());
            //计算总件数
            sumPiece+=vos.getCount();
            infoVos.add(goodsService.getWeightBySkuId(vos.getSkuId()));
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
            //logger.debug(String.valueOf(loc));
            //logger.debug(String.valueOf(goodsVos.get(loc)));
            sumWeight+=vos.getWeight()*goodsVos.get(loc).getCount();
        }

        ReturnObject<Long> returnObject;

        returnObject=findMaxFreight(freightModelIds,sumWeight,sumPiece);
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
    private ReturnObject<Long> findMaxFreight(List<Long> freightModelIds,Integer sumWeight,Integer sumPiece){
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
                if(retObj.getCode()!= ResponseCode.OK)
                    return new ReturnObject<>(retObj.getCode(),retObj.getErrmsg());
                wf=retObj.getData();
                temp=cacuByWeight(po.getUnit(),wf,sumPiece);
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

}
