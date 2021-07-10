package cn.edu.xmu.payment.model.vo;

import cn.edu.xmu.payment.model.bo.Payment;
import lombok.Data;

/**
 * 支付状态VO
 * @author jun
 * @date 2020/12/02 22:28
 */
@Data
public class PaymentStateVo {
        private Long Code;
        private String name;
        public PaymentStateVo(Payment.State state){
            Code=Long.valueOf(state.getCode());
            name=state.getDescription();
        }

}
