package com.chenmeng.train.business.controller.admin;

import com.chenmeng.train.business.model.dto.TrainQueryDTO;
import com.chenmeng.train.business.model.dto.TrainSaveDTO;
import com.chenmeng.train.business.model.vo.TrainQueryVO;
import com.chenmeng.train.business.service.TrainService;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车次表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/admin/train")
public class TrainAdminController {

    @Resource
    private TrainService trainService;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody TrainSaveDTO saveReq) {
        trainService.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 车次分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainQueryVO>> queryList(@Valid TrainQueryDTO req) {
        PageResp<TrainQueryVO> list = trainService.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除车次接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainService.delete(id);
        return new CommonResp<>();
    }

    /**
     * 获取所有车次信息列表 - 根据车次编号升序
     *
     * @return
     */
    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryVO>> queryList() {
        List<TrainQueryVO> list = trainService.queryAll();
        return new CommonResp<>(list);
    }
}
