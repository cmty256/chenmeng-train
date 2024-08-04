package com.chenmeng.train.business.controller;

import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.business.model.dto.SkTokenQueryDTO;
import com.chenmeng.train.business.model.dto.SkTokenSaveDTO;
import com.chenmeng.train.business.model.vo.SkTokenQueryVO;
import com.chenmeng.train.business.service.SkTokenService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀令牌表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/sk-token")
public class SkTokenController {

    @Resource
    private SkTokenService skTokenService;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> save(@Valid @RequestBody SkTokenSaveDTO saveReq) {
        skTokenService.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 秒杀令牌分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<SkTokenQueryVO>> queryList(@Valid SkTokenQueryDTO req) {
        PageResp<SkTokenQueryVO> list = skTokenService.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除秒杀令牌接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        skTokenService.delete(id);
        return new CommonResp<>();
    }
}
