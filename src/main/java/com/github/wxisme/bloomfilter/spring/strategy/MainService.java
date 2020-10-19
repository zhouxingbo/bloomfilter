package com.github.wxisme.bloomfilter.spring.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * desc:
 *
 * @author: bobo
 * 经典写法if…else if…else…
 * 代码重构:spring-策略模式
 * createDate: 20-10-19
 */
@Service
@Slf4j
public class MainService {

	@Autowired
	private static ApplicationContext applicationContext;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse httpServletResponse;

	private HttpSession httpSession;

	@Autowired
	private EventStrategyService eventStrategyService;

	public void main(){
		String userObject = "userObject";

		//处理事件
		try {
			eventStrategyService.dealEvent("ADD_USER_AFTER", userObject);
		} catch (BizException e) {
			log.error("error");
		}
	}

	public void beanFactory(){
		String userObject = "userObject";
		//处理事件
		try {
			BeanFactory beanFactory = new XmlWebApplicationContext();
			EventStrategyService userService = (EventStrategyService)beanFactory.getBean("userService");
			userService.dealEvent("ADD_USER_AFTER", userObject);

		} catch (Exception e) {
			log.error("error");
		}
	}


}
