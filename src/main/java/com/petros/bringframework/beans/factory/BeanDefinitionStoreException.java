package com.petros.bringframework.beans.factory;

import com.petros.bringframework.beans.FatalBeansException;

import javax.annotation.Nullable;

public class BeanDefinitionStoreException extends FatalBeansException {

    @Nullable
    private final String resourceDescription;

    @Nullable
    private final String beanName;

    public BeanDefinitionStoreException(String msg) {
        super(msg);
        this.resourceDescription = null;
        this.beanName = null;
    }

    public BeanDefinitionStoreException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
        this.resourceDescription = null;
        this.beanName = null;
    }

    public BeanDefinitionStoreException(@Nullable String resourceDescription, String msg) {
        super(msg);
        this.resourceDescription = resourceDescription;
        this.beanName = null;
    }

    public BeanDefinitionStoreException(@Nullable String resourceDescription, String msg, @Nullable Throwable cause) {
        super(msg, cause);
        this.resourceDescription = resourceDescription;
        this.beanName = null;
    }

    public BeanDefinitionStoreException(@Nullable String resourceDescription, String beanName, String msg) {
        this(resourceDescription, beanName, msg, null);
    }

    public BeanDefinitionStoreException(
            @Nullable String resourceDescription, @Nullable String beanName, String msg, @Nullable Throwable cause) {

        super("Invalid bean definition with name '" + beanName + "' defined in " + resourceDescription + ": " + msg,
                cause);
        this.resourceDescription = resourceDescription;
        this.beanName = beanName;
    }


    @Nullable
    public String getResourceDescription() {
        return this.resourceDescription;
    }

    @Nullable
    public String getBeanName() {
        return this.beanName;
    }
}
