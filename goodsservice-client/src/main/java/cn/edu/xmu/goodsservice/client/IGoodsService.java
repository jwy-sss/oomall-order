package cn.edu.xmu.goodsservice.client;


import cn.edu.xmu.goodsservice.model.bo.GoodsSku;
import cn.edu.xmu.goodsservice.model.vo.*;

import java.util.List;

public interface IGoodsService {

    /**
     * @description: 根据商品skuid获得店铺信息
     * @author: jwy
     * @date: Created at 2020/12/6 22:27
     */
    ShopVo getShopBySkuId(Long skuId);

    /**
     * @description: 根据商品skuid获得店铺信息
     * @author: jwy
     * @date: Created at 2020/12/6 22:27
     */
    ShopVo getShopByshopId(Long shopId);

    /**
     * @description: 根据orderitem扣库存
     * @author: jwy
     * @date: Created at 2020/12/6 22:27
     */
    Boolean deductSkuStock(List<OrderItemVo> vo);

    /**
     * @description: 校验优惠劵 校验优惠券是否属于此用户以及优惠券当前是否可用
     * @author: jwy
     * @date: Created at 2020/12/6 22:27
     */
    Boolean checkCoupon(Long couponId, Long userId);

    /**
     * @description: 获取优惠券的适用商品范围
     * @author: Feiyan Liu
     * @date: Created at 2020/12/8 17:23
     */
    List<Long> getSkusByCouponId(Long couponId);
    /**
     * @description: 根据couponActId获取优惠规则
     * @author: jwy
     * @date: Created at 2020/12/6 22:27
     */
    String getStrategyByActivityId(Long couponActivityId,Long couponId);

    /**
     * @description: 获得团购活动商品,返回goodsSkuId 的List
     * @author: jwy
     * @date: Created at 2020/12/6 22:27
     */
    List<Long> getSkuListByGrouponId(Long grouponId);

    /**
     * @description: 获得预售活动商品,返回goodsSkuId
     * @author: jwy
     * @date: Created at 2020/12/6 22:27
     */
    Long getSkuByPresaleId(Long presaleId);

    /**
     * @description: 根据商品skuid获得商品重量
     * @author: jwy
     * @date: Created at 2020/12/6 22:27
     */
    InfoVo getWeightBySkuId(Long skuId);

    /**
     * @description: 将参数1运费模板id所连接的商品的运费模板换成default运费模板
     * @author: jwy
     * @date: Created at 2020/12/6 22:27
     */
    Boolean changeGoodsFreightWeight(Long FreightId, Long defaultFreightId);

    /**
     * @description: 根据goodsSkuid list获取价格
     * @author: jwy
     * @date: Created at 2020/12/6 22:27
     */
    List<Integer> getPriceById(List<Long> goodsSkuId);

    /**
     * @description: 减少预售库存量
     * @author: Feiyan Liu
     * @date: Created at 2020/12/8 17:56
     */

    Boolean deductPresaleStock(Long presaleId,int quantity);

    /**
     * @description:修改优惠券状态 changeMode：0 下单时将优惠券改为已使用
     *                                        1 退款时将优惠券改为已领取
     * @author: Feiyan Liu
     * @date: Created at 2020/12/8 19:05
     */
    Boolean changeCouponState(Long id,int changeMode);

    /**
     * @Description:  修改商品timeSegmentId为0
     *
     * @param timeSegmentId
     * @return: void
     * @Author: LJP_3424
     * @Date: 2020/12/10 15:02
     */
    public void setTimeSegmentIdZero(Long timeSegmentId);
    /**
     * 其他模块
     */
    /**
     * @description: 获取SKU的BO对象
     * @author: Feiyan Liu
     * @date: Created at 2020/12/8 19:54
     */
    GoodsSku getGoodsSkuById(Long skuid);

    /**
     * @description: 通过SKU Id获取价格
     * @author: Feiyan Liu
     * @date: Created at 2020/12/8 19:54
     */
    Integer getPriceBySkuId(Long skuId);

    /**
     * @description:判断sku是否是此商店的商品
     * @author: Feiyan Liu
     * @date: Created at 2020/12/8 19:46
     */
    Boolean checkSkuIdByShopId(Long shopId,Long skuId);

    /**
     * @description: 通过店铺id获取SKU Id列表
     * @author: Feiyan Liu
     * @date: Created at 2020/12/8 19:48
     */
    List<Long> getSkuIdListByShopId(Long shopId);

    /**
     * @description: 通过店铺id获取SKU的bo列表
     * @author: Feiyan Liu
     * @date: Created at 2020/12/8 19:48
     */
    List<GoodsSku> getSkuListByShopId(Long shopId);

    /**
     * @description: 通过skuId组装GoodsCartVo
     * @author: Feiyan Liu
     * @date: Created at 2020/12/10 19:35
     */

    GoodsCartVo getCartByskuId(Long Sku);

    /**
     * @description: 根据skuid获得当前有效的活动
     * @author: Feiyan Liu
     * @date: Created at 2020/12/10 19:34
     */
    CouponActivityVo getCouponActivityIdBySkuId(Long id);
}

