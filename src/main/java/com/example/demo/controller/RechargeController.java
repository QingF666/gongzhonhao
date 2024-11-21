package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.common.ApiResult;
import com.example.demo.model.dto.request.RechargeOrderQuery;
import com.example.demo.model.dto.request.RechargeRequest;
import com.example.demo.model.dto.response.RechargeResponse;
import com.example.demo.model.vo.RechargeOrderVO;
import com.example.demo.service.RechargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/recharge")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RechargeController {

    private final RechargeService rechargeService;

    @PostMapping("/create")
    public ApiResult<RechargeResponse> createRecharge(@RequestBody @Validated RechargeRequest request) {
        RechargeResponse response = rechargeService.processRecharge(request);
        if (response.getSuccess()) {
            return new ApiResult<>(true, "充值订单创建成功", response);
        } else {
            return new ApiResult<>(false, response.getMessage(), null);
        }
    }

    @GetMapping("/history")
    public ApiResult<Page<RechargeOrderVO>> getHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        RechargeOrderQuery query = new RechargeOrderQuery();
        query.setStatus(status);
        query.setPage(page);
        query.setPageSize(pageSize);

        if (startTime != null && !startTime.isEmpty()) {
            query.setStartTime(LocalDateTime.parse(startTime));
        }
        if (endTime != null && !endTime.isEmpty()) {
            query.setEndTime(LocalDateTime.parse(endTime));
        }

        Page<RechargeOrderVO> result = rechargeService.getRechargeHistory(query);
        return new ApiResult<>(true, "获取充值记录成功", result);
    }
}