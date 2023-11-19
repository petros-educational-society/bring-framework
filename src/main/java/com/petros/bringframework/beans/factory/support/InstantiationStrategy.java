package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.support.GenericBeanDefinition;

/**
 * @author "Maksym Oliinyk"
 */
public interface InstantiationStrategy {
    Object instantiate(GenericBeanDefinition gbd, String beanName);
}
