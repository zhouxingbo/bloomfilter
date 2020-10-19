package com.github.wxisme.bloomfilter.spring.strategy;

public interface IBaseEventService {

    /**
     * 处理事件
     * @param eventObject
     * @return
     * @throws Exception
     */
    public boolean dealEvent(String eventObject);

    /**
     * 获取事件类型
     * @return
     */
    public String getType();

}