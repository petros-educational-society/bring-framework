package com.petros.bringframework.beans.factory.config;

import javax.annotation.Nullable;

/**
 * Interface that defines a registry for shared bean instances.
 * Can be implemented by {@link com.petros.bringframework.beans.factory.BeanFactory}
 * implementations in order to expose their singleton management facility
 * in a uniform manner.
 *
 * <p>The {@link com.petros.bringframework.beans.factory.ConfigurableBeanFactory} interface extends this interface.
 *
 * @author "Maksym Oliinyk"
 */
public interface SingletonBeanRegistry {

    /**
     * Register the given existing object as singleton in the bean registry,
     * under the given bean name.
     * <p>The given instance is supposed to be fully initialized; the registry
     * will not perform any initialization callbacks method.
     * The given instance will not receive any destruction callbacks
     * (like DisposableBean's {@code destroy} method) either.
     * <p>When running within a full BeanFactory: <b>Register a bean definition
     * instead of an existing instance if your bean is supposed to receive
     * initialization and/or destruction callbacks.</b>
     * <p>Typically invoked during registry configuration, but can also be used
     * for runtime registration of singletons. As a consequence, a registry
     * implementation should synchronize singleton access; it will have to do
     * this anyway if it supports a BeanFactory's lazy initialization of singletons.
     *
     * @param beanName        the name of the bean
     * @param singletonObject the existing singleton object
     */
    void registerSingleton(String beanName, Object singletonObject);

    /**
     * Return the (raw) singleton object registered under the given name.
     * <p>Only checks already instantiated singletons; does not return an Object
     * for singleton bean definitions which have not been instantiated yet.
     *
     * @param beanName the name of the bean to look for
     * @return the registered singleton object, or {@code null} if none found
     */
    @Nullable
    Object getSingleton(String beanName);

    /**
     * Check if this registry contains a singleton instance with the given name.
     * <p>Only checks already instantiated singletons; does not return {@code true}
     * for singleton bean definitions which have not been instantiated yet.
     *
     * @param beanName the name of the bean to look for
     * @return if this bean factory contains a singleton instance with the given name
     */
    boolean containsSingleton(String beanName);

    /**
     * Return the names of singleton beans registered in this registry.
     * <p>Only checks already instantiated singletons; does not return names
     * for singleton bean definitions which have not been instantiated yet.
     *
     * @return the list of names as a String array (never {@code null})
     */
    String[] getSingletonNames();

    /**
     * Return the number of singleton beans registered in this registry.
     * <p>Only checks already instantiated singletons; does not count
     * singleton bean definitions which have not been instantiated yet.
     *
     * @return the number of singleton beans
     */
    int getSingletonCount();
}