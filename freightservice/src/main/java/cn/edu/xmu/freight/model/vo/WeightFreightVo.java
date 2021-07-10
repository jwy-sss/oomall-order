package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.WeightFreight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description ="件数模板明细传值对象")
public class WeightFreightVo {
    @NotNull(message = "抵达地区码不能为空")
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


    /**
     * 使用传入的VO创建WeightFreight对象
     *
     * @author wwq
     * @return Order
     * created in 2020/11/27 08:36
     */
    public WeightFreight createWeightFreight(){
        WeightFreight bo=new WeightFreight();
        bo.setRegionId(this.regionId);
        bo.setFirstWeight(this.firstWeight);
        bo.setFirstWeightFreight(this.firstWeightFreight);
        bo.setTenPrice(this.tenPrice);
        bo.setFiftyPrice(this.fiftyPrice);
        bo.setHundredPrice(this.hundredPrice);
        bo.setTrihunPrice(this.trihunPrice);
        bo.setAbovePrice(this.abovePrice);
        return bo;
    }
}