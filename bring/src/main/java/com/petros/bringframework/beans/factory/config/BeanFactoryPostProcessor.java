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

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     * @param beanFactory the bean factory used by the application context
     */
    void postProcessBeanFactory(BeanFactory beanFactory);
}
