package model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsVo implements Serializable {


    private Long skuId;

    private Integer count;

}
