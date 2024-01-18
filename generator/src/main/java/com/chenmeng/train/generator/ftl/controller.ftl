package com.chenmeng.train.${module}.controller;

import com.chenmeng.train.common.context.LoginMemberContext;
import com.chenmeng.train.common.resp.CommonResp;
import com.chenmeng.train.common.resp.PageResp;
import com.chenmeng.train.${module}.model.dto.${Domain}QueryDTO;
import com.chenmeng.train.${module}.model.dto.${Domain}SaveDTO;
import com.chenmeng.train.${module}.model.vo.${Domain}QueryVO;
import com.chenmeng.train.${module}.service.${Domain}Service;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * ${tableNameCn}表前端控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping("/${do_main}")
public class ${Domain}Controller {

    @Resource
    private ${Domain}Service ${domain}Service;

    /**
     * 新增保存 或 编辑保存 接口
     * @param saveReq
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Integer> count(@Valid @RequestBody ${Domain}SaveDTO saveReq) {
        ${domain}Service.save(saveReq);
        return new CommonResp<>();
    }

    /**
     * ${tableNameCn}分页列表查询接口
     *
     * @param req
     * @return
     */
    @GetMapping("/query-list")
    public CommonResp<PageResp<${Domain}QueryVO>> queryList(@Valid ${Domain}QueryDTO req) {
        PageResp<${Domain}QueryVO> list = ${domain}Service.queryList(req);
        return new CommonResp<>(list);
    }

    /**
     * 根据 id 删除${tableNameCn}接口
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ${domain}Service.delete(id);
        return new CommonResp<>();
    }
}
