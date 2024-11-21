package com.example.demo.service;

import com.example.demo.model.vo.WeChatMessage;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class WeChatService {

    @Value("${wechat.token}")
    private String token;

    private final XmlMapper xmlMapper = new XmlMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public boolean verifySignature(String signature, String timestamp, String nonce) {
        try {
            String[] array = new String[]{token, timestamp, nonce};
            java.util.Arrays.sort(array);

            StringBuilder content = new StringBuilder();
            for (String item : array) {
                content.append(item);
            }
            String generatedSignature = DigestUtils.sha1Hex(content.toString());

            return generatedSignature.equals(signature);
        } catch (Exception e) {
            log.error("验证签名异常", e);
            return false;
        }
    }

    public String processMessage(String requestBody) throws Exception {
        // 1. 解析消息
        WeChatMessage requestMessage = xmlMapper.readValue(requestBody, WeChatMessage.class);

        // 2. 构建响应消息
        WeChatMessage responseMessage = new WeChatMessage();
        responseMessage.setToUserName(requestMessage.getFromUserName());
        responseMessage.setFromUserName(requestMessage.getToUserName());
        responseMessage.setCreateTime(System.currentTimeMillis() / 1000);
        responseMessage.setMsgType("text");

        // 3. 根据消息类型处理
        if ("event".equals(requestMessage.getMsgType())) {
            // 处理事件消息
            switch (requestMessage.getEvent()) {
                case "scancode_push":
                    // 处理扫码推事件
                    handleScanCodePush(requestMessage, responseMessage);
                    break;

                case "scancode_waitmsg":
                    // 处理扫码等待事件
                    handleScanCodeWaitMsg(requestMessage, responseMessage);
                    break;

                default:
                    responseMessage.setContent("暂不支持的事件类型：" + requestMessage.getEvent());
                    break;
            }
        } else if ("text".equals(requestMessage.getMsgType())) {
            // 添加文本消息的日志记录
            log.info("收到用户{}的文本消息：{}", requestMessage.getFromUserName(), requestMessage.getContent());
            responseMessage.setContent("收到你的消息：" + requestMessage.getContent());
        } else {
            responseMessage.setContent("暂不支持的消息类型");
        }

        // 4. 直接返回XML字符串
        String response = xmlMapper.writeValueAsString(responseMessage);
        log.info("返回消息：{}", response);
        return response;
    }

    /**
     * 处理扫码推事件
     */
    private void handleScanCodePush(WeChatMessage requestMessage, WeChatMessage responseMessage) {
        String eventKey = requestMessage.getEventKey();
        log.info("用户{}扫描二维码(推事件)，EventKey：{}", requestMessage.getFromUserName(), eventKey);

        if (requestMessage.getScanCodeInfo() != null) {
            log.info("扫码类型：{}，扫码结果：{}",
                    requestMessage.getScanCodeInfo().getScanType(),
                    requestMessage.getScanCodeInfo().getScanResult());
        }

        String scanResult = requestMessage.getScanCodeInfo() != null ?
                requestMessage.getScanCodeInfo().getScanResult() : "";
        responseMessage.setContent("设备绑定成功！扫码结果：" + scanResult);
    }

    /**
     * 处理扫码等待事件
     */
    private void handleScanCodeWaitMsg(WeChatMessage requestMessage, WeChatMessage responseMessage) {
        String eventKey = requestMessage.getEventKey();
        log.info("用户{}扫描二维码(等待事件)，EventKey：{}", requestMessage.getFromUserName(), eventKey);

        if (requestMessage.getScanCodeInfo() != null) {
            log.info("扫码类型：{}，扫码结果：{}",
                    requestMessage.getScanCodeInfo().getScanType(),
                    requestMessage.getScanCodeInfo().getScanResult());

            String scanResult = requestMessage.getScanCodeInfo().getScanResult();
            String scanType = requestMessage.getScanCodeInfo().getScanType();

            // 构建详细的响应消息
            StringBuilder responseContent = new StringBuilder();
            responseContent.append("扫码成功！\n");
            responseContent.append("━━━━━━━━━━\n");
            responseContent.append("扫码类型：").append(scanType).append("\n");
            responseContent.append("扫码结果：").append(scanResult).append("\n");

            // 这里可以根据扫码结果进行不同的业务处理
            // 例如：如果扫码结果是URL，可以提取域名
            if (scanResult.toLowerCase().startsWith("http")) {
                try {
                    java.net.URL url = new java.net.URL(scanResult);
                    responseContent.append("域名：").append(url.getHost()).append("\n");
                } catch (Exception e) {
                    log.error("解析URL失败", e);
                }
            }

            responseContent.append("━━━━━━━━━━\n");
            responseContent.append("请等待系统处理...");

            responseMessage.setContent(responseContent.toString());
        } else {
            responseMessage.setContent("未能获取扫码信息，请重试");
        }
    }
}