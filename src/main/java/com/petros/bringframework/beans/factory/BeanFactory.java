package com.petros.bringframework.beans.factory;

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

    String[] getAliases(String name);

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> type);
}
