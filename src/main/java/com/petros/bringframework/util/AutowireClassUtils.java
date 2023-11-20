package com.petros.bringframework.util;

import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.factory.annotation.InjectPlease;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.stream.Stream;

import static com.petros.bringframework.util.ClassUtils.CGLIB_CLASS_SEPARATOR;

/**
 * @author "Maksym Oliinyk"
 */
public abstract class AutowireClassUtils {

    public static Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, final String beanName) {
        Constructor<?>[] ctors = beanClass.getDeclaredConstructors();
        Constructor<?> requiredConstructor = null;
        Constructor<?> defaultConstructor = null;
        for (Constructor<?> candidate : ctors) {
            boolean isAutowiredConstructorPresent = candidate.isAnnotationPresent(InjectPlease.class);
            if (!isAutowiredConstructorPresent) {
                if (beanClass.getName().contains(CGLIB_CLASS_SEPARATOR)) {
                    Class<?> superclass = beanClass.getSuperclass();
                    if (superclass != null && superclass != Object.class) {
                        try {
                            final Constructor<?> declaredConstructor = superclass.getDeclaredConstructor(candidate.getParameterTypes());
                            isAutowiredConstructorPresent = superclass.isAnnotationPresent(InjectPlease.class);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            }
            if (isAutowiredConstructorPresent) {
                if (requiredConstructor != null) {
                    throw new BeanCreationException(beanName, "Multiple autowired constructors found: " + requiredConstructor + " and " + candidate);
                }
                requiredConstructor = candidate;
            } else if (candidate.getParameterCount() == 0) {
                defaultConstructor = candidate;
            }
        }
        return Stream.of(requiredConstructor, defaultConstructor)
                     .filter(Objects::nonNull).toArray(Constructor[]::new);
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
                            isAutowiredConstructorPresent = superclass.isAnnotationPresent(InjectPlease.class);
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