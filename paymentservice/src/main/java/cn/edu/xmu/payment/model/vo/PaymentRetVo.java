package cn.edu.xmu.payment.model.vo;


import cn.edu.xmu.payment.model.bo.Payment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付返回视图
 *
 * @author jun
 * createdBy 2020/12/02 21:32
 * modifiedBy
 **/
@Data
@ApiModel(description = "支付返回视图对象")
public class PaymentRetVo {

    @ApiModelProperty(value = "支付id")
    private Long id;

    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "售后单id")
    private Long aftersaleId;

    @ApiModelProperty(value = "支付金额")
    private Long amount;

    @ApiModelProperty(value = "实际支付金额")
    private Long actualAmount;

    @ApiModelProperty(value = "支付时间")
    private LocalDateTime payTime;

    @ApiModelProperty(value = "支付渠道")
    private String paymentPattern;

    @ApiModelProperty(value = "开始支付时间")
    private LocalDateTime beginTime;

    @ApiModelProperty(value = "结束支付时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "支付状态")
    private Integer state;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;


    /**
     * 用Payment对象建立Vo对象
     *
     * @author jun
     * @param payment
     * @return PaymentRetVo
     * createdBy jun 2020/12/02 22:01
     * modifiedBy
     */
    public PaymentRetVo(Payment payment){
        this.id = payment.getId();
        this.orderId = payment.getOrderId();
        this.aftersaleId = payment.getAftersaleId();
        this.amount = payment.getAmount();
        this.actualAmount = payment.getActualAmount();
        this.payTime = payment.getPayTime();
        this.paymentPattern = payment.getPaymentPattern();
        this.beginTime = payment.getBeginTime();
        this.endTime = payment.getEndTime();
        if(payment.getState()!=null) {
            this.state=payment.getState().getCode();
        }
        this.gmtCreate = payment.getGmtCreate();
        this.gmtModified = payment.getGmtModified();


    }

}
