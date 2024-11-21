// RechargeOrderQuery.java
package com.example.demo.model.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RechargeOrderQuery {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}