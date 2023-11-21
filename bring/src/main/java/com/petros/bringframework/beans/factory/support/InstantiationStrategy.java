package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.support.GenericBeanDefinition;

import java.lang.reflect.Constructor;

/**
 * @author "Maksym Oliinyk"
 */
public interface InstantiationStrategy {
    Object instantiate(GenericBeanDefinition gbd, String beanName, Constructor<?>[] ctors, Object[] explicitArgs);
}
