package cn.edu.xmu.payment.model.vo;

import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.bo.Refund;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创建退款传值对象
 * @author jun
 * @date Created in 2020/12/2 22:39
 **/
@Data
@ApiModel(description ="创建退款传值对象")
public class PaymentVo {
    /*{
  "price": 0,
  "paymentPattern": "string"
    */

    @ApiModelProperty(value = "支付金额")
    private Long price;

    @ApiModelProperty(value = "支付渠道")
    private String paymentPattern;
    /**
     * 用Refund对象建立Vo对象
     *
     * @author jun
     *
     * @return Refund
     * createdBy jun 2020/12/02 22:40
     * modifiedBy
     */
    public Payment createPayment() {
        Payment payment = new Payment();
        payment.setActualAmount(this.price);//no setPrice methods
        payment.setPaymentPattern(this.paymentPattern);
        return payment;
    }

}
