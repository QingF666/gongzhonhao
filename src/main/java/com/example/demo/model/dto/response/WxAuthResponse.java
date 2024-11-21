package com.example.demo.model.dto.response;

import com.example.demo.model.vo.WxUserVO;
import lombok.Data;

@Data
public class WxAuthResponse {
    private String token;
    private WxUserVO userInfo;
}
