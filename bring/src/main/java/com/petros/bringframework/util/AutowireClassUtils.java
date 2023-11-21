package com.petros.bringframework.util;

import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.support.AbstractBeanDefinition;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author "Maksym Oliinyk"
 */
public abstract class AutowireClassUtils {

    /**
     * This method identifies the autowired constructor and default constructor (if any) for a given bean class and returns them in a Map.
     * This information is used by the Bpring framework to instantiate beans using dependency injection.
     *
     * @param beanName the name of the bean, as specified for the bean definition
     * @param bd       the BeanDefinition of the bean
     * @return The Boolean key indicates whether the constructor is an autowired constructor (true) or a default constructor, any random if default not exis (false).
     * The value of the Map is the Constructor itself.
     */
    public static Map<Boolean, Constructor<?>> determineCandidateConstructors(String beanName, BeanDefinition bd) {
        Map<Boolean, Constructor<?>> result = new HashMap<>();
        AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) bd;
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?>[] ctors = beanClass.getDeclaredConstructors();
        Constructor<?> autowiredConstructor = null;
        for (Constructor<?> candidate : ctors) {
            boolean isAutowiredConstructorPresent = candidate.isAnnotationPresent(InjectPlease.class);
            if (isAutowiredConstructorPresent) {
                if (autowiredConstructor != null) {
                    throw new BeanCreationException(beanName, "Multiple autowired constructors found: " + autowiredConstructor + " and " + candidate);
                }
                autowiredConstructor = candidate;
                result.put(Boolean.TRUE, autowiredConstructor);
            } else if (candidate.getParameterCount() == 0) {
                result.put(Boolean.FALSE, candidate);
            } else {
                result.putIfAbsent(Boolean.FALSE, candidate);
            }
        }
        return result;
    }

}