package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.support.GenericBeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author "Maksym Oliinyk"
 */
public interface InstantiationStrategy {
    Object instantiate(GenericBeanDefinition gbd, String beanName, Constructor<?>[] ctors, Object[] explicitArgs);

    Object instantiate(GenericBeanDefinition bd, String beanName, BeanFactory owner, Object factoryBean, Method factoryMethod, Object[] explicitArgs);
}
