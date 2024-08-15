package com.chenmeng.train.business.enums;

import lombok.Getter;

/**
 * MQ主题枚举
 *
 * @author 沉梦听雨
 */
@Getter
public enum RocketMQTopicEnum {

    CONFIRM_ORDER("CONFIRM_ORDER", "确认订单排队");

    private final String code;

    private final String desc;

    RocketMQTopicEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "RocketMQTopicEnum{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                "} " + super.toString();
    }
}
