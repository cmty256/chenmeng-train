package com.chenmeng.train.business.model.dto;

import com.chenmeng.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 沉梦听雨
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class DailyTrainSeatQueryDTO extends PageReq {

    /**
     * 车次编号
     */
    private String trainCode;

}

