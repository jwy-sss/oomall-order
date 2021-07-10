package cn.edu.xmu.provider.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustomerVo implements Serializable {

    Long id;

    String userName;

    String realName;

    String mobile;

    String email;

    String gender;
    LocalDate birthday;
    LocalDateTime gmt_created;
    LocalDateTime gmt_modified;
    public CustomerVo(){}


}