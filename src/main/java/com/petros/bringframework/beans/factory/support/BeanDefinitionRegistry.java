package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.factory.BeanDefinitionStoreException;
import com.petros.bringframework.beans.factory.config.BeanDefinition;

public interface BeanDefinitionRegistry {


    /**
     * Register a new bean definition with this registry.
     * @param beanName bean instance name to register
     * @param beanDefinition bean instance definition to register
     * @throws BeanDefinitionStoreException if the BeanDefinition is invalid
     * @throws BeanDefinitionOverrideException if there is already a BeanDefinition
     * for the specified bean name and we are not allowed to override it
     * @see GenericBeanDefinition
     * @see RootBeanDefinition
     * @see ChildBeanDefinition
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException;

    /**
     * @param beanName
     * @throws NoSuchBeanDefinitionException
     */
    void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    /**
     * @param beanName
     * @return
     * @throws NoSuchBeanDefinitionException
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
