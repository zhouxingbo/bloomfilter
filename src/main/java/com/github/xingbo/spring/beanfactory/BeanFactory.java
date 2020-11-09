package com.github.xingbo.spring.beanfactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * desc:
 * 简单工厂模式
 * @author: bobo
 * createDate: 20-10-19
 * 1. BeanFactory是基础，BeanFactory和它的子接口定义的API满足了spring环境中对bean管理和配置的需求；
 * 2. ApplicationContext是扩展，以BeanFactory为主线，通过继承的方式综合了环境、国际化、资源、事件等多条支线，
 * 自己又规定了一些扩展服务（如返回context的id，应用名称等），而所有支线都以bean服务为基础；
 *
 * 在Spring中，所有的Bean都是由BeanFactory(也就是IOC容器)来进行管理的。
 * 但对FactoryBean而言，这个Bean不是简单的Bean，而是一个能生产或者修饰对象生成的工厂Bean,它的实现与设计模式中的工厂模式和修饰器模式类似
 *
 * BeanFactory定义了容器的基本形式，SpringIoc的最基本规范。
 */

public class BeanFactory {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml", "applicationContext-part2.xml"});
		BeanFactory factory = (BeanFactory) context;
	}


}
