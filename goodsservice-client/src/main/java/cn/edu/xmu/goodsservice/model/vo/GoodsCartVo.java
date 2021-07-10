package cn.edu.xmu.goodsservice.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Yancheng Lai
 * createdBy Yancheng Lai 2020/12/10 12:27
 * modifiedBy Yancheng Lai 12:27
 **/

@Data
public class GoodsCartVo implements Serializable {
    Integer id;
    Long goodsSkuId;
    String skuName;
    String spuName;
    String quantity;
    Integer price;
    //List<CouponActivity> couponActivityList;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;

    public GoodsCartVo(){}
}
