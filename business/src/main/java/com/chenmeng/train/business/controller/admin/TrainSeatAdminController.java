package com.chenmeng.train.business.controller.admin;

import com.chenmeng.train.business.model.dto.TrainSeatQueryDTO;
import com.chenmeng.train.business.model.dto.TrainSeatSaveDTO;
import com.chenmeng.train.business.model.vo.TrainSeatQueryVO;
import com.chenmeng.train.business.service.TrainSeatService;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 座位表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/admin/train-seat")
public class TrainSeatAdminController {

    @Resource
    private TrainSeatService trainSeatService;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody TrainSeatSaveDTO saveReq) {
        trainSeatService.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 座位分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainSeatQueryVO>> queryList(@Valid TrainSeatQueryDTO req) {
        PageResp<TrainSeatQueryVO> list = trainSeatService.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除座位接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainSeatService.delete(id);
        return new CommonResp<>();
    }
}
