package com.example.demo.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RechargeResponse {
    private String orderNo;
    private Boolean success;
    private String message;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentUrl;    // 支付链接
    private Integer status;       // 订单状态
}