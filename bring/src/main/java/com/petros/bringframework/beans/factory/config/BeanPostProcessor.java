package com.petros.bringframework.beans.factory.config;

import javax.annotation.Nullable;

/**
 * Factory hook that allows for custom modification of new bean instances.
 *
 * @author "Vasiuk Maryna"
 */
public interface BeanPostProcessor {

    @Nullable
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Nullable
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
