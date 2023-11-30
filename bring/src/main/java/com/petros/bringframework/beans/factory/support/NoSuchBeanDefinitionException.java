package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeansException;

import javax.annotation.Nullable;

/**
 * Exception thrown when a {@code BeanFactory} is asked for a bean instance for which it
 * cannot find a definition. This may point to a non-existing bean, a non-unique bean,
 * or a manually registered singleton instance without an associated bean definition.
 *
 * @author "Viktor Basanets"
 */
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
