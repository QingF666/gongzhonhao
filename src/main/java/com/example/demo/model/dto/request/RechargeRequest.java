// RechargeRequest.java
package com.example.demo.model.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeRequest {
    private BigDecimal amount;
    private String paymentMethod;
}

