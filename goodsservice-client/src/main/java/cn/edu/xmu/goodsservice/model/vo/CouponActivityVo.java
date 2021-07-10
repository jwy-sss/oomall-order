package cn.edu.xmu.goodsservice.model.vo;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Feiyan Liu
 * @date Created at 2020/12/10 19:22
 */
@Data
public class CouponActivityVo implements Serializable {
    Long id;
    String name;
    LocalDateTime beginTime;
    LocalDateTime endTime;

    public CouponActivityVo(){}
}
