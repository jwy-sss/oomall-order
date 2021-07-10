package cn.edu.xmu.order.model.vo;

import cn.edu.xmu.order.model.bo.Order;
import lombok.Data;

/**
 * 订单状态VO
 * @author LiangJi3229
 * @date 2020/11/10 18:41
 */
@Data
public class OrderStateVo {
    private Integer Code;

    private String name;
    public OrderStateVo(Integer Code ,String name){
        this.Code=Code;
        this.name=name;
    }
}
