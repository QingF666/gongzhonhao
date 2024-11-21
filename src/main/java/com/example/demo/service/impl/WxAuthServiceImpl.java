package com.example.demo.service.impl;

// Spring相关
import com.example.demo.common.config.WeChatConfig;
import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.util.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

// MyBatis-Plus相关
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

// 项目内部类

import com.example.demo.mapper.WxUserMapper;
import com.example.demo.model.dto.response.WxAuthResponse;
import com.example.demo.model.dto.response.WxOAuth2Response;
import com.example.demo.model.entity.WxUser;
import com.example.demo.model.vo.WxUserVO;
import com.example.demo.service.WxAuthService;


// Java标准库
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// JWT相关
import io.jsonwebtoken.Claims;

// Lombok
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WxAuthServiceImpl implements WxAuthService {

    private final WeChatConfig weChatConfig;
    private final RestTemplate restTemplate;
    private final WxUserMapper wxUserMapper;
    private final JwtUtils jwtUtils;
    private final String wxAuthUrl;

    public WxAuthServiceImpl(
            WeChatConfig weChatConfig,
            RestTemplate restTemplate,
            WxUserMapper wxUserMapper,
            JwtUtils jwtUtils,
            @Value("${wechat.auth-url}") String wxAuthUrl) {
        this.weChatConfig = weChatConfig;
        this.restTemplate = restTemplate;
        this.wxUserMapper = wxUserMapper;
        this.jwtUtils = jwtUtils;
        this.wxAuthUrl = wxAuthUrl;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxAuthResponse wxAuth(String code) {
        try {
            // 1. 通过code获取access_token和openid
            WxOAuth2Response wxResponse = getWxOAuth2Response(code);

            // 2. 获取或创建用户
            WxUser wxUser = getOrCreateWxUser(wxResponse);

            // 3. 生成JWT token
            String token = generateToken(wxUser);

            // 4. 构建响应对象
            WxAuthResponse response = new WxAuthResponse();
            response.setToken(token);
            response.setUserInfo(convertToVO(wxUser));

            return response;
        } catch (Exception e) {
            log.error("微信授权失败", e);
            throw new BusinessException("微信授权失败: " + e.getMessage());
        }
    }

    @Override
    public WxUserVO getUserInfo(String openId) {
        WxUser wxUser = wxUserMapper.selectOne(
                new LambdaQueryWrapper<WxUser>()
                        .eq(WxUser::getOpenId, openId)
        );

        if (wxUser == null) {
            throw new BusinessException("用户不存在");
        }

        return convertToVO(wxUser);
    }

    private WxOAuth2Response getWxOAuth2Response(String code) {
        String url = String.format(wxAuthUrl,
                weChatConfig.getAppId(),
                weChatConfig.getAppSecret(),
                code
        );

        ResponseEntity<WxOAuth2Response> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(url, WxOAuth2Response.class);
        } catch (Exception e) {
            log.error("调用微信接口失败", e);
            throw new BusinessException("调用微信接口失败");
        }

        WxOAuth2Response response = responseEntity.getBody();
        if (response == null || response.getErrcode() != null) {
            log.error("微信授权响应异常: {}", response);
            throw new BusinessException("微信授权失败: " +
                    Optional.ofNullable(response)
                            .map(WxOAuth2Response::getErrmsg)
                            .orElse("未知错误"));
        }

        return response;
    }

    private WxUser getOrCreateWxUser(WxOAuth2Response wxResponse) {
        // 查询现有用户
        WxUser wxUser = wxUserMapper.selectOne(
                new LambdaQueryWrapper<WxUser>()
                        .eq(WxUser::getOpenId, wxResponse.getOpenid())
        );

        LocalDateTime now = LocalDateTime.now();

        if (wxUser == null) {
            // 创建新用户
            wxUser = new WxUser();
            wxUser.setOpenId(wxResponse.getOpenid());
            wxUser.setUnionId(wxResponse.getUnionid());
            wxUser.setCreateTime(now);
            wxUser.setUpdateTime(now);
            wxUserMapper.insert(wxUser);
        } else {
            // 更新用户信息
            wxUser.setUpdateTime(now);
            if (wxResponse.getUnionid() != null &&
                    !wxResponse.getUnionid().equals(wxUser.getUnionId())) {
                wxUser.setUnionId(wxResponse.getUnionid());
            }
            wxUserMapper.updateById(wxUser);
        }

        return wxUser;
    }

    private String generateToken(WxUser wxUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("openId", wxUser.getOpenId());
        claims.put("userId", wxUser.getId());

        return jwtUtils.generateToken(claims);
    }

    private WxUserVO convertToVO(WxUser wxUser) {
        if (wxUser == null) {
            return null;
        }

        WxUserVO vo = new WxUserVO();
        BeanUtils.copyProperties(wxUser, vo);

        // 敏感信息处理
        if (vo.getNickname() != null) {
            vo.setNickname(maskNickname(vo.getNickname()));
        }

        return vo;
    }

    private String maskNickname(String nickname) {
        if (nickname == null || nickname.length() <= 1) {
            return nickname;
        }
        return nickname.charAt(0) + "***" +
                nickname.charAt(nickname.length() - 1);
    }

    @Override
    public void updateUserInfo(String openId, WxUserVO userInfo) {
        WxUser wxUser = wxUserMapper.selectOne(
                new LambdaQueryWrapper<WxUser>()
                        .eq(WxUser::getOpenId, openId)
        );

        if (wxUser == null) {
            throw new BusinessException("用户不存在");
        }

        BeanUtils.copyProperties(userInfo, wxUser);
        wxUser.setUpdateTime(LocalDateTime.now());

        wxUserMapper.updateById(wxUser);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Claims claims = jwtUtils.parseToken(token);
            String openId = claims.get("openId", String.class);
            return wxUserMapper.exists(
                    new LambdaQueryWrapper<WxUser>()
                            .eq(WxUser::getOpenId, openId)
            );
        } catch (Exception e) {
            log.error("Token验证失败", e);
            return false;
        }
    }
}