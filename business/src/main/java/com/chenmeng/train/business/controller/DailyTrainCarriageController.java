package com.chenmeng.train.business.controller;

import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.business.model.dto.DailyTrainCarriageQueryDTO;
import com.chenmeng.train.business.model.dto.DailyTrainCarriageSaveDTO;
import com.chenmeng.train.business.model.vo.DailyTrainCarriageQueryVO;
import com.chenmeng.train.business.service.DailyTrainCarriageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 每日车厢表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/daily-train-carriage")
public class DailyTrainCarriageController {

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody DailyTrainCarriageSaveDTO saveReq) {
        dailyTrainCarriageService.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 每日车厢分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainCarriageQueryVO>> queryList(@Valid DailyTrainCarriageQueryDTO req) {
        PageResp<DailyTrainCarriageQueryVO> list = dailyTrainCarriageService.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除每日车厢接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainCarriageService.delete(id);
        return new CommonResp<>();
    }
}
