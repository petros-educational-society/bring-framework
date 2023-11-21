package com.petros.bringframework.util;

import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.support.AbstractBeanDefinition;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.petros.bringframework.util.ClassUtils.CGLIB_CLASS_SEPARATOR;

/**
 * @author "Maksym Oliinyk"
 */
public abstract class AutowireClassUtils {

    /**
     * false - default constructor, or any random if default not exist
     * true - autowired constructor
     * @param name
     * @param bd
     * @return
     */
    public static Map<Boolean, Constructor<?>> determineCandidateConstructors(String name, BeanDefinition bd) {
        Map<Boolean, Constructor<?>> result = new HashMap<>();
        AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition)bd;
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?>[] ctors = beanClass.getDeclaredConstructors();
        Constructor<?> autowiredConstructor = null;
        for (Constructor<?> candidate : ctors) {
            boolean isAutowiredConstructorPresent = candidate.isAnnotationPresent(InjectPlease.class);
            if (isAutowiredConstructorPresent) {
                if (autowiredConstructor != null) {
                    throw new BeanCreationException(name, "Multiple autowired constructors found: " + autowiredConstructor + " and " + candidate);
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

    public static Constructor<?>[] determineInjectCandidateConstructor(final Class<?> beanClass, final String beanName) {
        Constructor<?>[] ctors = beanClass.getDeclaredConstructors();
        Constructor<?> autowiredConstructor = null;
        for (Constructor<?> candidate : ctors) {
            boolean isAutowiredConstructorPresent = candidate.isAnnotationPresent(InjectPlease.class);
            if (!isAutowiredConstructorPresent) {
                if (beanClass.getName().contains(CGLIB_CLASS_SEPARATOR)) {
                    Class<?> superclass = beanClass.getSuperclass();
                    if (superclass != null && superclass != Object.class) {
                        try {
                            final Constructor<?> declaredConstructor = superclass.getDeclaredConstructor(candidate.getParameterTypes());
                            isAutowiredConstructorPresent = declaredConstructor.isAnnotationPresent(InjectPlease.class);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            }
            if (isAutowiredConstructorPresent) {
                if (autowiredConstructor != null) {
                    throw new BeanCreationException(beanName, "Multiple autowired constructors found: " + autowiredConstructor + " and " + candidate);
                }
                autowiredConstructor = candidate;
            }
        }

        return Stream.of(autowiredConstructor)
                     .filter(Objects::nonNull).toArray(Constructor[]::new);
    }

}