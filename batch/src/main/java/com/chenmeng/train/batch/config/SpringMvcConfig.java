package com.chenmeng.train.batch.config;

import com.chenmeng.train.common.interceptor.LogInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置, 注入拦截器
 *
 * @author 沉梦听雨
 */
@Configuration
@ComponentScan(basePackages = "com.chenmeng.train.common")
public class SpringMvcConfig implements WebMvcConfigurer {

   @Resource
   private LogInterceptor logInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(logInterceptor)
              .addPathPatterns("/**");
   }
}
