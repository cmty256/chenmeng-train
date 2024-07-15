package com.chenmeng.train.batch.fegin;

import com.chenmeng.train.common.resp.CommonResp;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Feign接口降级类
 *
 * @author 沉梦听雨
 */
@Component
public class BusinessFeignFallback implements BusinessFeign {
    @Override
    public String hello() {
        return "Fallback";
    }

    @Override
    public CommonResp<Object> genDaily(Date date) {
        return null;
    }
}
