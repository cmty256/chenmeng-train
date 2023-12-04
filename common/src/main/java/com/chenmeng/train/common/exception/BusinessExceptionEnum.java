package com.chenmeng.train.common.exception;

/**
 * 业务异常枚举类
 *
 * @author 沉梦
 */

public enum BusinessExceptionEnum {

    /**
     * 业务异常枚举
     */
    MEMBER_MOBILE_EXIST("手机号已注册"),
    ;

    private String desc;

    BusinessExceptionEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "BusinessExceptionEnum{" +
                "desc='" + desc + '\'' +
                "} " + super.toString();
    }
}
