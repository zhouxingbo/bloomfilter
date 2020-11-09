package com.github.xingbo.spring.strategy;

import org.springframework.stereotype.Service;

@Service
public class AddUserEventServiceImpl implements IBaseEventService {
    @Override
    public boolean dealEvent(String eventObject) {
        return false;
    }

    @Override
    public String getType() {
        return EventTypeEnum.ADD_USER_AFTER.getKey();
    }
}