package com.chenmeng.train.business.model.dto;

import com.chenmeng.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author 沉梦听雨
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class DailyTrainCarriageQueryDTO extends PageReq {

    /**
     * 日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    /**
     * 车次编号
     */
    private String trainCode;

}

