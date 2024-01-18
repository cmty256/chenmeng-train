package com.chenmeng.train.batch.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 *
 * @author 沉梦听雨
 * @date 2024/1/18
 **/
@RestController("/test")
public class TestController {

    @GetMapping("/hello")
    public String test() {
        return "Hello World, Batch!";
    }

}
