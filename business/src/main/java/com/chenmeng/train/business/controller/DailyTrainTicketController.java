package com.chenmeng.train.business.controller;

import com.chenmeng.train.business.model.dto.DailyTrainTicketQueryDTO;
import com.chenmeng.train.business.model.vo.DailyTrainTicketQueryVO;
import com.chenmeng.train.business.service.DailyTrainTicketService;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户端余票信息表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    /**
     * 余票信息分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryVO>> queryList(@Valid DailyTrainTicketQueryDTO req) {
        PageResp<DailyTrainTicketQueryVO> list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }
}
