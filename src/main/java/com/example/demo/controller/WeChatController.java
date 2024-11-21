package com.example.demo.controller;

import com.example.demo.service.WeChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/wechat")
public class WeChatController {

    private final WeChatService weChatService;

    @Autowired
    public WeChatController(WeChatService weChatService) {
        this.weChatService = weChatService;
    }

    @GetMapping("/verify")
    public String verifyMessage(
            @RequestParam(name = "signature") String signature,
            @RequestParam(name = "timestamp") String timestamp,
            @RequestParam(name = "nonce") String nonce,
            @RequestParam(name = "echostr") String echostr) {

        log.info("收到微信验证请求 - signature: {}, timestamp: {}, nonce: {}",
                signature, timestamp, nonce);

        if (weChatService.verifySignature(signature, timestamp, nonce)) {
            log.info("微信验证成功");
            return echostr;
        }

        log.warn("微信验证失败");
        return "验证失败";
    }

    @PostMapping(value = "/verify", produces = "application/xml;charset=UTF-8")
    public String handleMessage(
            @RequestBody String requestBody,
            @RequestParam(name = "signature") String signature,
            @RequestParam(name = "timestamp") String timestamp,
            @RequestParam(name = "nonce") String nonce) {

        log.info("收到微信消息: {}", requestBody);

        // 1. 验证签名
        if (!weChatService.verifySignature(signature, timestamp, nonce)) {
            log.warn("消息验证失败");
            return "success";
        }

        try {
            // 2. 直接处理消息并返回响应
            return weChatService.processMessage(requestBody);
        } catch (Exception e) {
            log.error("处理消息异常", e);
            return "success";
        }
    }
}