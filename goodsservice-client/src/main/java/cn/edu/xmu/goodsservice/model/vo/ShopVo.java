package cn.edu.xmu.goodsservice.model.vo;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ShopVo implements Serializable {
    private Long id;

    private String name;

    private Integer state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
    public ShopVo(){};
}
