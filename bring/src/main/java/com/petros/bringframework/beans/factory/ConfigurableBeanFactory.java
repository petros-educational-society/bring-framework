package com.petros.bringframework.beans.factory;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.factory.config.BeanFactoryPostProcessor;
import com.petros.bringframework.beans.factory.config.BeanPostProcessor;
import com.petros.bringframework.beans.factory.config.SingletonBeanRegistry;
import com.petros.bringframework.beans.factory.support.NoSuchBeanDefinitionException;

import java.util.List;

/**
 * @author "Maksym Oliinyk"
 */
public interface ConfigurableBeanFactory extends BeanFactory, SingletonBeanRegistry {

    /**
     * Ensure that all non-lazy-init singletons are instantiated.
     *
     * @throws BeansException if one of the singleton beans could not be created.
     *                        Note: This may have left the factory with some beans already initialized!
     */
    void preInstantiateSingletons() throws BeansException;

    /**
     * Add a new BeanPostProcessor that will get applied to beans created
     * by this factory. To be invoked during factory configuration.
     * <p>Note: Post-processors submitted here will be applied in the order of
     * registration;
     *
     * @param beanPostProcessor the post-processor to register
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    /**
     * Determine whether the bean with the given name is a FactoryBean.
     *
     * @param name the name of the bean to check
     * @return whether the bean is a FactoryBean
     * ({@code false} means the bean exists but is not a FactoryBean)
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     */
    default boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException {
        return false;
    }

    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor);

    List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors();
}
