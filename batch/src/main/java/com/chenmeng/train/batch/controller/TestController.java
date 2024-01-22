package com.chenmeng.train.batch.controller;

import com.chenmeng.train.batch.fegin.BusinessFeign;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 *
 * @author 沉梦听雨
 * @date 2024/1/18
 **/
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    BusinessFeign businessFeign;

    @GetMapping("/hello")
    public String hello() {
        String businessHello = businessFeign.hello();
        log.info(businessHello);
        return "Hello World! Batch! " + businessHello;
    }

    // @GetMapping("/hello")
    // public String test() {
    //     return "Hello World, Batch!";
    // }
}
