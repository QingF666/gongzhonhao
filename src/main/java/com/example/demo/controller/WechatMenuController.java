package com.example.demo.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/wechat/menu")
public class WechatMenuController {

    @Value("${wechat.appId}")
    private String appId;

    @Value("${wechat.appSecret}")
    private String appSecret;

    /**
     * 创建自定义菜单
     */
    @PostMapping("/create")
    public R<String> createMenu() {
        try {
            // 获取access_token
            String accessToken = getAccessToken();

            // 构造菜单JSON
            JSONObject menuJson = new JSONObject();
            JSONArray buttonArray = new JSONArray();

            // 充值续费按钮（跳转链接）
            JSONObject rechargeButton = new JSONObject();
            rechargeButton.put("type", "view");
            rechargeButton.put("name", "充值续费");
            rechargeButton.put("url", "https://www.ygbjq.cn");

            // 扫一扫按钮（扫码等待）
            JSONObject scanButton = new JSONObject();
            scanButton.put("type", "scancode_waitmsg");
            scanButton.put("name", "扫一扫");
            scanButton.put("key", "scan_wait_button");

            // 充值记录按钮（跳转链接）
            JSONObject recordButton = new JSONObject();
            recordButton.put("type", "view");
            recordButton.put("name", "充值记录");
            recordButton.put("url", "https://www.ygbjq.cn");

            // 添加按钮到数组
            buttonArray.add(rechargeButton);
            buttonArray.add(scanButton);
            buttonArray.add(recordButton);
            menuJson.put("button", buttonArray);

            // 发送创建菜单请求
            String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken;
            String result = HttpUtil.post(url, JSONObject.toJSONString(menuJson));

            // 解析返回结果
            JSONObject jsonResult = JSONObject.parseObject(result);
            if (jsonResult.getIntValue("errcode") == 0) {
                return R.ok("创建菜单成功");
            } else {
                return R.error(jsonResult.getString("errmsg"));
            }

        } catch (Exception e) {
            log.error("创建自定义菜单异常", e);
            return R.error("创建菜单失败");
        }
    }

    /**
     * 获取access_token
     */
    private String getAccessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        String result = HttpUtil.get(url);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject.getString("access_token");
    }
}