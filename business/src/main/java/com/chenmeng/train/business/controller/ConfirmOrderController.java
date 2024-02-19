package com.chenmeng.train.business.controller;

import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.business.model.dto.ConfirmOrderQueryDTO;
import com.chenmeng.train.business.model.dto.ConfirmOrderSaveDTO;
import com.chenmeng.train.business.model.vo.ConfirmOrderQueryVO;
import com.chenmeng.train.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody ConfirmOrderSaveDTO saveReq) {
        confirmOrderService.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 确认订单分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<ConfirmOrderQueryVO>> queryList(@Valid ConfirmOrderQueryDTO req) {
        PageResp<ConfirmOrderQueryVO> list = confirmOrderService.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除确认订单接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        confirmOrderService.delete(id);
        return new CommonResp<>();
    }
}
