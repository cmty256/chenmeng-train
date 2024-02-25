package com.chenmeng.train.member.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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

    @GetMapping("/hello")
    public String test() {
        return "Hello World!" + " - " + testNacos;
    }
}
