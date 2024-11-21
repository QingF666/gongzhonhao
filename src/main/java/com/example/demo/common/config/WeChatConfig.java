package com.example.demo.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WeChatConfig {
    // 公众号配置
    private String token;
    private String encodingAESKey;
    private String messageEncryptMode;

    // OAuth2 配置
    private String appId;
    private String appSecret;

    // 其他配置
    private String authUrl;  // 原来在 WxAuthServiceImpl 中通过 @Value 注入的
}