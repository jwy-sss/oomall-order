package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.PieceFreight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 件数模板明细返回VO
 * @author Wang Xiaohan
 * Created in 2020/11/26 12:46
 */
@Data
@ApiModel(description ="件数模板明细传值对象")
public class PieceFreightVo {
    @NotNull(message = "抵达地区码不能为空")
    @ApiModelProperty(value = "抵达地区码")
    private Long regionId;

    @ApiModelProperty(value = "首件数")
    private Integer firstItem;

    @ApiModelProperty(value = "规则首件运费")
    private Long firstItemPrice;

    @ApiModelProperty(value = "规则续件数")
    private Integer additionalItems;

    @ApiModelProperty(value = "规则续件运费")
    private Long additionalItemsPrice;

    /**
     * 使用传入的VO创建PieceFreight对象
     *
     * @author wxh
     * @return PieceFreight
     * created in 2020/11/26 15:16
     */
    public PieceFreight createPieceFreight(){
        PieceFreight bo=new PieceFreight();
        bo.setRegionId(this.regionId);
        bo.setFirstItem(this.firstItem);
        bo.setFirstItemPrice(this.firstItemPrice);
        bo.setAdditionalItems(this.additionalItems);
        bo.setAdditionalItemsPrice(this.additionalItemsPrice);
        return bo;
    }

}
