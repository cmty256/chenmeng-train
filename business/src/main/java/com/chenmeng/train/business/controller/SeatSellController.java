package com.chenmeng.train.business.controller;

import com.chenmeng.train.business.model.dto.SeatSellDTO;
import com.chenmeng.train.business.model.vo.SeatSellVO;
import com.chenmeng.train.business.service.DailyTrainSeatService;
import com.chenmeng.train.common.resp.CommonResp;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 座位销售图前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/seat-sell")
public class SeatSellController {

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @GetMapping("/query")
    public CommonResp<List<SeatSellVO>> query(@Valid SeatSellDTO dto) {
        List<SeatSellVO> seatList = dailyTrainSeatService.querySeatSell(dto);
        return new CommonResp<>(seatList);
    }

}
