package com.example.demo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wx_user")
public class WxUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String openId;
    private String unionId;
    private String nickname;
    private String avatar;
    private Integer gender;
    private String country;
    private String province;
    private String city;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
