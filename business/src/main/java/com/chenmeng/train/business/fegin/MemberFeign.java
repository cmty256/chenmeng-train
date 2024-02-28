package com.chenmeng.train.business.fegin;

import com.chenmeng.train.common.req.MemberTicketReq;
import com.chenmeng.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("member")
// @FeignClient(name = "member", url = "http://127.0.0.1:8001")
public interface MemberFeign {

    /**
     * 会员购票后 新增车票记录接口
     *
     * @param req
     * @return
     */
    @GetMapping("/member/feign/ticket/save")
    CommonResp<Object> save(@RequestBody MemberTicketReq req);

}
