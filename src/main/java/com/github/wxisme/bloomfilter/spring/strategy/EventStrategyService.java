package com.github.wxisme.bloomfilter.spring.strategy;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventStrategyService {

	@Resource(name = "itemBuildMap")
	private Map<String, IBaseEventService> itemBuildMap;

    Map<String, IBaseEventService> eventServiceMap = new HashMap<>();

    /**
     * 构造函数
     * @param eventServices spring容器中所有IBaseEventService的实现类
     */
    public EventStrategyService(List<IBaseEventService> eventServices) {
        for (IBaseEventService eventService : eventServices) {
            eventServiceMap.put(eventService.getType(), eventService);
        }
    }

	@Bean(name = "itemBuildMap")
	public Map<String, IBaseEventService> itemBuildMap(AddUserEventServiceImpl addUserEventService, AddCustomEventServiceImpl addCustomEventService) {
		Map<String, IBaseEventService> map = new HashMap<>();
		map.put(EventTypeEnum.ADD_USER_AFTER.getKey(), addUserEventService);
		map.put(EventTypeEnum.ADD_CUSTOM_AFTER.getKey(), addCustomEventService);
		return map;
	}

    /**
     * 根据事件类型调用不同的实现类处理
	 * 两种调用方式
     */
    public boolean dealEvent(String eventType, String eventObject) throws BizException{
		IBaseEventService eventService = itemBuildMap.get(eventType);
		if (eventService == null){
			throw new BizException("未找到事件处理实现类，eventType：" + eventType);
		}

        eventService = eventServiceMap.get(eventType);
        if (eventService == null){
            throw new BizException("未找到事件处理实现类，eventType：" + eventType);
        }
        return eventService.dealEvent(eventObject);
    }

}