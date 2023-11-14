package com.petros.bringframework.beans.factory;

import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.support.NoSuchBeanDefinitionException;

public interface BeanFactory {

    Object getBean(String name);

    <T> T getBean(Class<T> requiredType);

    boolean containsBean(String name);

    boolean isSingleton(String name);

    boolean isPrototype(String name);

    boolean isTypeMatch(String name, Class<?> typeToMatch);

    Class<?> getType(String name);

    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws BeanDefinitionStoreException;

    void removeBeanDefinition(String beanName)
            throws NoSuchBeanDefinitionException;

    String[] getAliases(String name);

}
