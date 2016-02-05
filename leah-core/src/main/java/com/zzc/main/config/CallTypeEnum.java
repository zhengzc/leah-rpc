package com.zzc.main.config;

/**
 * Created by ying on 15/6/2.
 * 调用类型
 */
public enum CallTypeEnum {
    syn("syn"), future("future");

    private final String value;

    CallTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CallTypeEnum getCallType(String value) {
        return CallTypeEnum.valueOf(value);
    }
}
