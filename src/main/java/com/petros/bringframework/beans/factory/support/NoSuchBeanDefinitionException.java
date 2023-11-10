package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeanException;

import javax.annotation.Nullable;

//todo: find if needed more details about type of bean
public class NoSuchBeanDefinitionException extends BeanException {
    @Nullable
    private final String beanName;

    public NoSuchBeanDefinitionException(@Nullable String name) {
        super("No bean named '" + name + "' available");
        this.beanName = name;
    }

    public NoSuchBeanDefinitionException(@Nullable String name, String message) {
        super("No bean named '" + name + "' available: " + message);
        this.beanName = name;
    }

    public NoSuchBeanDefinitionException(@Nullable String name, @Nullable Throwable cause) {
        super(name, cause);
        this.beanName = name;
    }
}
