package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.bo.Freight;
import cn.edu.xmu.freight.model.bo.PieceFreight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 运费模板明细返回VO
 * @author wwq
 * Created in 2020/11/26 12:46
 */
@Data
@ApiModel(description ="运费模板传值对象")
public class FreightVo {
    @ApiModelProperty(value = "卖家定义的运费模板名")
    private String name;

    @ApiModelProperty(value = "运费模板类型")
    private Byte type;

    @ApiModelProperty(value = "默认模板")
    private Boolean defaultModel;

    @ApiModelProperty(value = "计量单位")
    private Integer unit;

    /**
     * 使用传入的VO创建Freight对象
     *
     * @author wwq
     * @return Freight
     * created in 2020/11/28 20:16
     * modified by wwq
     */
    public Freight createFreight(){
        Freight freight=new Freight();
        freight.setName(this.name);
        freight.setType(this.type);
        if(this.defaultModel==null||this.defaultModel==false)
            freight.setDefaultModel(false);
        else
            freight.setDefaultModel(true);
            freight.setUnit(this.unit);
        return freight;
    }

}
