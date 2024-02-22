package com.chenmeng.train.member.controller.admin;

import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.member.model.dto.TicketQueryDTO;
import com.chenmeng.train.member.model.vo.TicketQueryVO;
import com.chenmeng.train.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 车票表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/admin/ticket")
public class TicketAdminController {

    @Resource
    private TicketService ticketService;

    /**
     * 车票分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryVO>> queryList(@Valid TicketQueryDTO req) {
        PageResp<TicketQueryVO> list = ticketService.queryList(req);
        return new CommonResp<>(list);
    }
}
