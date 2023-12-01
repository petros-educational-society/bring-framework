package com.petros.bringframework.beans.factory.config;

import javax.annotation.Nullable;

/**
 * Factory hook that allows for custom modification of new bean instances.
 *
 * @author "Vasiuk Maryna"
 */
public interface BeanPostProcessor {

    /**
     * Apply this {@code BeanPostProcessor} to the given new bean instance <i>before</i> any bean
     * initialization callbacks.
     */
    @Nullable
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }


    /**
     * Apply this {@code BeanPostProcessor} to the given new bean instance <i>after</i> any bean
     * initialization callbacks.
     */
    @Nullable
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
