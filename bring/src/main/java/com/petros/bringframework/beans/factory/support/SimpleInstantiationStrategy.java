package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.exception.BeanInstantiationException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.support.GenericBeanDefinition;
import com.petros.bringframework.util.BeanUtils;
import com.petros.bringframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author "Maksym Oliinyk"
 */
public class SimpleInstantiationStrategy implements InstantiationStrategy {

    private static final ThreadLocal<Method> currentlyInvokedFactoryMethod = new ThreadLocal<>();

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
        }
        catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(ctorToUse.getName(), "Is the constructor accessible?", ex);
        }
        catch (IllegalArgumentException ex) {
            throw new BeanInstantiationException(ctorToUse.getName(), "Illegal arguments for constructor", ex);
        }
        catch (InvocationTargetException ex) {
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
                return result;
            }
            finally {
                if (priorInvokedFactoryMethod != null) {
                    currentlyInvokedFactoryMethod.set(priorInvokedFactoryMethod);
                }
                else {
                    currentlyInvokedFactoryMethod.remove();
                }
            }
        } catch (Exception ex) {
            // TODO
            throw new RuntimeException(ex);
        }
        /*catch (IllegalArgumentException ex) {
            throw new BeanInstantiationException(factoryMethod,
                    "Illegal arguments to factory method '" + factoryMethod.getName() + "'; " +
                            "args: " + StringUtils.arrayToCommaDelimitedString(args), ex);
        }
        catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(factoryMethod,
                    "Cannot access factory method '" + factoryMethod.getName() + "'; is it public?", ex);
        }
        catch (InvocationTargetException ex) {
            String msg = "Factory method '" + factoryMethod.getName() + "' threw exception with message: " +
                    ex.getTargetException().getMessage();
            if (bd.getFactoryBeanName() != null && owner instanceof ConfigurableBeanFactory cbf &&
                    cbf.isCurrentlyInCreation(bd.getFactoryBeanName())) {
                msg = "Circular reference involving containing bean '" + bd.getFactoryBeanName() + "' - consider " +
                        "declaring the factory method as static for independence from its containing instance. " + msg;
            }
            throw new BeanInstantiationException(factoryMethod, msg, ex.getTargetException());
        }*/
    }
}
