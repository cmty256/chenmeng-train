package com.chenmeng.train.business.controller;

import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.business.model.dto.DailyTrainSeatQueryDTO;
import com.chenmeng.train.business.model.dto.DailyTrainSeatSaveDTO;
import com.chenmeng.train.business.model.vo.DailyTrainSeatQueryVO;
import com.chenmeng.train.business.service.DailyTrainSeatService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 每日座位表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/daily-train-seat")
public class DailyTrainSeatController {

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody DailyTrainSeatSaveDTO saveReq) {
        dailyTrainSeatService.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 每日座位分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainSeatQueryVO>> queryList(@Valid DailyTrainSeatQueryDTO req) {
        PageResp<DailyTrainSeatQueryVO> list = dailyTrainSeatService.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除每日座位接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainSeatService.delete(id);
        return new CommonResp<>();
    }
}
