package com.chenmeng.train.business.config;

import com.chenmeng.train.common.interceptor.LogInterceptor;
import com.chenmeng.train.common.interceptor.MemberInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置, 注入拦截器
 *
 * @author 沉梦听雨
 */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

   @Resource
   private LogInterceptor logInterceptor;

   @Resource
   private MemberInterceptor memberInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(logInterceptor)
              .addPathPatterns("/**");

      registry.addInterceptor(memberInterceptor)
              .addPathPatterns("/**")
              .excludePathPatterns(
                      "/hello"
              );
   }
}
