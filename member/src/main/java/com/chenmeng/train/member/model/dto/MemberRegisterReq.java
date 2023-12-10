package com.chenmeng.train.member.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @author 沉梦听雨
 */
public class MemberRegisterReq {

    @NotBlank(message = "【手机号】不能为空")
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "MemberRegisterReq{" +
                "mobile='" + mobile + '\'' +
                '}';
    }
}