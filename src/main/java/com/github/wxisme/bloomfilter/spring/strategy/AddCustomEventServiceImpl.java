package com.github.wxisme.bloomfilter.spring.strategy;

import org.springframework.stereotype.Service;

@Service
public class AddCustomEventServiceImpl implements IBaseEventService {
    @Override
    public boolean dealEvent(String eventObject) {
        // TODO 业务处理逻辑
        return false;
    }

    @Override
    public String getType() {
        return EventTypeEnum.ADD_USER_AFTER.getKey();
    }
}