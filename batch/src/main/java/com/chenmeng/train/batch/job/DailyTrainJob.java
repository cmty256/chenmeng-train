package com.chenmeng.train.batch.job;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;

/**
 * 每日车次定时任务
 *
 * @author 沉梦听雨
 */
@DisallowConcurrentExecution
@Slf4j
public class DailyTrainJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        log.info("生成15天后的车次数据开始");
        log.info("生成15天后的车次数据结束");
    }
}
