package cn.edu.xmu.payment.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.model.bo.Payment;

import cn.edu.xmu.payment.model.vo.PaymentPatternVo;
import cn.edu.xmu.payment.model.vo.PaymentStateVo;
import cn.edu.xmu.payment.model.vo.PaymentVo;
import cn.edu.xmu.payment.model.vo.RefundVo;
import cn.edu.xmu.payment.service.PaymentService;
import cn.edu.xmu.payment.service.RefundService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Api(value = "支付服务", tags = "payment")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/payment", produces = "application/json;charset=UTF-8")
public class PaymentController {


    private  static  final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;


    @Autowired
    private RefundService refundService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 买家为订单创建支付单
     * @param id 订单id
     * @param vo 支付单视图
     * @return Object 支付模型
     * createdBy Jun 2020/12/01 15:20
     *
     * modifiedBy
     */
    @ApiOperation(value = "买家为订单创建支付单", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path"),
            @ApiImplicitParam(paramType = "body", dataType = "PaymentVo", name = "vo", value = "可修改的支付信息", required = true)    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/orders/{id}/payments")
    public Object customerPostPaymentOrder(@LoginUser @ApiIgnore Long userId, @PathVariable Long id, @RequestBody PaymentVo vo, BindingResult bindingResult) {
        logger.info("insert payment");
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.info("validate fail");
            return returnObject;
        }

        ReturnObject<VoObject> retObject = paymentService.customerPostPaymentOrder(userId, id, vo);

        if(retObject.getCode().equals(ResponseCode.OK))
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);

    }

    /**
     * 获得支付单的所有状态
     *
     * @return Object
     * createdBy jun 2020/12/01 17:50
     */
    @ApiOperation(value = "获得支付单的所有状态", nickname = "getpaymentState")
//    @ApiImplicitParams({})
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    //@Audit
    @GetMapping("/payments/states")
    public Object getpaymentState(){

        Payment.State[] states=Payment.State.class.getEnumConstants();
        List<PaymentStateVo> stateVos=new ArrayList<PaymentStateVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new PaymentStateVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());

    }


    /**
     * 获得支付渠道
     *
     * @return Object
     * createdBy jun 2020/12/01 17:52
     */
    @ApiOperation(value = "获得支付渠道", nickname = "getPaymentsPatterns", notes = "目前只返回002 模拟支付渠道")

    @ApiResponses({
            @ApiResponse(code = 0, message = "成功") })
