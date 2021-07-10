package cn.edu.xmu.payment.model.vo;

import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.bo.Refund;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 退款返回视图
 *
 * @author jun
 * createdBy 2020/12/02 22:00
 * modifiedBy
 **/
@Data
@ApiModel(description = "退款返回视图对象")
public class RefundRetVo {
    /*      "id": 0,
      "paymentId": 0,
      "amount": 0,
      "state": 0,
      "gmtCreate": "string",
      "gmtModified": "string",
      "orderId": 0,
      "aftersaleId": 0*/

    @ApiModelProperty(value = "退款id")
    private Long id;

    @ApiModelProperty(value = "支付id")
    private Long paymentId;

    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "售后单id")
    private Long aftersaleId;

    @ApiModelProperty(value = "退款金额")
    private Long amount;

    @ApiModelProperty(value = "退款支付状态")
    private Integer state;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    /**
     * 用Refund对象建立Vo对象
     *
     * @author jun
     * @param refund
     * @return RefundRetVo
     * createdBy jun 2020/12/02 22:01
     * modifiedBy
     */
    public RefundRetVo(Refund refund){
        this.id = refund.getId();
        this.orderId = refund.getOrderId();
        this.aftersaleId = refund.getAftersaleId();
        this.amount = refund.getAmount();
        this.paymentId= refund.getPaymentId();
        if(refund.getState()!=null)
            this.state=refund.getState().getCode();
        this.gmtCreate = refund.getGmtCreate();
        this.gmtModified = refund.getGmtModified();


    }

}
