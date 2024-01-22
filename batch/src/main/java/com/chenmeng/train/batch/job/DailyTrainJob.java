package com.chenmeng.train.batch.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.chenmeng.train.batch.fegin.BusinessFeign;
import com.chenmeng.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;

import java.util.Date;

/**
 * 每日车次定时任务
 *
 * @author 沉梦听雨
 */
@DisallowConcurrentExecution
@Slf4j
public class DailyTrainJob implements Job {

    @Resource
    private BusinessFeign businessFeign;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        log.info("生成15天后的车次数据开始");
        Date date = new Date();
        // 获取当前日期后 15 天的日期
        DateTime dateTime = DateUtil.offsetDay(date, 15);
        // DateTime 转换为 Date 类型
        Date offsetDate = dateTime.toJdkDate();
        CommonResp<Object> commonResp = businessFeign.genDaily(offsetDate);
        log.info("生成15天后的车次数据结束，结果：{}", commonResp);
    }
}
