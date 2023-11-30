package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.exception.BeanInstantiationException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.support.GenericBeanDefinition;
import com.petros.bringframework.util.BeanUtils;
import com.petros.bringframework.util.ReflectionUtils;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Simple object instantiation strategy for use in a BeanFactory.
 *
 * @author "Maksym Oliinyk"
 */
@Log4j2
public class SimpleInstantiationStrategy implements InstantiationStrategy {

    private static final ThreadLocal<Method> currentlyInvokedFactoryMethod = new ThreadLocal<>();

    /**
     * Return the factory method currently being invoked or {@code null} if none.
     * <p>Allows factory method implementations to determine whether the current
     * caller is the container itself as opposed to user code.
     */
    @Nullable
    public static Method getCurrentlyInvokedFactoryMethod() {
        return currentlyInvokedFactoryMethod.get();
    }

    @Override
    public Object instantiate(GenericBeanDefinition gbd, String beanName, Constructor<?>[] ctors, Object[] explicitArgs) {
        if (ctors == null || ctors.length == 0) {
            throw new BeanCreationException(beanName,
                    "Could not resolve matching constructor on bean class[" + gbd.getBeanClassName() + "]");

        } else if (ctors.length > 1) {
            throw new BeanCreationException(beanName,
                    "Ambiguous constructor matches found on bean class [" + gbd.getBeanClassName() + "]");
        }

        Constructor<?> ctorToUse = ctors[0];
        Class<?>[] paramToUse;
        if (explicitArgs != null && explicitArgs.length > 0) {
            Class<?> explicitArg = (Class<?>) explicitArgs[0];
            paramToUse = new Class<?>[]{explicitArg};
        } else {
            paramToUse = ctorToUse.getParameterTypes();
        }

        Object[] argsWithDefaultValues = new Object[paramToUse.length];
        for (int i = 0; i < paramToUse.length; i++) {
            Class<?> param = paramToUse[i];
            argsWithDefaultValues[i] = getDefaultValue(param);
        }

        try {
            return ctorToUse.newInstance(argsWithDefaultValues);
        } catch (InstantiationException ex) {
            throw new BeanInstantiationException(ctorToUse.getName(), "Is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(ctorToUse.getName(), "Is the constructor accessible?", ex);
        } catch (IllegalArgumentException ex) {
            throw new BeanInstantiationException(ctorToUse.getName(), "Illegal arguments for constructor", ex);
        } catch (InvocationTargetException ex) {
            throw new BeanInstantiationException(ctorToUse.getName(), "Constructor threw exception", ex.getTargetException());
        }
    }

    private static Object getDefaultValue(Class<?> type) {
        if (type.isPrimitive()) {
            return BeanUtils.DEFAULT_TYPE_VALUES.get(type);
        }
        return null;
    }

    @Override
    public Object instantiate(GenericBeanDefinition bd, String beanName, BeanFactory owner, Object factoryBean,
                              final Method factoryMethod, Object[] explicitArgs) {

        try {
            ReflectionUtils.makeAccessible(factoryMethod);

            Method priorInvokedFactoryMethod = currentlyInvokedFactoryMethod.get();
            try {
                currentlyInvokedFactoryMethod.set(factoryMethod);
                Object result = factoryMethod.invoke(factoryBean, explicitArgs);
                if (result == null) {
                    result = new NullBean();
                }
                log.debug("Instantiated bean '{}' using factory method '{}'", beanName, factoryMethod.getName());
                return result;
            } finally {
                if (priorInvokedFactoryMethod != null) {
                    currentlyInvokedFactoryMethod.set(priorInvokedFactoryMethod);
                } else {
                    currentlyInvokedFactoryMethod.remove();
                }
            }
        } catch (Exception ex) {
            log.debug("Failed to instantiate bean '{}' using factory method '{}'", beanName, factoryMethod.getName(), ex);
            throw new RuntimeException(ex);
        }
    }
}
