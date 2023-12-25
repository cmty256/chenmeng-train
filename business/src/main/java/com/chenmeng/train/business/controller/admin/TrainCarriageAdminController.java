package com.chenmeng.train.business.controller.admin;

import com.chenmeng.train.business.model.dto.TrainCarriageQueryDTO;
import com.chenmeng.train.business.model.dto.TrainCarriageSaveDTO;
import com.chenmeng.train.business.model.vo.TrainCarriageQueryVO;
import com.chenmeng.train.business.service.TrainCarriageService;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 列车车厢表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/admin/train-carriage")
public class TrainCarriageAdminController {

    @Resource
    private TrainCarriageService trainCarriageService;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody TrainCarriageSaveDTO saveReq) {
        trainCarriageService.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 列车车厢分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainCarriageQueryVO>> queryList(@Valid TrainCarriageQueryDTO req) {
        PageResp<TrainCarriageQueryVO> list = trainCarriageService.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除列车车厢接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainCarriageService.delete(id);
        return new CommonResp<>();
    }
}
