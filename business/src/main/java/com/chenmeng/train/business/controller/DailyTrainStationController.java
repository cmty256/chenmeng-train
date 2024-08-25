package com.chenmeng.train.business.controller;

import com.chenmeng.train.business.model.dto.DailyTrainStationQueryAllDTO;
import com.chenmeng.train.business.model.vo.DailyTrainStationQueryVO;
import com.chenmeng.train.business.service.DailyTrainStationService;
import com.chenmeng.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 每日车站表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/daily-train-station")
public class DailyTrainStationController {

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @GetMapping("/query-by-train-code")
    public CommonResp<List<DailyTrainStationQueryVO>> queryByTrain(@Valid DailyTrainStationQueryAllDTO req) {
        List<DailyTrainStationQueryVO> list = dailyTrainStationService.queryByTrain(req.getDate(), req.getTrainCode());
        return new CommonResp<>(list);
    }
}
