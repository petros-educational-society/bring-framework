package com.petros.bringframework.beans.factory;

import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.factory.config.BeanDefinition;

public interface BeanFactory {

    Object getBean(String name);

    <T> T getBean(Class<T> requiredType);

    boolean containsBean(String name);

    boolean isSingleton(String name);

    boolean isPrototype(String name);

    boolean isTypeMatch(String name, Class<?> typeToMatch);

    Class<?> getType(String name);

    String[] getAliases(String name);

    /**
     * Create a bean instance for the given merged bean definition (and arguments).
     * The bean definition will already have been merged with the parent definition
     * in case of a child definition.
     * <p>All bean retrieval methods delegate to this method for actual bean creation.
     *
     * @param beanName the name of the bean
     * @param mbd      the merged bean definition for the bean
     * @param args     explicit arguments to use for constructor or factory method invocation
     * @return a new instance of the bean
     * @throws BeanCreationException if the bean could not be created
     */
    Object createBean(String beanName, BeanDefinition bd) throws BeanCreationException;

}
