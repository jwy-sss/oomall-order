package Service;

import java.util.List;

/**
 * 订单模块提供给其他模块调用的接口
 */
public interface OrderForOtherService {

    public Long caculateAmount(Long orderId);

    public Long getShopIdByOrderId(Long orderId);

    public int getCustomerIdByOrderId(Long orderId,Long userId);

    public int getOrderState(Long orderId);

}
