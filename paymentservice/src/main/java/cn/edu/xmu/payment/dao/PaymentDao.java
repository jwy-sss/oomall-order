package cn.edu.xmu.payment.dao;

import Service.OrderForOtherService;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.mapper.PaymentPoMapper;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.bo.Refund;
import cn.edu.xmu.payment.model.po.PaymentPo;
import cn.edu.xmu.payment.model.po.PaymentPoExample;
import cn.edu.xmu.payment.model.po.RefundPo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

/**
 * 支付DAO
 * @author jun
 **/
@Repository
public class PaymentDao {

    private  static  final Logger logger = LoggerFactory.getLogger(PaymentDao.class);

    @Autowired
    private PaymentPoMapper paymentPoMapper;

    @DubboReference(check = false)
    private OrderForOtherService orderForOtherService;
    /**
     * 买家为订单、售后单创建支付单
     *
     * @param payment 订单bo
     * @return ReturnObject<VoObject> 支付返回视图
     * createdBy Jun 2020/12/03 14:40
     *
     */
    public ReturnObject<Payment> postPayment(Payment payment) {
        //先由bo创建po
        PaymentPo paymentPo = payment.gotPaymentPo();


        if(paymentPo.getAmount().equals(-1L))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("需要支付的订单不存在"));
        ReturnObject<Payment> retObj = null;

        try{
            int ret = paymentPoMapper.insertSelective(paymentPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertPayment: insert payment fail " + paymentPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + paymentPo.getId()));
            } else {
                //插入成功
                logger.debug("insertPayment: insert payment = " + paymentPo.toString());
                payment.setId(paymentPo.getId());
                retObj = new ReturnObject<>(payment);
            }
        }
        catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;


    }


    /**
     * 买家查询订单的支付信息
     *
     * @param orderId: 订单id
//     * @param userId: 买家id
     * @return ReturnObject<VoObject> 支付返回视图
     * createdBy jun 2020/12/01 18:00
     */
    public ReturnObject<List> customerGetPayment(Long orderId) {
        PaymentPoExample example = new PaymentPoExample();
        PaymentPoExample.Criteria criteria1 = example.createCriteria();
        criteria1.andOrderIdEqualTo(orderId);

        //criteria1.andIdEqualTo(orderId);
        try{


            List<PaymentPo> paymentPo=paymentPoMapper.selectByExample(example);
            List<VoObject> paymentContents=null;

            if(paymentPo.isEmpty()){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询id不存在"));
            }
//            Payment payment = new Payment(paymentPo);
            paymentContents=new ArrayList<>(paymentPo.size());
            for (PaymentPo po:paymentPo){
                Payment payment=new Payment(po);

                paymentContents.add(payment);
            }

            return new ReturnObject<>(paymentContents);

        } catch (DataAccessException e) {
            logger.error("selectOrder: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception:" + e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }

    }

    /**
     * 管理员查询订单的支付信息
     * @param shopId: 商店id
     * @param id: 订单id
     * @return ReturnObject<VoObject> 支付返回视图
     * createdBy jun 2020/12/01 18:00
     */
    public ReturnObject<List> queryPayment(Long shopId, Long id) {

        PaymentPoExample example = new PaymentPoExample();
        PaymentPoExample.Criteria criteria1 = example.createCriteria();
        criteria1.andOrderIdEqualTo(id);

        try {
            List<PaymentPo> paymentPo=paymentPoMapper.selectByExample(example);
            List<VoObject> paymentContents=null;
            if(paymentPo.isEmpty()){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询id不存在"));
            }
//            Payment payment = new Payment(paymentPo);
            paymentContents=new ArrayList<>(paymentPo.size());
            for (PaymentPo po:paymentPo){
                Payment payment=new Payment(po);

                paymentContents.add(payment);
            }
            return new ReturnObject<>(paymentContents);
        }
        catch (DataAccessException e){
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

        }
        catch (Exception e){
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 买家查询自己的售后单支付信息
     * @param id: 售后订单id
     * @return ReturnObject<VoObject> 售后支付视图
     * createdBy jun 2020/12/01 19:40
     */
    public ReturnObject<List> customerGetAfterSalePayment(Long id) {
        PaymentPoExample example = new PaymentPoExample();
        PaymentPoExample.Criteria criteria1 = example.createCriteria();
        criteria1.andAftersaleIdEqualTo(id);
        try{

            List<PaymentPo> paymentPo=paymentPoMapper.selectByExample(example);
            List<VoObject> paymentContents=null;
            if(paymentPo.isEmpty()){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询id不存在"));
            }
//            Payment payment = new Payment(paymentPo);
            paymentContents=new ArrayList<>(paymentPo.size());
            for (PaymentPo po:paymentPo){
                Payment payment=new Payment(po);

                paymentContents.add(payment);
            }
            return new ReturnObject<>(paymentContents);

        } catch (DataAccessException e) {
            logger.error("selectOrder: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception:" + e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }

    }



    /**
     * 管理员查询售后单的支付信息
     * @param shopId: 店铺id
     * @param id: 售后单id
     * @return ReturnObject<VoObject> 售后单返回视图
     * createdBy jun 2020/12/01 19:40
     */
    public ReturnObject<List> adminGetAftersalePayment(Long shopId, Long id) {
        List<VoObject> payments=null;
        PaymentPoExample example = new PaymentPoExample();
        PaymentPoExample.Criteria criteria1 = example.createCriteria();
        criteria1.andAftersaleIdEqualTo(id);

        try {
            List<PaymentPo> paymentPo=paymentPoMapper.selectByExample(example);
            List<VoObject> paymentContents=null;
            if(paymentPo.isEmpty()){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询id不存在"));
            }
//            Payment payment = new Payment(paymentPo);
            paymentContents=new ArrayList<>(paymentPo.size());
            for (PaymentPo po:paymentPo){
                Payment payment=new Payment(po);

                paymentContents.add(payment);
            }
            return new ReturnObject<>(paymentContents);
        }
        catch (DataAccessException e){
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

        }
        catch (Exception e){
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 查找支付单的orderId
     * @author cyx
     * @param id 支付单id
     * @return long 返回orderId
     * createdBy cyx 2020/12/17 15:26
     */
    public Long getOrderId(Long id) {
        PaymentPo paymentPo = paymentPoMapper.selectByPrimaryKey(id);
        if(paymentPo==null)
            return null;
        else if(paymentPo.getOrderId()==null)
            return null;
        else return paymentPo.getOrderId();
    }
    /**
     * 查找支付单的ActualAmount
     * @author jun
     * @param id 支付单id
     * @return long 返回ActualAmount
     * createdBy cyx 2020/12/18 15:00
     */
    public Long getActualAmount(Long id) {
        PaymentPo paymentPo = paymentPoMapper.selectByPrimaryKey(id);
        return paymentPo.getActualAmount();
    }


}
