package com.example.demo.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WxAuthRequest {
    @NotBlank(message = "code不能为空")
    private String code;
}
