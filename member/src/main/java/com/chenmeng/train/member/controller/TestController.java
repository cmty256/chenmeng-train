package com.chenmeng.train.member.controller;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 *
 * @author 沉梦听雨
 * @date 2023/12/03 17:46
 **/
@RefreshScope
@RestController
@RequestMapping("/test")
public class TestController {

    @Value("${test.nacos:Nacos}")
    private String testNacos;

    @Resource
    private Environment environment;

    @GetMapping("/hello")
    public String hello() {
        String port = environment.getProperty("local.server.port");
        return String.format("Hello %s! 端口：%s", testNacos, port);
    }
}
