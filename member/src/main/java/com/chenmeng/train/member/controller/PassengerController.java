package com.chenmeng.train.member.controller;

import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.member.model.dto.PassengerQueryReq;
import com.chenmeng.train.member.model.dto.PassengerSaveReq;
import com.chenmeng.train.member.model.vo.PassengerQueryVO;
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

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody PassengerSaveReq saveReq) {
        passengerService.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 乘车人分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<PassengerQueryVO>> queryList(@Valid PassengerQueryReq req) {
        req.setMemberId(LoginMemberContext.getId());
        PageResp<PassengerQueryVO> list = passengerService.queryList(req);
        return new CommonResp<>(list);
    }
}
