// package com.chenmeng.train.common.config;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.module.SimpleModule;
// import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
//
// /**
//  * 统一注解, 解决前后端交互 Long 类型精度丢失的问题
//  *
//  * @author 沉梦听雨
//  */
// @Configuration
// public class JacksonConfig {
//
//     @Bean
//     public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
//         // 创建ObjectMapper对象
//         ObjectMapper objectMapper = builder.createXmlMapper(false).build();
//         // 创建SimpleModule对象
//         SimpleModule simpleModule = new SimpleModule();
//         // 添加Long类型的序列化器
//         simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
//         // 注册SimpleModule
//         objectMapper.registerModule(simpleModule);
//         // 返回ObjectMapper对象
//         return objectMapper;
//     }
// }
