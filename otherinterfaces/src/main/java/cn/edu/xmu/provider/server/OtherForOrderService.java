package cn.edu.xmu.provider.server;

import cn.edu.xmu.provider.model.vo.CustomerVo;

import java.time.LocalDateTime;

public interface OtherForOrderService {
    /**
     * @description: 根据userid获得顾客信息
     * @author: jwy
     * @date: Created at 2020/12/6 22:27
     */
    CustomerVo getUserByuserId(Long userId);


    /**
     * @description: 支付成功修改售后单状态
     * @author: jwy
     * @date: Created at 2020/12/6 22:27
     */
    boolean changeStateAfterPay(Long orderId);

    Long getCustormerIdByAftersaled(Long aftersaled);

    Long getShopIdByAfterSaled(Long aftersaledId);

    Long getParentRegionIdByson(Long regionId);
}
