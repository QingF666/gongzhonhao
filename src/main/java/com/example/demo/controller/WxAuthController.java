package com.example.demo.controller;

import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.util.JwtUtils;
import com.example.demo.model.common.ApiResult;
import com.example.demo.model.dto.request.WxAuthRequest;
import com.example.demo.model.dto.response.WxAuthResponse;
import com.example.demo.model.vo.WxUserVO;
import com.example.demo.service.WxAuthService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// controller/WxAuthController.java
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class WxAuthController {
    @Autowired
    private WxAuthService wxAuthService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/wx-oauth")
    public ApiResult<WxAuthResponse> wxAuth(@RequestBody @Valid WxAuthRequest request) {
        WxAuthResponse response = wxAuthService.wxAuth(request.getCode());
        return ApiResult.success(response);
    }

    @GetMapping("/userinfo")
    public ApiResult<WxUserVO> getUserInfo() {
        String openId = getCurrentUserOpenId();
        WxUserVO userInfo = wxAuthService.getUserInfo(openId);
        return ApiResult.success(userInfo);
    }

    // 获取当前用户OpenId的方法
    private String getCurrentUserOpenId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException("获取请求上下文失败");
        }

        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException("未获取到有效token");
        }

        // 去掉Bearer 前缀
        token = token.substring(7);
        Claims claims = jwtUtils.parseToken(token);
        String openId = claims.get("openId", String.class);
        if (openId == null) {
            throw new BusinessException("token中未包含openId");
        }

        return openId;
    }
}
