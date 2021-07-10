package service;

import cn.edu.xmu.ooad.util.ReturnObject;
import model.GoodsVo;

import java.util.List;

/**
 * 运费模块提供给其他模块调用的接口
 */
public interface FreightForOtherService {

    //Boolean changeGoodsFreightWeight(Long FreightId);

    public ReturnObject<Long> caculateFreight(List<GoodsVo> voList, Long rid, Long shopId);
}
