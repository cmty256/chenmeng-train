package com.chenmeng.train.business.controller.admin;

import com.chenmeng.train.business.model.dto.StationQueryDTO;
import com.chenmeng.train.business.model.dto.StationSaveDTO;
import com.chenmeng.train.business.model.vo.StationQueryVO;
import com.chenmeng.train.business.service.StationService;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 车站表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/admin/station")
public class StationAdminController {

    @Resource
    private StationService stationService;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody StationSaveDTO saveReq) {
        stationService.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 车站分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<StationQueryVO>> queryList(@Valid StationQueryDTO req) {
        PageResp<StationQueryVO> list = stationService.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除车站接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        stationService.delete(id);
        return new CommonResp<>();
    }
}
