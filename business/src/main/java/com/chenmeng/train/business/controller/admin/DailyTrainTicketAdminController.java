package com.chenmeng.train.business.controller.admin;

import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.business.model.dto.DailyTrainTicketQueryDTO;
import com.chenmeng.train.business.model.dto.DailyTrainTicketSaveDTO;
import com.chenmeng.train.business.model.vo.DailyTrainTicketQueryVO;
import com.chenmeng.train.business.service.DailyTrainTicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 余票信息表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/admin/daily-train-ticket")
public class DailyTrainTicketAdminController {

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody DailyTrainTicketSaveDTO saveReq) {
        dailyTrainTicketService.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * 余票信息分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryVO>> queryList(@Valid DailyTrainTicketQueryDTO req) {
        PageResp<DailyTrainTicketQueryVO> list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除余票信息接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainTicketService.delete(id);
        return new CommonResp<>();
    }
}
