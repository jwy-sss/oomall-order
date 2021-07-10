package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.po.PieceFreightModelPo;
import cn.edu.xmu.freight.model.vo.PieceFreightRetVo;
import cn.edu.xmu.freight.model.vo.PieceFreightVo;
import cn.edu.xmu.ooad.model.VoObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创建件数模板bo
 * @author wxh
 * created in 2020/11/27 8:28
 */
@Data
public class PieceFreight implements VoObject, Serializable {
    private Long id;
    private Long freightModelId;
    private Long regionId;
    private Integer firstItem;
    private Long firstItemPrice;
    private Integer additionalItems;
    private Long additionalItemsPrice;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModify;

    public PieceFreight(){}

    /**
     * 构造函数
     */
    public PieceFreight(PieceFreightModelPo po){
        this.id=po.getId();
        this.freightModelId=po.getFreightModelId();
        this.regionId=po.getRegionId();
        this.firstItem= po.getFirstItems();
        this.firstItemPrice= po.getFirstItemsPrice();
        this.additionalItems=po.getAdditionalItems();
        this.additionalItemsPrice=po.getAdditionalItemsPrice();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModify=po.getGmtModified();
    }

    /**
     * 构建PieceFreightRetVo对象返回
     */
    @Override
    public Object createVo(){return new PieceFreightRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }


    /**
     * 用bo更新po
     */
    public PieceFreightModelPo gotPieceFreightPo(){
        PieceFreightModelPo po=new PieceFreightModelPo();
        po.setId(this.id);
        po.setFreightModelId(this.freightModelId);
        po.setRegionId(this.regionId);
        po.setFirstItems(this.firstItem);
        po.setFirstItemsPrice(this.firstItemPrice);
        po.setAdditionalItems(this.additionalItems);
        po.setAdditionalItemsPrice(this.additionalItemsPrice);
        po.setGmtCreate(this.gmtCreate);
        po.setGmtModified(this.gmtModify);
        return po;
    }

}
