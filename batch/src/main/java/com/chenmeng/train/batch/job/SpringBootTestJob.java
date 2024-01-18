// package com.chenmeng.train.batch.job;
//
// import org.springframework.scheduling.annotation.EnableScheduling;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// /**
//  * SpringBoot 自带定时任务测试
//  * 注意：需要开启定时任务注解 @EnableScheduling
//  *
//  * @author 沉梦听雨
//  */
// @Component
// @EnableScheduling
// public class SpringBootTestJob {
//
//     /* 适合单体应用，不适合集群
//        无法实时更改定时任务状态和策略 */
//
//     /**
//      * 每 5 秒执行一次
//      */
//     @Scheduled(cron = "0/5 * * * * ?")
//     private void test() {
//         // 增加分布式锁，可解决集群问题
//         System.out.println("SpringBootTestJob TEST");
//     }
// }
