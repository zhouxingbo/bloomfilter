package com.github.wxisme.bloomfilter.spring.beanfactory;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 1 <bean id="fbHelloWorldService" class="com.github.wxisme.bloomfilter.spring.beanfactory.MyFactoryBean">
 * 2     <property name="interfaceName" value="com.ebao.xxx.HelloWorldService" />
 * 3     <property name="target" ref="helloWorldService" />
 * 4 </bean>
 */
public class MyFactoryBean implements FactoryBean<Object>, InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(MyFactoryBean.class);
	private String interfaceName;
	private Object target;
	private Object proxyObj;

	@Autowired
	private ApplicationContext context;

	@Override
	public void destroy() throws Exception {
		logger.debug("destroy......");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		proxyObj = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Class.forName(interfaceName)},
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						logger.debug("invoke method......" + method.getName());
						logger.debug("invoke method before......" + System.currentTimeMillis());
						Object result = method.invoke(target, args);
						logger.debug("invoke method after......" + System.currentTimeMillis());
						return result;
					}
				});
		logger.debug("afterPropertiesSet......");
	}

	@Override
	public Object getObject() throws Exception {
		logger.debug("getObject......");
		return proxyObj;
	}

	@Override
	public Class<?> getObjectType() {
		return proxyObj == null ? Object.class : proxyObj.getClass();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Test
	public void testFactoryBean() {
		HelloWorldService helloWorldService = (HelloWorldService) context.getBean("fbHelloWorldService");
		helloWorldService.sayHello();
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Object getProxyObj() {
		return proxyObj;
	}

	public void setProxyObj(Object proxyObj) {
		this.proxyObj = proxyObj;
	}
}