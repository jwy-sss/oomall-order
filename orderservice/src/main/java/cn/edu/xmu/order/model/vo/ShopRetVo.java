package cn.edu.xmu.order.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopRetVo {

    private Long id;

    private String name;

    private Integer state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
