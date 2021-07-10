package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.WeightFreight;
import cn.edu.xmu.freight.model.po.PieceFreightModelPo;
import cn.edu.xmu.freight.model.po.WeightFreightModelPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 重量模板明细返回VO
 * @author wwq
 * Created in 2020/11/26 14:36
 */
@Data
public class WeightFreightRetVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "抵达地区码")
    private Long regionId;

    @ApiModelProperty(value = "首重")
    private Long firstWeight;

    @ApiModelProperty(value = "首重价格")
    private Long firstWeightFreight;

    @ApiModelProperty(value = "10kg以下每0.5kg价格")
    private Long tenPrice;

    @ApiModelProperty(value = "50kg以下每0.5kg价格")
    private Long fiftyPrice;

    @ApiModelProperty(value = "100kg以下每0.5kg价格")
    private Long hundredPrice;

    @ApiModelProperty(value = "300kg以下每0.5kg价格")
    private Long trihunPrice;

    @ApiModelProperty(value = "300kg以上每0.5kg价格")
    private Long abovePrice;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;


    /**
     * 由PO对象创建RetVo对象
     */
    public WeightFreightRetVo(WeightFreight weightFreightModel)
    {
        this.id=weightFreightModel.getId();
        this.regionId=weightFreightModel.getRegionId();
        this.firstWeight=weightFreightModel.getFirstWeight();
        this.firstWeightFreight=weightFreightModel.getFirstWeightFreight();
        this.tenPrice=weightFreightModel.getTenPrice();
        this.fiftyPrice=weightFreightModel.getFiftyPrice();
        this.hundredPrice=weightFreightModel.getHundredPrice();
        this.trihunPrice=weightFreightModel.getTrihunPrice();
        this.abovePrice=weightFreightModel.getAbovePrice();
        this.gmtCreate=weightFreightModel.getGmtCreate();
        this.gmtModified=weightFreightModel.getGmtModify();
    }
}
