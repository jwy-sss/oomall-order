package cn.edu.xmu.payment.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.payment.model.po.PaymentPo;
import cn.edu.xmu.payment.model.vo.PaymentRetVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * @author jun
 * @date Created in 2020/12/03 0:20
 **/
@Data
public class Payment implements VoObject, Serializable {

    public enum State {
        UNPAID(0, "未支付"),
        PAID(1,"已支付"),
        FALIED(2,"支付失败");



        private static final Map<Integer, State> stateMap;

        //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
        static {
            stateMap = new HashMap();
            for (State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;

        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }


        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum PaymentPattern {
        RET("001", "返点支付"),
        NORMAL("002","模拟支付渠道");



        private static final Map<Integer, PaymentPattern> stateMap;

        //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
        static {
            stateMap = new HashMap();
            for (PaymentPattern enums : values()) {
                stateMap.put(enums.ordinal(), enums);
            }
        }

        private String payPattern;

        private String description;

        PaymentPattern(String payPattern, String description) {
            this.payPattern = payPattern;
            this.description = description;
        }

        public static Payment.PaymentPattern getTypeByCode(String payPattern) {
            return stateMap.get(payPattern);
        }


        public String getPayPattern() {
            return payPattern;
        }

        public String getDescription() {
            return description;
        }


    }

    private Long id;

    private Long orderId;

    private Long aftersaleId;

    private Long amount;

    private Long actualAmount;

    private LocalDateTime payTime;

    private String paymentPattern;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private State state = State.UNPAID;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String paySn;
    /**
     * 用bo对象创建po对象
     * @return
     */
    public PaymentPo gotPaymentPo() {
        PaymentPo paymentPo = new PaymentPo();
        paymentPo.setId(this.getId());
//        paymentPo.setAftersaleId()
        paymentPo.setOrderId(this.getOrderId());
        paymentPo.setAftersaleId(this.getAftersaleId());
        paymentPo.setAmount(this.getAmount());
        paymentPo.setActualAmount(this.getActualAmount());
        paymentPo.setPayTime(this.getPayTime());
        paymentPo.setBeginTime(this.getBeginTime());
        paymentPo.setEndTime(this.getEndTime());
        paymentPo.setPaymentPattern(this.getPaymentPattern());
        Byte state = (byte) this.state.code;
        paymentPo.setState(state);
        paymentPo.setGmtCreate(this.gmtCreate);
        paymentPo.setGmtModified(this.gmtModified);
        paymentPo.setPaySn(this.getPaySn());
        return paymentPo;
    }

    /*
     *构造函数
     * */
    public Payment(){}

    public Payment(PaymentPo po){
        this.id=po.getId();
        this.actualAmount = po.getActualAmount();
        this.amount = po.getAmount();
        this.beginTime = po.getBeginTime();
        this.endTime = po.getEndTime();
        this.payTime = po.getPayTime();
        this.paySn = po.getPaySn();
        if(po.getOrderId()!=null)
            this.orderId = po.getOrderId();
        if(po.getAftersaleId()!=null)
            this.aftersaleId = po.getAftersaleId();
        this.paymentPattern =po.getPaymentPattern();
        this.state=State.getTypeByCode(po.getState().intValue());
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo() { return new PaymentRetVo(this); }

    @Override
    public Object createSimpleVo() { return null; }

    /**
     * 用po对象构建bo对象
     */




}
