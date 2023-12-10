package com.chenmeng.train.member.controller;

import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.member.model.dto.PassengerSaveReq;
import com.chenmeng.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 乘车人控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Resource
    private PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody PassengerSaveReq saveReq) {
        passengerService.save(saveReq);
        return new CommonResp<>();
    }
}
