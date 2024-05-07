package com.chenmeng.train.business.controller.web;

import com.chenmeng.train.business.model.dto.ConfirmOrderDoDTO;
import com.chenmeng.train.business.service.ConfirmOrderService;
import com.chenmeng.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 确认订单表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    @Resource
    private ConfirmOrderService confirmOrderService;

    /**
     * 确认订单（抢票）
     *
     * @param dto
     * @return
     */
    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoDTO dto) {
        confirmOrderService.doConfirm(dto);
        return new CommonResp<>();
    }
}
