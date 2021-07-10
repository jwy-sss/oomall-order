package cn.edu.xmu.payment.model.bo;

import cn.edu.xmu.ooad.model.VoObject;

import cn.edu.xmu.payment.model.po.RefundPo;

import cn.edu.xmu.payment.model.vo.RefundRetVo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Refund implements VoObject, Serializable {
    public enum State {
        UNREFUNDED(0, "未退款"),
        REFUNDED(1,"已退款"),
        FALIED(2,"退款失败");



        private static final Map<Integer, Refund.State> stateMap;

        //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
        static {
            stateMap = new HashMap();
            for (Refund.State enums : values()) {
                stateMap.put(enums.code, enums);
            }
        }

        private int code;

        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Refund.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }


        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }



    private Long id;

    private Long paymentId;

    private Long orderId;

    private Long aftersaleId;

    private Long amount;

    private State state = State.UNREFUNDED;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String paySn;

    /**
     * 用bo对象创建po对象
     * @return
     */
    public RefundPo gotRefundPo() {
        RefundPo refundPo = new RefundPo();
        refundPo.setId(this.getId());
//        refundPo.setAftersaleId()
        refundPo.setPaymentId(this.getPaymentId());

        refundPo.setOrderId(this.getOrderId());

        refundPo.setAftersaleId(this.getAftersaleId());

        refundPo.setAmount(this.getAmount());

        Byte state = (byte) this.state.code;
        refundPo.setState(state);
        refundPo.setGmtCreate(this.gmtCreate);
        refundPo.setGmtModified(this.gmtModified);

        return refundPo;
    }

    /*
     *构造函数
     * */
    public Refund(){}

    public Refund(RefundPo po){
        this.id=po.getId();
        this.amount = po.getAmount();
        if(po.getOrderId()!=null)
            this.orderId = po.getOrderId();
        if(po.getAftersaleId()!=null)
            this.aftersaleId = po.getAftersaleId();
        if(po.getState()!=null)
            this.state= Refund.State.getTypeByCode(po.getState().intValue());
        this.gmtCreate=po.getGmtCreate();
        if(po.getGmtModified()!=null)
            this.gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo() { return new RefundRetVo(this); }

    @Override
    public Object createSimpleVo() { return null; }
}

