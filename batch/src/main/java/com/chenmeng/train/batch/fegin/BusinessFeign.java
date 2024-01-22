package com.chenmeng.train.batch.fegin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * business业务模块服务调用客户端（用于调用business业务模块的接口）
 *
 * @author 沉梦听雨
 **/
// @FeignClient("business")
@FeignClient(name = "business", url = "http://127.0.0.1:8002/business")
public interface BusinessFeign {

    /**
     * 调用business业务模块的hello接口
     * @return
     */
    @GetMapping("/test/hello")
    String hello();

}
