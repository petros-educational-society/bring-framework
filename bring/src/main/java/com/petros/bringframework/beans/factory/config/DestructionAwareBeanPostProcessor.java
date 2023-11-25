package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.BeansException;

/**
 * Subinterface of {@link AnnotationBeanPostProcessor} that adds a before-destruction callback.
 *
 * <p>The typical usage will be to invoke custom destruction callbacks on
 * specific bean types, matching corresponding initialization callbacks.
 *
 * @author "Vasiuk Maryna"
 */
public interface DestructionAwareBeanPostProcessor extends AnnotationBeanPostProcessor {

    /**
     * Apply this BeanPostProcessor to the given bean instance before its
     * destruction, e.g. invoking custom destruction callbacks.
     * <p>Like DisposableBean's {@code destroy} and a custom destroy method, this
     * callback will only apply to beans which the container fully manages the
     * lifecycle for. This is usually the case for singletons and scoped beans.
     * @param bean the bean instance to be destroyed
     * @param beanName the name of the bean
     * @throws com.petros.bringframework.beans.BeansException in case of errors
     */
    void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException;
}

