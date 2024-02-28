package com.chenmeng.train.batch.fegin;

import com.chenmeng.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

/**
 * business业务模块服务调用客户端（用于调用business业务模块的接口）
 *
 * @author 沉梦听雨
 **/
@FeignClient("business")
// @FeignClient(name = "business", url = "http://127.0.0.1:8002/business")
public interface BusinessFeign {

    /**
     * 调用business业务模块的hello接口
     *
     * @return
     */
    @GetMapping("/business/test/hello")
    String hello();

    /**
     * 根据 时间 生成某日所有车次信息，包括车次、车站、车厢、座位
     *
     * @param date
     * @return
     */
    @GetMapping("/business/admin/daily-train/gen-daily/{date}")
    CommonResp<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);
}
