package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.factory.BeanDefinitionStoreException;
import com.petros.bringframework.beans.factory.config.BeanDefinition;

public interface BeanDefinitionRegistry {


    /**
     * Register a new bean definition with this registry
     * @param beanName bean instance name to register
     * @param beanDefinition bean instance definition to register
     * @throws BeanDefinitionStoreException if the BeanDefinition is invalid
     * @throws BeanDefinitionOverrideException if there is already a BeanDefinition
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException;

    /**
     * Remove the BeanDefinition for the given name
     * @param beanName bean instance name to register
     * @throws NoSuchBeanDefinitionException if there is no such bean definition
     */
    void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    /**
     * Return the BeanDefinition for the given bean name
     * @param beanName bean name to find a definition for
     * @return the BeanDefinition for the given name
     * @throws NoSuchBeanDefinitionException if there is no such bean definition
     */
    BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    /**
     * @param beanName
     * @return
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * @return
     */
    String[] getBeanDefinitionNames();

    /**
     * @return
     */
    int getBeanDefinitionCount();

    /**
     * @param beanName
     * @return
     */
    boolean isBeanNameInUse(String beanName);

    void registerAlias(String beanName, String alias);
}
