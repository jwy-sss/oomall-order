package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.PieceFreight;
import cn.edu.xmu.freight.model.po.PieceFreightModelPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 件数模板明细返回VO
 * @author Wang Xiaohan
 * Created in 2020/11/26 12:46
 */
@Data
public class PieceFreightRetVo {

    @ApiModelProperty(value = "id")
    private Long id;

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

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModify;

    /**
     * 由bo对象创建RetVo对象
     */
    public PieceFreightRetVo(PieceFreight pieceFreightModel)
    {
        this.id=pieceFreightModel.getId();
        this.regionId=pieceFreightModel.getRegionId();
        this.firstItem=pieceFreightModel.getFirstItem();
        this.firstItemPrice=pieceFreightModel.getFirstItemPrice();
        this.additionalItems=pieceFreightModel.getAdditionalItems();
        this.additionalItemsPrice=pieceFreightModel.getAdditionalItemsPrice();
        this.gmtCreate=pieceFreightModel.getGmtCreate();
        this.gmtModify=pieceFreightModel.getGmtModify();

    }
}
