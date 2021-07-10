package cn.edu.xmu.payment.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.payment.mapper.PaymentPoMapper;
import cn.edu.xmu.payment.mapper.RefundPoMapper;
import cn.edu.xmu.payment.model.bo.Payment;
import cn.edu.xmu.payment.model.bo.Refund;
import cn.edu.xmu.payment.model.po.PaymentPo;
import cn.edu.xmu.payment.model.po.PaymentPoExample;
import cn.edu.xmu.payment.model.po.RefundPo;
import cn.edu.xmu.payment.model.po.RefundPoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 退款DAO
 * @author jun
 **/
@Repository
public class RefundDao {
    private  static  final Logger logger = LoggerFactory.getLogger(PaymentDao.class);

    @Autowired
    private RefundPoMapper refundPoMapper;

    /**
     * 管理员创建退款信息
     *
     * @return ReturnObject<VoObject> 退款信息视图
     * createdBy jun 2020/12/01 19:40
     */
    public ReturnObject<Refund> postRefund(Refund refund) {
        //先由bo创建po
        RefundPo refundPo = refund.gotRefundPo();

        ReturnObject<Refund> retObj = null;

        try{
            int ret = refundPoMapper.insertSelective(refundPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertRefund: insert refund fail " + refundPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + refundPo.getId()));
            } else {
                //插入成功
                logger.debug("insertRefund: insert refund = " + refundPo.toString());
                refund.setId(refundPo.getId());
                retObj = new ReturnObject<>(refund);
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
     * 管理员查询订单的退款信息
     * @param shopId: 店铺id
     * @param id: 订单id
     * @return ReturnObject<VoObject> 退款信息视图
     * createdBy jun 2020/12/01 20:04
     */
    public ReturnObject<List> adminGetRefunds(Long shopId, Long id) {
        List<VoObject> refunds=null;
        RefundPoExample example = new RefundPoExample();
        RefundPoExample.Criteria criteria1 = example.createCriteria();
        criteria1.andOrderIdEqualTo(id);
        try {
            List<RefundPo> refundPo=refundPoMapper.selectByExample(example);
            List<VoObject> refundContents=null;
            if(refundPo.isEmpty()){

                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询id不存在"));
            }
            refundContents=new ArrayList<>(refundPo.size());
            for (RefundPo po:refundPo){
                Refund refund=new Refund(po);

                refundContents.add(refund);
            }
            return new ReturnObject<>(refundContents);
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
     * 管理员查询售后订单的退款信息
     * @param shopId: 店铺id
     * @param id: 售后单id
     * @return ReturnObject<VoObject> 退款返回视图
     * createdBy jun 2020/12/01 20:00
     */
    public ReturnObject<List> adminGetAftersaleRefunds(Long shopId, Long id) {
        List<VoObject> refunds=null;
        RefundPoExample example = new RefundPoExample();
        RefundPoExample.Criteria criteria1 = example.createCriteria();
        criteria1.andAftersaleIdEqualTo(id);

        try {
            List<RefundPo> refundPo=refundPoMapper.selectByExample(example);
            List<VoObject> refundContents=null;
            if(refundPo.isEmpty()){

                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询id不存在"));
            }
            refundContents=new ArrayList<>(refundPo.size());
            for (RefundPo po:refundPo){
                Refund refund=new Refund(po);

                refundContents.add(refund);
            }
            return new ReturnObject<>(refundContents);
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
     * 买家查询自己的退款信息
     * @param id: 售后单id
     * @return ReturnObject<VoObject> 退款信息视图
     * createdBy jun 2020/12/01 19:58
     */
    public ReturnObject<List> customerGetRefunds(Long id) {
        RefundPoExample example = new RefundPoExample();
        RefundPoExample.Criteria criteria1 = example.createCriteria();
        criteria1.andAftersaleIdEqualTo(id);
        try{

            List<RefundPo> refundPo=refundPoMapper.selectByExample(example);
            List<VoObject> refundContents=null;
            if(refundPo.isEmpty()){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询id不存在"));
            }
            refundContents=new ArrayList<>(refundPo.size());
            for (RefundPo po:refundPo){
                Refund refund=new Refund(po);

                refundContents.add(refund);
            }
            return new ReturnObject<>(refundContents);

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
     * 买家查询自己的售后退款信息
     * @param id: 售后订单id
     * @return ReturnObject<VoObject> 退款返回视图
     * createdBy jun 2020/12/01 19:55
     */
    public ReturnObject<List> customerGetAftersalesRefunds(Long id) {
        RefundPoExample example = new RefundPoExample();
        RefundPoExample.Criteria criteria1 = example.createCriteria();
        criteria1.andAftersaleIdEqualTo(id);
        try{
            List<RefundPo> refundPo=refundPoMapper.selectByExample(example);
            List<VoObject> refundContents=null;

            if(refundPo.isEmpty()){

                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("查询id不存在"));
            }

//            Payment payment = new Payment(paymentPo);
            refundContents=new ArrayList<>(refundPo.size());
            for (RefundPo po:refundPo){
                Refund refund=new Refund(po);

                refundContents.add(refund);
            }
            return new ReturnObject<>(refundContents);

        } catch (DataAccessException e) {
            logger.error("selectOrder: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("other exception:" + e.getMessage());
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("严重数据库错误：%s", e.getMessage()));
        }
    }


}


