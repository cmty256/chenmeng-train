package com.chenmeng.train.business.controller.admin;

import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.business.model.dto.TrainQueryDTO;
import com.chenmeng.train.business.model.dto.TrainSaveDTO;
import com.chenmeng.train.business.model.vo.TrainQueryVO;
import com.chenmeng.train.business.service.TrainService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 车次前端控制器
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
     * 乘车人分页列表查询接口
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
     * 根据 id 删除乘车人接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainService.delete(id);
        return new CommonResp<>();
    }
}
