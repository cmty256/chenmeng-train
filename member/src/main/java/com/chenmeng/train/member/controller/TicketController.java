package com.chenmeng.train.member.controller;

import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.member.model.dto.TicketQueryDTO;
import com.chenmeng.train.member.model.dto.TicketSaveDTO;
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
@RequestMapping("/ticket")
public class TicketController {

    @Resource
    private TicketService ticketService;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody TicketSaveDTO saveReq) {
        ticketService.save(saveReq);
        return new CommonResp<>();
    }

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

    /**
     * 根据 id 删除车票接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return new CommonResp<>();
    }
}
