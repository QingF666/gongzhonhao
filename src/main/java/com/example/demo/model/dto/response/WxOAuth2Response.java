package com.example.demo.model.dto.response;

import lombok.Data;

@Data
public class WxOAuth2Response {
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;
    private Integer errcode;
    private String errmsg;
}