//    @Audit
    @GetMapping(value = "/payments/patterns")
    public Object getPaymentsPatterns(){
//          key value mode   key = 001/002  mode = 一般渠道
        Payment.PaymentPattern[] paymentPatterns=Payment.PaymentPattern.class.getEnumConstants();
        List<PaymentPatternVo> paymentPatternVos=new ArrayList<PaymentPatternVo>();
        for(int i=0;i<paymentPatterns.length;i++){
            paymentPatternVos.add(new PaymentPatternVo(paymentPatterns[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(paymentPatternVos).getData());

        }

    /**
     * 买家查询订单的支付信息
     *
     * @param id: 订单id
     * @return Object
     * createdBy jun 2020/12/01 18:00
     */
    @ApiOperation(value = "买家查询订单的支付信息", nickname = "customerGetPayment")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")    })

    @ApiResponses(
            @ApiResponse(code = 0, message = "成功" ))

    @Audit
    @GetMapping(value = "/orders/{id}/payments")
    public Object customerGetPayment(@LoginUser @ApiIgnore Long userId,
                                     @PathVariable Long id){

        Object returnObject;
        ReturnObject<List> payment = paymentService.customerGetPayment(userId, id);
        //不是异常情况
        if(payment.getCode().equals(ResponseCode.OK)) {
            return Common.getListRetObject(payment);
        }
        //是异常情况
        else{
            returnObject = new ReturnObject(payment.getCode(),payment.getErrmsg());
            return Common.decorateReturnObject((ReturnObject) returnObject);
        }


    }


    /**
     * 管理员查询订单的支付信息
     * @param shopId: shangpi id
     * @param id: 订单id
     * @return Object
     * createdBy jun 2020/12/01 18:00
     */
    @ApiOperation(value = "管理员查询订单的支付信息", nickname = "queryPayment")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", required = true, value = "店铺id",dataType="Long", paramType="path"),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")    })

    @ApiResponses(
            @ApiResponse(code = 0, message = "" ))
    @Audit
    @GetMapping(value = "/shops/{shopId}/orders/{id}/payments")

    public Object queryPayment(@PathVariable Long shopId, @PathVariable Long id){

        Object returnObject;
        logger.debug("queryPayment: id = "+ id + "shopId = "+ shopId);
        ReturnObject<List> payment = paymentService.queryPayment(shopId,id);
        if(!payment.getCode().equals(ResponseCode.OK)) {
            returnObject = new ReturnObject(payment.getCode(),payment.getErrmsg());
            return Common.decorateReturnObject((ReturnObject) returnObject);
        }
        //非异常情况
        else
            return Common.getListRetObject(payment);
    }


    /**
     * 买家为售后单创建支付单
     *
     * @param bindingResult 校验错误
     * @param id: 售后单id
     * @return Object
     * createdBy jun 2020/12/01 18:00
     */
    @ApiOperation(value = "买家为售后单创建支付单", nickname = "aftersalesIdPaymentsPost", notes = "- 此API为模拟API，即时返回支付成功，生成paysn - 如果用返点支付也会产生一条支付记录，price为点数，paymentPattern 为001")

    @RequestMapping(value = "/aftersales/{id}/payments",
            produces = { "application/json" },
            method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path"),
            @ApiImplicitParam(paramType = "body", dataType = "PaymentVo", name = "vo", value = "可修改的支付信息", required = true)    })
    @ApiResponses(
            @ApiResponse(code = 0, message = "成功") )
    @Audit
    @PostMapping("/orders/{id}/payments")
    public Object customerPostAfterSalePaymentOrder(@LoginUser @ApiIgnore Long userId, @PathVariable Long id, @RequestBody PaymentVo vo, BindingResult bindingResult) {
        logger.info("insert payment");
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.info("validate fail");
            return returnObject;
        }
        //由AOP解析token获取userId

        ReturnObject<VoObject> retObject = paymentService.customerPostAfterSalePaymentOrder(userId, id, vo);
        if(retObject.getCode().equals(ResponseCode.OK))
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);

    }


    /**
     * 买家查询自己的售后单支付信息
     * @param id: 售后订单id
     * @return Object
     * createdBy jun 2020/12/01 19:40
     */
    @ApiOperation(value = "买家查询订单的支付信息", nickname = "customerGetAfterSalePayment")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")    })
    @ApiResponses(
            @ApiResponse(code = 0, message = "成功" ))

    @Audit
    @GetMapping(value = "/aftersales/{id}/payments")
    public Object customerGetAfterSalePayment(@LoginUser @ApiIgnore Long userId, @PathVariable Long id){

        Object returnObject;
        ReturnObject<List> payment = paymentService.customerGetAfterSalePayment(userId, id);
        //如果是异常情况
        if(!payment.getCode().equals(ResponseCode.OK)) {
            returnObject = new ReturnObject(payment.getCode(),payment.getErrmsg());
            return Common.decorateReturnObject((ReturnObject) returnObject);
        }
        //非异常情况
        else
            return Common.getListRetObject(payment);
    }



    /**
     * 管理员查询售后单的支付信息
     * @param shopId: 店铺id
     * @param id: 售后单id
     * @return Object
     * createdBy jun 2020/12/01 19:40
     */
    @ApiOperation(value = "管理员查询售后单的支付信息", nickname = "adminGetAftersalePayment")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", required = true, value = "店铺id",dataType="Long", paramType="path"),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")    })

    @ApiResponses(@ApiResponse(code = 0, message = ""))
    @Audit
    @GetMapping(value = "/shops/{shopId}/aftersales/{id}/payments")
    public Object adminGetAftersalePayment(@PathVariable Long shopId, @PathVariable Long id){

        Object returnObject;
        logger.debug("adminGetAftersalePayment: id = "+ id +"shopId = "+ shopId);
        ReturnObject<List> payment = paymentService.adminGetAftersalePayment(shopId,id);
        if(!payment.getCode().equals(ResponseCode.OK)) {
            returnObject = new ReturnObject(payment.getCode(),payment.getErrmsg());
            return Common.decorateReturnObject((ReturnObject) returnObject);
        }
        //非异常情况
        else
            return Common.getListRetObject(payment);
    }





    /**
     * 管理员创建退款信息
     * @param shopId: 店铺id
     * @param id: 售后单id
     * @param vo: 退款金额
     * @return Object
     * createdBy jun 2020/12/01 19:40
     */
    @ApiOperation(value = "管理员创建退款信息，需检查Payment是否是此商铺的payment", nickname = "adminCreateRefund", notes = "此API为模拟API，即时返回支付成功，生成paysn")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", required = true, value = "支付id",dataType="Long", paramType="path"),
            @ApiImplicitParam(name="shopId", required = true, value = "店铺id",dataType="Long", paramType="path"),
            @ApiImplicitParam(paramType = "body", dataType = "RefundVo", name = "vo", value = "退款金额", required = true)    })

    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功") })

    @Audit
    @PostMapping(value = "/shops/{shopId}/payments/{id}/refund")
    public Object adminCreateRefund(@PathVariable Long shopId, @PathVariable Long id, @RequestBody RefundVo vo, BindingResult bindingResult){

        logger.info("insert refund");
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.info("validate fail");
            return returnObject;
        }

        ReturnObject<VoObject> retObject = refundService.adminCreateRefund(shopId, id, vo);
        if(retObject.getCode().equals(ResponseCode.OK))
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);

    }


    /**
     * 管理员查询订单的退款信息
     * @param shopId: 店铺id
     * @param id: 订单id
     * @return Object
     * createdBy jun 2020/12/01 20:04
     */
    @ApiOperation(value = "管理员查询订单的退款信息", nickname = "adminGetRefunds")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", required = true, value = "店铺id",dataType="Long", paramType="path"),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "") })

    @Audit
    @GetMapping(value = "/shops/{shopId}/orders/{id}/refunds")
    public Object adminGetRefunds(@PathVariable Long shopId, @PathVariable Long id){

        Object returnObject;

        ReturnObject<List> refund = refundService.adminGetRefunds(shopId,id);
        if(!refund.getCode().equals(ResponseCode.OK)) {
            returnObject = new ReturnObject(refund.getCode(),refund.getErrmsg());
            return Common.decorateReturnObject((ReturnObject) returnObject);
        }
        //非异常情况
        else
            return Common.getListRetObject(refund);
    }



    /**
     * 管理员查询售后订单的退款信息
     * @param shopId: 店铺id
     * @param id: 售后单id
     * @return Object
     * createdBy jun 2020/12/01 20:00
     */
    @ApiOperation(value = "管理员查询售后订单的退款信息", nickname = "adminGetAftersaleRefunds", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="shopId", required = true, value = "店铺id",dataType="Long", paramType="path"),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "") })

    @Audit
    @GetMapping(value = "/shops/{shopId}/aftersales/{id}/refunds")
    public Object adminGetAftersaleRefunds(@PathVariable Long shopId, @PathVariable Long id){

        Object returnObject;

        ReturnObject<List> refund = refundService.adminGetAftersaleRefunds(shopId,id);
        if(!refund.getCode().equals(ResponseCode.OK)) {
            returnObject = new ReturnObject(refund.getCode(),refund.getErrmsg());
            return Common.decorateReturnObject((ReturnObject) returnObject);
        }
        //非异常情况
        else
            return Common.getListRetObject(refund);
    }

    /**
     * 买家查询自己的退款信息
     * @param id: 订单id
     * @return Object
     * createdBy jun 2020/12/01 19:58
     */
    @ApiOperation(value = "买家查询自己的退款信息", nickname = "customerGetRefunds")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", required = true, value = "售后单id",dataType="Long", paramType="path")    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功") })

    @Audit
    @GetMapping(value = "/orders/{id}/refunds")
    public Object customerGetRefunds(@LoginUser @ApiIgnore Long userId, @PathVariable Long id){

        Object returnObject;
        ReturnObject<List> refund = refundService.customerGetRefunds(userId,id);
        if(!refund.getCode().equals(ResponseCode.OK)) {
            returnObject = new ReturnObject(refund.getCode(),refund.getErrmsg());
            return Common.decorateReturnObject((ReturnObject) returnObject);
        }
        //非异常情况
        else
            return Common.getListRetObject(refund);
    }



    /**
     * 买家查询自己的售后退款信息
     * @param id: 售后订单id
     * @return Object
     * createdBy jun 2020/12/01 19:55
     */
    @ApiOperation(value = "买家查询自己的售后退款信息", nickname = "customerGetAftersalesRefunds", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")    })

    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")})
    @Audit
    @GetMapping(value = "/aftersales/{id}/refunds")
    public Object customerGetAftersalesRefunds(@LoginUser @ApiIgnore Long userId, @PathVariable Long id){

        Object returnObject;
        ReturnObject<List> refund = refundService.customerGetAftersalesRefunds(userId,id);
        if(!refund.getCode().equals(ResponseCode.OK)) {
            returnObject = new ReturnObject(refund.getCode(),refund.getErrmsg());
            return Common.decorateReturnObject((ReturnObject) returnObject);
        }
        //非异常情况
        else
            return Common.getListRetObject(refund);
    }

}
