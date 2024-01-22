package com.chenmeng.train.business.controller.admin;

import com.chenmeng.train.business.model.dto.DailyTrainQueryDTO;
import com.chenmeng.train.business.model.dto.DailyTrainSaveDTO;
import com.chenmeng.train.business.model.vo.DailyTrainQueryVO;
import com.chenmeng.train.business.service.DailyTrainService;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 每日车次表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/admin/daily-train")
public class DailyTrainAdminController {

    @Resource
    private DailyTrainService dailyTrainService;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody DailyTrainSaveDTO saveReq) {
        dailyTrainService.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 每日车次分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainQueryVO>> queryList(@Valid DailyTrainQueryDTO req) {
        PageResp<DailyTrainQueryVO> list = dailyTrainService.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除每日车次接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainService.delete(id);
        return new CommonResp<>();
    }

    /**
     * 根据 时间 生成某日所有车次信息，包括车次、车站、车厢、座位
     *
     * @param date
     * @return
     */
    @GetMapping("/gen-daily/{date}")
    public CommonResp<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        dailyTrainService.genDaily(date);
        return new CommonResp<>();
    }
}
