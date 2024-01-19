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
public class DailyTrainQueryDTO extends PageReq {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String code;

}

