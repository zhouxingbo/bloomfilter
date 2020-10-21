package com.github.wxisme.bloomfilter.spring.beanfactory;

/**
 * desc:
 * 工厂模式
 * @author: bobo
 * createDate: 20-10-19
 * Spring通过反射机制利用<bean>的class属性指定实现类实例化Bean，
 * 在某些情况下，实例化Bean过程比较复杂，如果按照传统的方式，则需要在<bean>中提供大量的配置信息
 * 配置方式的灵活性是受限的，这时采用编码的方式可能会得到一个简单的方案。
 * Spring为此提供了一个org.springframework.bean.factory.FactoryBean的工厂类接口，用户可以通过实现该接口定制实例化Bean的逻辑。
 * 从Spring3.0开始，FactoryBean开始支持泛型，即接口声明改为FactoryBean<T>的形式
 */

public class FactoryBeanEx {
}
