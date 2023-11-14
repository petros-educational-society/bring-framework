package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeansException;

import javax.annotation.Nullable;

//todo: find if needed more details about this bean type
public class NoSuchBeanDefinitionException extends BeansException {
    @Nullable
    private final String beanName;

    @Nullable
    private final Class<?> beanType;

    public NoSuchBeanDefinitionException(@Nullable String name) {
        super("No bean named '" + name + "' available");
        this.beanName = name;
        this.beanType = null;
    }

    public NoSuchBeanDefinitionException(@Nullable String name, String message) {
        super("No bean named '" + name + "' available: " + message);
        this.beanName = name;
        this.beanType = null;
    }

    public NoSuchBeanDefinitionException(@Nullable String name, @Nullable Throwable cause) {
        super(name, cause);
        this.beanName = name;
        this.beanType = null;
    }

    public <T> NoSuchBeanDefinitionException(@Nullable Class<T> requiredType) {
        super("No qualifying bean of type '" + requiredType + "' available");
        this.beanName = null;
        this.beanType = requiredType;
    }
}
