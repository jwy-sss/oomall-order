package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.dao.PaymentDao;
import cn.edu.xmu.payment.dao.RefundDao;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.bo.Refund;
import cn.edu.xmu.payment.model.vo.RefundVo;
import cn.edu.xmu.payment.util.SnowFlake;
import cn.edu.xmu.provider.server.OtherForOrderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Service.OrderForOtherService;

import java.time.LocalDateTime;
import java.util.List;

@DubboService
@Service
public class RefundService {


    private Logger logger = LoggerFactory.getLogger(RefundService.class);

    @Autowired
    private RefundDao refundDao;

    @Autowired
    private PaymentDao paymentDao;

    @DubboReference(check = false)
    private OrderForOtherService orderForOtherService;

    @DubboReference(check = false)
    private OtherForOrderService otherForOrderService;

    private SnowFlake snowFlake=new SnowFlake(0,0);

    /**
     * 管理员创建退款信息
     * @param shopId: 店铺id
     * @param id: 支付id
     * @return ReturnObject<VoObject> 退款信息视图
     * createdBy jun 2020/12/01 19:40
     */
    @Transactional
    public ReturnObject<VoObject> adminCreateRefund(Long shopId, Long id, RefundVo vo) {

        //检查Payment是否是此商铺的payment
        Long orderId = paymentDao.getOrderId(id);

        if(orderId==null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

        if((!shopId.equals(orderForOtherService.getShopIdByOrderId(orderId))))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("Payment不是本店铺的payment"));

        //检查Payment是否大于退款额？
        Long actualAmount = paymentDao.getActualAmount(id);
        if(actualAmount.compareTo(vo.getAmount()) == 1)
            return new ReturnObject<>(ResponseCode.REFUND_MORE,String.format("退款金额超过支付金额"));


        Refund refund = vo.createRefund();

        refund.setOrderId(orderId);

        refund.setPaySn(String.valueOf(snowFlake.nextId()));
        refund.setGmtCreate(LocalDateTime.now());

        refund.setAmount(vo.getAmount());

        refund.setAftersaleId(orderId);

        //即时返回支付成功
        refund.setState(Refund.State.REFUNDED);
        refund.setPaymentId(id);


        ReturnObject<Refund> retObj = refundDao.postRefund(refund);
        ReturnObject<VoObject> retOrder = null;

        if (retObj.getCode().equals(ResponseCode.OK)) {
            retOrder = new ReturnObject<>(retObj.getData());
        } else {
            retOrder = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
        }
        return retOrder;
    }


    /**
     * 管理员查询订单的退款信息
     * @param shopId: 店铺id
     * @param orderId: 订单id
     * @return ReturnObject<VoObject> 退款信息视图
     * createdBy jun 2020/12/01 20:04
     */
    public ReturnObject<List> adminGetRefunds(Long shopId, Long orderId) {
        if(orderForOtherService.getShopIdByOrderId(orderId).equals(-1l)){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询的orderId不存在"));
        }

        if(!orderForOtherService.getShopIdByOrderId(orderId).equals(shopId)){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("查询的不是对应商铺的orderId"));
        }
        return refundDao.adminGetRefunds(shopId,orderId);
    }




    /**
     * 管理员查询售后订单的退款信息
     * @param shopId: 店铺id
     * @param aftersaleId: 售后单id
     * @return ReturnObject<VoObject> 退款返回视图
     * createdBy jun 2020/12/01 20:00
     */
    public ReturnObject<List> adminGetAftersaleRefunds(Long shopId, Long aftersaleId) {
        if(otherForOrderService.getShopIdByAfterSaled(aftersaleId).equals(-1l)){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询的orderId对应的shopId不存在"));
        }

        if(!otherForOrderService.getShopIdByAfterSaled(aftersaleId).equals(shopId)){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("查询的不是对应商铺的orderId"));
        }
        return refundDao.adminGetAftersaleRefunds(shopId,aftersaleId);
    }


    /**
     * 买家查询自己的退款信息
     * @param orderId: 订单id
     * @return ReturnObject<VoObject> 退款信息视图
     * createdBy jun 2020/12/01 19:58
     */
    public ReturnObject<List> customerGetRefunds(Long userId, Long orderId) {
        if(orderForOtherService.getCustomerIdByOrderId(orderId,userId)==1){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询的订单id不存在"));
        }

        if(orderForOtherService.getCustomerIdByOrderId(orderId,userId)==3){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("查询的不是自己的orderId"));
        }



        return refundDao.customerGetRefunds(orderId);
    }

    /**
     * 买家查询自己的售后退款信息
     * @param aftersaleId: 售后订单id
     * @return ReturnObject<VoObject> 退款返回视图
     * createdBy jun 2020/12/01 19:55
     */
    public ReturnObject<List> customerGetAftersalesRefunds(Long userId, Long aftersaleId) {
        if(otherForOrderService.getCustormerIdByAftersaled(aftersaleId).equals(-1l)){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询的售后单id不存在"));
        }

        if(!otherForOrderService.getCustormerIdByAftersaled(aftersaleId).equals(userId)){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("查询的不是自己的orderId"));
        }
        return refundDao.customerGetAftersalesRefunds(aftersaleId);
    }


}
