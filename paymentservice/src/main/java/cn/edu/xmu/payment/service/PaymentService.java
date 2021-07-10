package cn.edu.xmu.payment.service;

import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.dao.PaymentDao;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.vo.PaymentVo;
import cn.edu.xmu.payment.util.SnowFlake;
import cn.edu.xmu.provider.server.OtherForOrderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import Service.OrderForOtherService;

import java.time.LocalDateTime;
import java.util.List;

@DubboService
@Service
public class PaymentService {

    private Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentDao paymentDao;

    @DubboReference(check = false)
    private OrderForOtherService orderForOtherService;

    @DubboReference(check = false)
    private OtherForOrderService otherForOrderService;

    private SnowFlake snowFlake = new SnowFlake(0, 0);

    /**
     * 买家为订单创建支付单
     *
     * @param orderId 订单id
     * @param vo      订单视图
     * @return ReturnObject<VoObject> 支付返回视图
     * createdBy Jun 2020/12/01 15:20
     */
    @Transactional
    public ReturnObject<VoObject> customerPostPaymentOrder(Long userId, Long orderId, PaymentVo vo) {
        //exceptions:
        //A. 支付 (无此订单))
        if (orderForOtherService.getCustomerIdByOrderId(orderId, userId) == 1 || orderForOtherService.getOrderState(orderId) == 1) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("创建支付的订单id不存在"));
        }

        //1 null   2 no status
        //D.买家为订单支付 (订单状态禁止)
        if (orderForOtherService.getOrderState(orderId) == 2) {
            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW, String.format("订单状态禁止支付"));
        }
        //C.买家为订单支付 (不是自己订单)
        if (orderForOtherService.getCustomerIdByOrderId(orderId, userId) == 3) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("创建支付的订单id不是自己的订单id"));
        }

        Payment payment = vo.createPayment();
        payment.setBeginTime(LocalDateTime.now());
        payment.setPaySn(String.valueOf(snowFlake.nextId()));

        //payment.setAftersaleId(0L);
        //判断：返点or 一般渠道？
        payment.setPaymentPattern(vo.getPaymentPattern());

        payment.setOrderId(orderId);
        //需要支付的价格
        if (orderForOtherService.caculateAmount(orderId) != null)
            payment.setAmount(orderForOtherService.caculateAmount(orderId));
        //最终支付的价格
        payment.setActualAmount(vo.getPrice());

        //B. 买家为订单支付 (超额支付)
        if (vo.getPrice() > payment.getAmount()) {
        }

        //如果创建即成功，那么支付时间就是现在？
        payment.setPayTime(LocalDateTime.now());
        //即时支付成功？
        payment.setState(Payment.State.PAID);
        payment.setGmtCreate(LocalDateTime.now());
        //如果创建即成功，那么结束时间就是现在？
        payment.setEndTime(LocalDateTime.now());

        //logger.info("aaaaa"+payment);
        ReturnObject<Payment> retObj = paymentDao.postPayment(payment);
        ReturnObject<VoObject> retPayment = null;

        if (retObj.getCode().equals(ResponseCode.OK)) {

            retPayment = new ReturnObject<>(retObj.getData());
        } else {
            retPayment = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
        }
        return retPayment;
    }

    /**
     * 买家查询订单的支付信息
     *
     * @param orderId: 订单id
     * @param userId:  买家id
     * @return ReturnObject<VoObject> 支付返回视图
     * createdBy jun 2020/12/01 18:00
     */
    public ReturnObject<List> customerGetPayment(Long userId, Long orderId) {
        if (orderForOtherService.getCustomerIdByOrderId(orderId, userId) == 1) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询的订单id不存在"));
        }

        if (orderForOtherService.getCustomerIdByOrderId(orderId, userId) == 3) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("查询的不是自己的orderId"));
        }

        return paymentDao.customerGetPayment(orderId);

    }

    /**
     * 管理员查询订单的支付信息
     *
     * @param shopId:  商铺id
     * @param orderId: 订单id
     * @return ReturnObject<VoObject> 支付返回视图
     * createdBy jun 2020/12/01 18:00
     */
    public ReturnObject<List> queryPayment(Long shopId, Long orderId) {
        if (orderForOtherService.getShopIdByOrderId(orderId).equals(-1L)) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询的orderId不存在"));
        }

        if (!orderForOtherService.getShopIdByOrderId(orderId).equals(shopId)) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("查询的不是对应商铺的orderId"));
        }
        return paymentDao.queryPayment(shopId, orderId);
    }

    /**
     * 买家为售后单创建支付单
     *
     * @param aftersaleId: 售后单id
     * @return ReturnObject<VoObject> 售后支付单视图
     * createdBy jun 2020/12/01 18:00
     */
    @Transactional
    public ReturnObject<VoObject> customerPostAfterSalePaymentOrder(Long userId, Long aftersaleId, PaymentVo vo) {
        //exceptions:
//        //A. 支付 (无此订单))
//        if (orderForOtherService.getCustomerIdByOrderId(orderId, userId) == 1 || orderForOtherService.getOrderState(orderId) == 1) {
//            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("创建支付的订单id不存在"));
//        }
//
//        //1 null   2 no status
//        //D.买家为订单支付 (订单状态禁止)
//        if (orderForOtherService.getOrderState(orderId) == 2) {
//            return new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW, String.format("订单状态禁止支付"));
//        }
//        //C.买家为订单支付 (不是自己订单)
//        if (orderForOtherService.getCustomerIdByOrderId(orderId, userId) == 3) {
//            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("创建支付的订单id不是自己的订单id"));
//        }
//A. 支付 (无此订单))

        //查询售后单id不存在
        if (otherForOrderService.getCustormerIdByAftersaled(aftersaleId) == -1L) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("创建的售后单id不存在"));
        }
        //C.买家为售后单支付 (不是自己订单)
        if (!otherForOrderService.getCustormerIdByAftersaled(aftersaleId).equals(userId)) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("创建支付的售后单id不是自己的售后单id"));
        }


        Payment payment = vo.createPayment();
        payment.setBeginTime(LocalDateTime.now());
        payment.setPaySn(String.valueOf(snowFlake.nextId()));

        payment.setAftersaleId(aftersaleId);
        //需要支付的价格
        if (orderForOtherService.caculateAmount(aftersaleId) != null)
            payment.setAmount(orderForOtherService.caculateAmount(aftersaleId));


        payment.setPaymentPattern(vo.getPaymentPattern());

        //最终支付的价格
        payment.setActualAmount(vo.getPrice());
        //如果创建即成功，那么支付时间就是现在？
        payment.setPayTime(LocalDateTime.now());
        //即时支付成功？
        payment.setState(Payment.State.PAID);
        payment.setGmtCreate(LocalDateTime.now());

        //如果创建即成功，那么结束时间就是现在？
        payment.setEndTime(LocalDateTime.now());

        //logger.info("aaaaa"+payment);
        ReturnObject<Payment> retObj = paymentDao.postPayment(payment);
        ReturnObject<VoObject> retPayment = null;
        if (retObj.getCode().equals(ResponseCode.OK)) {
            retPayment = new ReturnObject<>(retObj.getData());
        } else {
            retPayment = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
        }
        return retPayment;
    }


    /**
     * 买家查询自己的售后单支付信息
     * @param aftersaleId: 售后订单id
     * @return ReturnObject<VoObject> 售后支付视图
     * createdBy jun 2020/12/01 19:40
     */
    public ReturnObject<List> customerGetAfterSalePayment(Long userId, Long aftersaleId) {

        //查询售后单id不存在
        if (otherForOrderService.getCustormerIdByAftersaled(aftersaleId).equals(-1l)) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询的售后单id不存在"));
        }

        if (!otherForOrderService.getCustormerIdByAftersaled(aftersaleId).equals(userId)) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("查询的不是自己的aftersaleId"));
        }

        return paymentDao.customerGetAfterSalePayment(aftersaleId);

    }

    /**
     * 管理员查询售后单的支付信息
     * @param shopId: 店铺id
     * @param aftersaleId: 售后单id
     * @return ReturnObject<VoObject> 售后单返回视图
     * createdBy jun 2020/12/01 19:40
     */
    public ReturnObject<List> adminGetAftersalePayment(Long shopId, Long aftersaleId) {


        //查询售后单id不存在
        if (otherForOrderService.getShopIdByAfterSaled(aftersaleId).equals(-1l)) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询的aftersaleId不存在"));
        }

        if (!otherForOrderService.getShopIdByAfterSaled(aftersaleId).equals(shopId)) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("查询的不是对应商铺的aftersaleId"));
        }
        return paymentDao.adminGetAftersalePayment(shopId, aftersaleId);
    }
}