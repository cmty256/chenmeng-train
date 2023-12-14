package com.chenmeng.train.member.controller;

import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.member.model.dto.${Domain}QueryDTO;
import com.chenmeng.train.member.model.dto.${Domain}SaveDTO;
import com.chenmeng.train.member.model.vo.${Domain}QueryVO;
import com.chenmeng.train.member.service.${Domain}Service;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * ${do_main}前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/${do_main}")
public class ${Domain}Controller {

    @Resource
    private ${Domain}Service ${domain}Service;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody ${Domain}SaveDTO saveReq) {
        ${domain}Service.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 乘车人分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<${Domain}QueryVO>> queryList(@Valid ${Domain}QueryDTO req) {
        req.setMemberId(LoginMemberContext.getId());
        PageResp<${Domain}QueryVO> list = ${domain}Service.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除乘车人接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ${domain}Service.delete(id);
        return new CommonResp<>();
    }
}