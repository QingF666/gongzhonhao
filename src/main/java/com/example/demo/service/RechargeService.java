package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.mapper.RechargeOrderMapper;

import com.example.demo.model.dto.request.RechargeOrderQuery;
import com.example.demo.model.dto.request.RechargeRequest;
import com.example.demo.model.dto.response.RechargeResponse;
import com.example.demo.model.entity.RechargeOrder;
import com.example.demo.model.vo.RechargeOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RechargeService {

    private final RechargeOrderMapper rechargeOrderMapper;

    @Transactional
    public RechargeResponse processRecharge(RechargeRequest request) {
        try {
            // 生成订单号
            String orderNo = UUID.randomUUID().toString().replace("-", "");

            // 创建订单
            RechargeOrder order = RechargeOrder.builder()
                    .orderNo(orderNo)
                    .amount(request.getAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .status(0) // 待支付状态
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            // 保存订单到数据库
            rechargeOrderMapper.insert(order);

            // 构建支付链接（示例链接，实际需要对接支付系统）
            String paymentUrl = "/pay/" + request.getPaymentMethod() + "/" + orderNo;

            // 返回详细的响应
            return RechargeResponse.builder()
                    .orderNo(orderNo)
                    .success(true)
                    .message("充值订单创建成功")
                    .amount(request.getAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .paymentUrl(paymentUrl)
                    .status(0)
                    .build();
        } catch (Exception e) {
            return RechargeResponse.builder()
                    .success(false)
                    .message("创建订单失败：" + e.getMessage())
                    .build();
        }
    }

    public Page<RechargeOrderVO> getRechargeHistory(RechargeOrderQuery query) {
        // 创建分页对象
        Page<RechargeOrder> page = new Page<>(query.getPage(), query.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<RechargeOrder> queryWrapper = new LambdaQueryWrapper<>();

        // 添加状态查询条件
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            Integer statusCode = switch (query.getStatus()) {
                case "success" -> 1;
                case "pending" -> 0;
                case "failed" -> 2;
                default -> null;
            };
            if (statusCode != null) {
                queryWrapper.eq(RechargeOrder::getStatus, statusCode);
            }
        }

        // 添加时间范围查询条件
        if (query.getStartTime() != null) {
            queryWrapper.ge(RechargeOrder::getCreateTime, query.getStartTime());
        }
        if (query.getEndTime() != null) {
            queryWrapper.le(RechargeOrder::getCreateTime, query.getEndTime());
        }

        // 添加排序条件
        queryWrapper.orderByDesc(RechargeOrder::getCreateTime);

        // 执行查询
        Page<RechargeOrder> resultPage = rechargeOrderMapper.selectPage(page, queryWrapper);

        // 转换结果
        Page<RechargeOrderVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage, "records");

        // 转换记录
        List<RechargeOrderVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    private RechargeOrderVO convertToVO(RechargeOrder order) {
        RechargeOrderVO vo = new RechargeOrderVO();
        BeanUtils.copyProperties(order, vo);

        // 转换状态
        vo.setStatus(switch (order.getStatus()) {
            case 0 -> "pending";
            case 1 -> "success";
            case 2 -> "failed";
            default -> "unknown";
        });

        return vo;
    }
}