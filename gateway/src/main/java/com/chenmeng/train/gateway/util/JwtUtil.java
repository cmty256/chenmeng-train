package com.chenmeng.train.gateway.util;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.crypto.GlobalBouncyCastleProvider;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 沉梦
 */
public class JwtUtil {
    private static final Logger LOG = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * 盐值很重要，不能泄漏，且每个项目都应该不一样，可以放到配置文件中
     */
    private static final String key = "ChenMeng12306";

    /**
     * 生成 token
     * @param id
     * @param mobile
     * @return
     */
    public static String createToken(Long id, String mobile) {
        LOG.info("开始生成JWT token，id：{}，mobile：{}", id, mobile);
        GlobalBouncyCastleProvider.setUseBouncyCastle(false);
        DateTime now = DateTime.now();
        DateTime expTime = now.offsetNew(DateField.HOUR, 24);
        Map<String, Object> payload = new HashMap<>();
        // 签发时间
        payload.put(JWTPayload.ISSUED_AT, now);
        // 过期时间
        payload.put(JWTPayload.EXPIRES_AT, expTime);
        // 生效时间
        payload.put(JWTPayload.NOT_BEFORE, now);
        // 内容
        payload.put("id", id);
        payload.put("mobile", mobile);
        String token = JWTUtil.createToken(payload, key.getBytes());
        LOG.info("生成JWT token：{}", token);
        return token;
    }

    /**
     * 校验 token
     * 成功逻辑: 这个 token 没有被篡改过，且还在有效期内
     *
     * @param token
     * @return true 或者 false
     */
   public static boolean validate(String token) {
        LOG.info("开始JWT token校验，token：{}", token);
        // 设置使用BouncyCastle为false，不使用BouncyCastle
        GlobalBouncyCastleProvider.setUseBouncyCastle(false);
        // 解析token
        JWT jwt = JWTUtil
                .parseToken(token)
                .setKey(key.getBytes());
        // validate包含了verify
        boolean validate = jwt.validate(0);
        LOG.info("JWT token校验结果：{}", validate);
        return validate;
    }

    /**
     * 根据 token 获取原始内容 payloads
     * 工具类一般不依赖项目具体的某个类，所以写成通用的json
     *
     * @param token
     * @return
     */
   public static JSONObject getJSONObject(String token) {
       // todo 增加校验, 校验为 false 时不能获取到原始内容 -- validate(String token)
        // 设置使用BouncyCastle为false
        GlobalBouncyCastleProvider.setUseBouncyCastle(false);
        // 解析token
        JWT jwt = JWTUtil
                .parseToken(token)
                .setKey(key.getBytes());
        // 获取payload
        JSONObject payloads = jwt.getPayloads();
        // 移除时间戳
        payloads.remove(JWTPayload.ISSUED_AT);
        payloads.remove(JWTPayload.EXPIRES_AT);
        payloads.remove(JWTPayload.NOT_BEFORE);
        LOG.info("根据token获取原始内容：{}", payloads);
        return payloads;
    }

    /**
     * 工具类方法测试
     * @param args
     */
    public static void main(String[] args) {
        createToken(1L, "123");

        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE2NzY4OTk4MjcsIm1vYmlsZSI6IjEyMyIsImlkIjoxLCJleHAiOjE2NzY4OTk4MzcsImlhdCI6MTY3Njg5OTgyN30.JbFfdeNHhxKhAeag63kifw9pgYhnNXISJM5bL6hM8eU";
        validate(token);

        getJSONObject(token);
    }
}
