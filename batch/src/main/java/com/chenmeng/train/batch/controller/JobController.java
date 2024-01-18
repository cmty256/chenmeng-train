package com.chenmeng.train.batch.controller;

import com.chenmeng.train.batch.model.dto.CronJobDTO;
import com.chenmeng.train.batch.model.vo.CronJobVO;
import com.chenmeng.train.common.resp.CommonResp;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定时任务控制器
 *
 * @author 沉梦听雨
 **/
@RestController
@RequestMapping(value = "/admin/job")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JobController {

    private static final Logger LOG = LoggerFactory.getLogger(JobController.class);

    private final SchedulerFactoryBean schedulerFactoryBean;

    /**
     * 手动执行任务
     *
     * @param cronJobDTO
     * @return
     * @throws SchedulerException
     */
    @RequestMapping(value = "/run")
    public CommonResp<Object> run(@RequestBody CronJobDTO cronJobDTO) throws SchedulerException {
        String jobClassName = cronJobDTO.getName();
        String jobGroupName = cronJobDTO.getGroup();
        LOG.info("手动执行任务开始：{}, {}", jobClassName, jobGroupName);
        schedulerFactoryBean.getScheduler().triggerJob(JobKey.jobKey(jobClassName, jobGroupName));
        return new CommonResp<>();
    }

    @RequestMapping(value = "/add")
    public CommonResp<Object> add(@RequestBody CronJobDTO cronJobDTO) {
        String jobClassName = cronJobDTO.getName();
        String jobGroupName = cronJobDTO.getGroup();
        String cronExpression = cronJobDTO.getCronExpression();
        String description = cronJobDTO.getDescription();
        LOG.info("创建定时任务开始：{}，{}，{}，{}", jobClassName, jobGroupName, cronExpression, description);
        CommonResp<Object> commonResp = new CommonResp<>();

        try {
            // 通过SchedulerFactory获取一个调度器实例
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            // 启动调度器
            scheduler.start();

            //构建job信息
            JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) Class.forName(jobClassName)).withIdentity(jobClassName, jobGroupName).build();

            //表达式调度构建器(即任务执行的时间)
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            //按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobGroupName).withDescription(description).withSchedule(scheduleBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            LOG.error("创建定时任务失败:" + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("创建定时任务失败:调度异常");
        } catch (ClassNotFoundException e) {
            LOG.error("创建定时任务失败:" + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("创建定时任务失败：任务类不存在");
        }
        LOG.info("创建定时任务结束：{}", commonResp);
        return commonResp;
    }

    /**
     * 暂停定时任务
     *
     * @param cronJobDTO
     * @return
     */
    @RequestMapping(value = "/pause")
    public CommonResp<Object> pause(@RequestBody CronJobDTO cronJobDTO) {
        String jobClassName = cronJobDTO.getName();
        String jobGroupName = cronJobDTO.getGroup();
        LOG.info("暂停定时任务开始：{}，{}", jobClassName, jobGroupName);
        CommonResp<Object> commonResp = new CommonResp<>();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            LOG.error("暂停定时任务失败:" + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("暂停定时任务失败:调度异常");
        }
        LOG.info("暂停定时任务结束：{}", commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/resume")
    public CommonResp<Object> resume(@RequestBody CronJobDTO cronJobDTO) {
        String jobClassName = cronJobDTO.getName();
        String jobGroupName = cronJobDTO.getGroup();
        LOG.info("重启定时任务开始：{}，{}", jobClassName, jobGroupName);
        CommonResp<Object> commonResp = new CommonResp<>();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            LOG.error("重启定时任务失败:" + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("重启定时任务失败:调度异常");
        }
        LOG.info("重启定时任务结束：{}", commonResp);
        return commonResp;
    }

    /**
     * 修改定时任务 或 重启
     *
     * @param cronJobDTO
     * @return
     */
    @RequestMapping(value = "/reschedule")
    public CommonResp<Object> reschedule(@RequestBody CronJobDTO cronJobDTO) {
        String jobClassName = cronJobDTO.getName();
        String jobGroupName = cronJobDTO.getGroup();
        String cronExpression = cronJobDTO.getCronExpression();
        String description = cronJobDTO.getDescription();
        LOG.info("更新定时任务开始：{}，{}，{}，{}", jobClassName, jobGroupName, cronExpression, description);
        CommonResp<Object> commonResp = new CommonResp<>();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTriggerImpl trigger1 = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
            // 重新设置开始时间
            trigger1.setStartTime(new Date());
            CronTrigger trigger = trigger1;

            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withDescription(description).withSchedule(scheduleBuilder).build();

            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (Exception e) {
            LOG.error("更新定时任务失败:" + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("更新定时任务失败:调度异常");
        }
        LOG.info("更新定时任务结束：{}", commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/delete")
    public CommonResp<Object> delete(@RequestBody CronJobDTO cronJobDTO) {
        String jobClassName = cronJobDTO.getName();
        String jobGroupName = cronJobDTO.getGroup();
        LOG.info("删除定时任务开始：{}，{}", jobClassName, jobGroupName);
        CommonResp<Object> commonResp = new CommonResp<>();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            LOG.error("删除定时任务失败:" + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("删除定时任务失败:调度异常");
        }
        LOG.info("删除定时任务结束：{}", commonResp);
        return commonResp;
    }

    /**
     * 查看所有定时任务列表
     *
     * @return
     */
    @RequestMapping(value="/query")
    public CommonResp<Object> query() {
        LOG.info("查看所有定时任务开始");
        CommonResp<Object> commonResp = new CommonResp<>();
        List<CronJobVO> cronJobDtoList = new ArrayList<>();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    CronJobVO cronJobVO = new CronJobVO();
                    cronJobVO.setName(jobKey.getName());
                    cronJobVO.setGroup(jobKey.getGroup());

                    // get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    CronTrigger cronTrigger = (CronTrigger) triggers.get(0);
                    cronJobVO.setNextFireTime(cronTrigger.getNextFireTime());
                    cronJobVO.setPreFireTime(cronTrigger.getPreviousFireTime());
                    cronJobVO.setCronExpression(cronTrigger.getCronExpression());
                    cronJobVO.setDescription(cronTrigger.getDescription());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(cronTrigger.getKey());
                    cronJobVO.setState(triggerState.name());

                    cronJobDtoList.add(cronJobVO);
                }

            }
        } catch (SchedulerException e) {
            LOG.error("查看定时任务失败:" + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("查看定时任务失败:调度异常");
        }
        commonResp.setContent(cronJobDtoList);
        LOG.info("查看定时任务结束：{}", commonResp);
        return commonResp;
    }

}
