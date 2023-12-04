package com.chenmeng.train.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 *
 * @author 沉梦
 * @date 2023/12/03 17:46
 **/
@RestController("/test")
public class TestController {

    @GetMapping("/hello")
    public String test() {
        return "Hello World!";
    }

}