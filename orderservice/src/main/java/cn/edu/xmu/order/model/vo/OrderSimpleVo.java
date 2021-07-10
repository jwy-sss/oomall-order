package cn.edu.xmu.order.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 修改订单时的传值对象
 * @author jwy
 * @date Created in 2020/11/7 21:24
 **/
@Data
@ApiModel(description ="修改订单传值对象")
public class OrderSimpleVo {

    @ApiModelProperty(value = "收货人")
    private String consignee;

    @ApiModelProperty(value = "地区id")
    private Long regionId;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "电话")
    private String mobile;

}
