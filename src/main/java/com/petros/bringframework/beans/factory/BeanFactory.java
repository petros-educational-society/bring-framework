package com.petros.bringframework.beans.factory;

import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.support.NoSuchBeanDefinitionException;
import javax.annotation.Nullable;
import java.util.Map;

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

    void destroyBeans();

    String[] getAliases(String name);

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> type);
}
