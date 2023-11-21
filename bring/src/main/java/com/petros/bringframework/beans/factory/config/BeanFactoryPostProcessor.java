package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.factory.BeanFactory;

/**
 * Factory hook that allows for custom modification of an application context's bean definitions,
 * adapting the bean property values of the context's underlying bean factory.
 *
 * @author "Maksym Oliinyk"
 * @author "Maryna Vasiuk"
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(BeanFactory beanFactory);
}
