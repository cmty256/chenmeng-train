package com.chenmeng.train.business.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.chenmeng.train.business.service.TestService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 *
 * @author 沉梦听雨
 * @date 2023/12/03 17:46
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    private TestService testService;

    @SentinelResource("hello")
    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        int i = RandomUtil.randomInt(1, 10);
        if (i <= 2) {
            throw new RuntimeException("测试异常");
        }
        return "Hello World! Business!";
    }

    @SentinelResource("hello1")
    @GetMapping("/hello1")
    public String hello1() throws InterruptedException {
        testService.hello2();
        return "Hello World! Business1!";
    }
}
