package cn.edu.xmu.payment.model.vo;

import cn.edu.xmu.payment.model.bo.Payment;
import lombok.Data;

@Data
public class PaymentPatternVo {
    private String payPattern;
    private String name;
    public PaymentPatternVo(Payment.PaymentPattern pattern){
        payPattern=pattern.getPayPattern();
        name=pattern.getDescription();
    }

}
