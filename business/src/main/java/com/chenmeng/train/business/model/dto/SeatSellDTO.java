package com.chenmeng.train.business.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author 沉梦听雨
 */
@Data
public class SeatSellDTO {

    /**
     * 日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "【日期】不能为空")
    private Date date;

    /**
     * 车次编号
     */
    @NotNull(message = "【车次编号】不能为空")
    private String trainCode;
}
