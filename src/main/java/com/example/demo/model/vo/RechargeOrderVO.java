// RechargeOrderVO.java
package com.example.demo.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RechargeOrderVO {
    private String orderNo;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;  // 前端需要的状态: success, pending, failed
    private LocalDateTime createTime;
    private LocalDateTime payTime;
}