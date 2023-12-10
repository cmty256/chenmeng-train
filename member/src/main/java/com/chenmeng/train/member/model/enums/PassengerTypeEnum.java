package com.chenmeng.train.member.model.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

/**
 * 乘车人类型枚举
 *
 * @author 沉梦听雨
 **/
public enum PassengerTypeEnum {

    /**
     * 1、给开发人员用的, 枚举类型
     */
    ADULT("1", "成人"),
    CHILD("2", "儿童"),
    STUDENT("3", "学生");

    /**
     * 2、类型编码, 给数据库用的
     */
    private String code;

    /**
     * 3、类型描述, 给前端用的
     */
    private String desc;

    PassengerTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static List<HashMap<String, String>> getEnumList() {
        List<HashMap<String, String>> list = new ArrayList<>();
        for (PassengerTypeEnum anEnum : EnumSet.allOf(PassengerTypeEnum.class)) {
            HashMap<String, String> map = new HashMap<>();
            map.put("code", anEnum.code);
            map.put("desc", anEnum.desc);
            list.add(map);
        }
        return list;
    }
}
