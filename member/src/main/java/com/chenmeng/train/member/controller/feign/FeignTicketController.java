package com.chenmeng.train.member.controller.feign;

import com.chenmeng.train.common.req.MemberTicketReq;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员模块 feign 接口
 *
 * @author 沉梦听雨
 */
@RestController
@RequestMapping("/feign/ticket")
public class FeignTicketController {

    @Resource
    private TicketService ticketService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody MemberTicketReq dto) throws Exception {
        ticketService.save(dto);
        return new CommonResp<>();
    }
}
