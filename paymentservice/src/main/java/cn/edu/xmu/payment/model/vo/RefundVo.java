package cn.edu.xmu.payment.model.vo;

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
public class RefundVo {
    /*"amount": 0*/

    @ApiModelProperty(value = "退款金额")
    private Long amount;
    /**
     * 用Refund对象建立Vo对象
     *
     * @author jun
     *
     * @return Refund
     * createdBy jun 2020/12/02 22:01
     * modifiedBy
     */
    public Refund createRefund() {
        Refund refund = new Refund();
        refund.setAmount(amount);
        return refund;
    }

}
