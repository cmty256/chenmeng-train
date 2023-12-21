package com.chenmeng.train.business.controller.admin;

import com.chenmeng.train.business.model.dto.TrainStationQueryDTO;
import com.chenmeng.train.business.model.dto.TrainStationSaveDTO;
import com.chenmeng.train.business.model.vo.TrainStationQueryVO;
import com.chenmeng.train.business.service.TrainStationService;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 列车车站前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/admin/train-station")
public class TrainStationAdminController {

    @Resource
    private TrainStationService trainStationService;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody TrainStationSaveDTO saveReq) {
        trainStationService.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 乘车人分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainStationQueryVO>> queryList(@Valid TrainStationQueryDTO req) {
        PageResp<TrainStationQueryVO> list = trainStationService.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除乘车人接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainStationService.delete(id);
        return new CommonResp<>();
    }
}
