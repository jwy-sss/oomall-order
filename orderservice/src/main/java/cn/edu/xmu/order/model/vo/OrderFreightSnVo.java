package cn.edu.xmu.order.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 修改订单时的传值对象
 * @author cyx
 * @date Created in 2020/12/1 20:00
 **/
@Data
@ApiModel(description ="店家标记发货传值对象")
public class OrderFreightSnVo {

    @ApiModelProperty(value = "运单信息")
    private String freightSn;


}
