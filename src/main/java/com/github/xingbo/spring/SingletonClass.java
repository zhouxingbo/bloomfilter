package com.github.xingbo.spring;

/**
 * desc:
 * Spring依赖注入Bean实例默认是单例的。Spring的依赖注入（包括lazy-init方式）都是发生在AbstractBeanFactory的getBean里。
 * getBean的doGetBean方法调用getSingleton进行bean的创建。分析getSingleton()方法
 * 线程不安全的
 * @author: bobo
 * createDate: 20-10-20
 */
public class SingletonClass {
}
