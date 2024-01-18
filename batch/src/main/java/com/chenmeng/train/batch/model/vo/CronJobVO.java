package com.chenmeng.train.batch.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * CronJob 返回视图
 *
 * @author 沉梦听雨
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CronJobVO {

    private String group;

    private String name;

    private String description;

    private String state;

    /** cron表达式 */
    private String cronExpression;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date nextFireTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date preFireTime;

}
