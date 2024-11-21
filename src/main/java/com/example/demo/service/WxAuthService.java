package com.example.demo.service;

import com.example.demo.model.dto.response.WxAuthResponse;
import com.example.demo.model.vo.WxUserVO;

public interface WxAuthService {
    /**
     * 微信认证
     */
    WxAuthResponse wxAuth(String code);

    /**
     * 获取用户信息
     */
    WxUserVO getUserInfo(String openId);

    /**
     * 更新用户信息
     */
    void updateUserInfo(String openId, WxUserVO userInfo);

    /**
     * 验证token
     */
    boolean validateToken(String token);
}