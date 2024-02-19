package com.chenmeng.train.business.controller.web;

import com.chenmeng.train.business.model.vo.StationQueryVO;
import com.chenmeng.train.business.service.StationService;
import com.chenmeng.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/station")
public class StationController {

    @Resource
    private StationService stationService;

    @GetMapping("/query-all")
    public CommonResp<List<StationQueryVO>> queryList() {
        List<StationQueryVO> list = stationService.queryAll();
        return new CommonResp<>(list);
    }
}