package com.chenmeng.train.member.controller;

import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.member.model.dto.MemberLoginReq;
import com.chenmeng.train.member.model.dto.MemberRegisterReq;
import com.chenmeng.train.member.model.dto.MemberSendCodeReq;
import com.chenmeng.train.member.model.vo.MemberLoginVO;
import com.chenmeng.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 会员控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/count")
    public CommonResp<Integer> count() {
        int count = memberService.count();
        CommonResp<Integer> commonResp = new CommonResp<>();
        commonResp.setContent(count);
        return commonResp;
    }

    /**
     * 注册
     * @param req
     * @return
     */
    @PostMapping("/register")
    public CommonResp<Long> register(@Valid MemberRegisterReq req) {
        long register = memberService.register(req);
        return new CommonResp<>(register);
    }

    /**
     * 发送验证码，含手机号注册功能
     * @param req
     * @return
     */
    @PostMapping("/send-code")
    public CommonResp<Long> sendCode(@Valid @RequestBody MemberSendCodeReq req) {
        memberService.sendCode(req);
        return new CommonResp<>();
    }

    /**
     * 登录
     * @param req
     * @return
     */
    @PostMapping("/login")
    public CommonResp<MemberLoginVO> login(@Valid @RequestBody MemberLoginReq req) {
        MemberLoginVO resp = memberService.login(req);
        return new CommonResp<>(resp);
    }
}
