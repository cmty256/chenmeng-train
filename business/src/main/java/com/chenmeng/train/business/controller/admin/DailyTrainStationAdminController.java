package com.chenmeng.train.business.controller.admin;

import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.business.model.dto.DailyTrainStationQueryDTO;
import com.chenmeng.train.business.model.dto.DailyTrainStationSaveDTO;
import com.chenmeng.train.business.model.vo.DailyTrainStationQueryVO;
import com.chenmeng.train.business.service.DailyTrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 每日车站表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/admin/daily-train-station")
public class DailyTrainStationAdminController {

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody DailyTrainStationSaveDTO saveReq) {
        dailyTrainStationService.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 每日车站分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainStationQueryVO>> queryList(@Valid DailyTrainStationQueryDTO req) {
        PageResp<DailyTrainStationQueryVO> list = dailyTrainStationService.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除每日车站接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainStationService.delete(id);
        return new CommonResp<>();
    }
}
