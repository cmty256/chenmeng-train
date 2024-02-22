package com.chenmeng.train.member.model.dto;

import com.chenmeng.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 沉梦听雨
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class TicketQueryDTO extends PageReq {

    private Long memberId;

}

