package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.po.WeightFreightModelPo;
import cn.edu.xmu.freight.model.vo.PieceFreightRetVo;
import cn.edu.xmu.freight.model.vo.PieceFreightVo;
import cn.edu.xmu.freight.model.vo.WeightFreightRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创建运费模板bo
 * @author wwq
 * created in 2020/11/27 10:05
 */
@Data
public class WeightFreight implements VoObject, Serializable {
    private Long id;
    private Long freightModelId;
    private Long regionId;
    private Long firstWeight;
    private Long firstWeightFreight;
    private Long tenPrice;
    private Long fiftyPrice;
    private Long hundredPrice;
    private Long trihunPrice;
    private Long abovePrice;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModify;

    public WeightFreight(){}

    /**
     * 构造函数
     */
    public WeightFreight(WeightFreightModelPo po){
        this.id=po.getId();
        this.freightModelId=po.getFreightModelId();
        this.regionId=po.getRegionId();
        this.firstWeight=po.getFirstWeight();
        this.firstWeightFreight=po.getFirstWeightFreight();
        this.tenPrice=po.getTenPrice();
        this.fiftyPrice=po.getFiftyPrice();
        this.hundredPrice=po.getHundredPrice();
        this.trihunPrice=po.getTrihunPrice();
        this.abovePrice=po.getAbovePrice();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModify=po.getGmtModified();
    }

    /**
     * 构建WeightFreightRetVo对象返回
     */
    @Override
    public Object createVo(){return new WeightFreightRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }


    /**
     * 用bo更新po
     */
    public WeightFreightModelPo gotWeightFreightPo(){
        WeightFreightModelPo po=new WeightFreightModelPo();
        po.setId(this.id);
        po.setFreightModelId(this.freightModelId);
        po.setRegionId(this.regionId);
        po.setFirstWeight(this.firstWeight);
        po.setFirstWeightFreight(this.firstWeightFreight);
        po.setTenPrice(this.tenPrice);
        po.setFiftyPrice(this.fiftyPrice);
        po.setHundredPrice(this.hundredPrice);
        po.setTrihunPrice(this.trihunPrice);
        po.setAbovePrice(this.abovePrice);
        po.setGmtCreate(this.gmtCreate);
        po.setGmtModified(this.gmtModify);
        return po;
    }
}
