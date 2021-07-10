package cn.edu.xmu.privilege.model.bo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsSku {

    private Long id;

    private Long goodsSpuId;

    private String skuSn;

    private String name;

    private BigDecimal originalPrice;

    private String configuration;

    private BigDecimal weight;

    private String imageUrl;

    private Integer inventory;

    private String detail;

    private Byte disabled;
}
