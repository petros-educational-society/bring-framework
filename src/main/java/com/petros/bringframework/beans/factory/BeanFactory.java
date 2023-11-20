package com.petros.bringframework.beans.factory;

import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.core.type.ResolvableType;

import javax.annotation.Nullable;
import java.util.Map;

public interface BeanFactory {

    String FACTORY_BEAN_PREFIX = "&";

    <T> void configureBeans(T t);

    Object getBean(String name);

    <T> T getBean(Class<T> requiredType);

    boolean containsBean(String name);

    boolean isSingleton(String name);

    boolean isPrototype(String name);

    boolean isTypeMatch(String name, BeanDefinition beanDefinition, ResolvableType typeToMatchh);

    Class<?> getType(String name);

    void destroyBeans();

    String[] getAliases(String name);

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> type);
}
