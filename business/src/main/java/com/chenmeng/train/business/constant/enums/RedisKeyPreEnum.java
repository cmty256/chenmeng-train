package com.chenmeng.train.business.constant.enums;

import lombok.Getter;

/**
 * @author 沉梦听雨
 */
@Getter
public enum RedisKeyPreEnum {

    CONFIRM_ORDER("LOCK_CONFIRM_ORDER", "购票锁"),
    SK_TOKEN("LOCK_SK_TOKEN", "令牌锁"),
    SK_TOKEN_COUNT("SK_TOKEN_COUNT", "令牌数");

    /**
     * 锁前缀
     */
    private final String code;

    /**
     * 锁描述
     */
    private final String desc;

    RedisKeyPreEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
