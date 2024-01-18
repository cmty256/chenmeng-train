package com.chenmeng.train.batch.model.dto;

import lombok.Data;

/**
 * CronJob 请求体
 *
 * @author 沉梦听雨
 **/
@Data
public class CronJobDTO {

    private String group;

    private String name;

    private String description;

    /** cron表达式 */
    private String cronExpression;

}
