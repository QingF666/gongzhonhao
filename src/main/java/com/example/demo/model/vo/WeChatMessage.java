package com.example.demo.model.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "xml")
public class WeChatMessage {
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "ToUserName")
    private String toUserName;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "FromUserName")
    private String fromUserName;

    @JacksonXmlProperty(localName = "CreateTime")
    private Long createTime;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "MsgType")
    private String msgType;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Content")
    private String content;

    @JacksonXmlProperty(localName = "MsgId")
    private Long msgId;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Event")
    private String event;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "EventKey")
    private String eventKey;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Ticket")
    private String ticket;

    // 新增扫码信息相关字段
    @JacksonXmlProperty(localName = "ScanCodeInfo")
    private ScanCodeInfo scanCodeInfo;

    @Data
    public static class ScanCodeInfo {
        @JacksonXmlCData
        @JacksonXmlProperty(localName = "ScanType")
        private String scanType;

        @JacksonXmlCData
        @JacksonXmlProperty(localName = "ScanResult")
        private String scanResult;
    }
}