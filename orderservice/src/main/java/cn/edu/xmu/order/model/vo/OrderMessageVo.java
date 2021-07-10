package cn.edu.xmu.order.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 修改订单时的传值对象
 * @author cyx
 * @date Created in 2020/12/1 15:52
 **/
@Data
@ApiModel(description ="店家修改订单留言传值对象")
public class OrderMessageVo {

    @ApiModelProperty(value = "留言")
    private String message;


}
