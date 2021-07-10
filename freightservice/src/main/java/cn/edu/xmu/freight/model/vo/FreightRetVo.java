package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.Freight;
import cn.edu.xmu.freight.model.po.PieceFreightModelPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 运费模板明细返回VO
 * @author wwq
 * Created in 2020/11/26 15:09
 */
@Data
public class FreightRetVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "卖家定义的运费模板名")
    private String name;

    @ApiModelProperty(value = "运费模板类型")
    private Byte type;

    @ApiModelProperty(value = "默认模板")
    private Boolean defaultModel;

    @ApiModelProperty(value = "创建时间")
    private String gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    @ApiModelProperty(value = "计量单位")
    private Integer unit;

    /**
     * 由PO对象创建RetVo对象
     * @param freightModelPo
     */
    public FreightRetVo(Freight freightModelPo)
    {
        this.id=freightModelPo.getId();
        this.name=freightModelPo.getName();
        this.type= freightModelPo.getType();
        this.defaultModel= freightModelPo.getDefaultModel();
        this.unit=freightModelPo.getUnit();
        DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        this.gmtCreate=freightModelPo.getGmtCreate().format(DATETIME);
        this.gmtModified=freightModelPo.getGmtModified();

    }
}
