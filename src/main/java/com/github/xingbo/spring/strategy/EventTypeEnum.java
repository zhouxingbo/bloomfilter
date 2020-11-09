package com.github.xingbo.spring.strategy;

public enum EventTypeEnum {
    ADD_USER_AFTER("ADD_USER_AFTER"),
	ADD_CUSTOM_AFTER("ADD_CUSTOM_AFTER");

    private String key;

    EventTypeEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}