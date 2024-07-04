package com.chenmeng.train.business.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.stereotype.Service;

/**
 * @author 沉梦听雨
 */
@Service
public class TestService {

    @SentinelResource("hello2")
    public void hello2() throws InterruptedException {
        Thread.sleep(500);
    }
}
