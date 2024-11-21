package com.example.demo.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cache.recharge-history")
@Data
public class CacheProperties {
    private Integer expireTime = 7200;
    private String prefix = "recharge:history:";
}
