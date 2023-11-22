package com.petros.bringframework.context.support;

import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.exception.BeanInstantiationException;
import com.petros.bringframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import com.petros.bringframework.beans.factory.support.BeanWrapper;
import com.petros.bringframework.beans.support.GenericBeanDefinition;
import com.petros.bringframework.util.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Delegate for resolving constructors and factory methods.
 *
 * @author "Maksym Oliinyk"
 */
public class ConstructorResolver {

    private final AbstractAutowireCapableBeanFactory beanFactory;

    public ConstructorResolver(AbstractAutowireCapableBeanFactory abstractAutowireCapableBeanFactory) {
        this.beanFactory = abstractAutowireCapableBeanFactory;
    }

    public BeanWrapper autowireConstructor(String beanName,
                                           GenericBeanDefinition mbd,
                                           Constructor<?>[] ctors,
                                           Object[] explicitArgs) {
        if (ctors == null || ctors.length == 0) {
            throw new BeanCreationException(beanName,
                                            "Could not resolve matching constructor on bean class[" + mbd.getBeanClassName() + "]");

        } else if (ctors.length > 1) {
            throw new BeanCreationException(beanName,
                                            "Ambiguous constructor matches found on bean class [" + mbd.getBeanClassName() + "]");
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
            if (param.isPrimitive()) {
                argsWithDefaultValues[i] = BeanUtils.DEFAULT_TYPE_VALUES.get(param);
            } else {
                final Object bean = beanFactory.getBean(param);
                argsWithDefaultValues[i] = bean;
            }
        }

        try {
            final Object o = ctorToUse.newInstance(argsWithDefaultValues);
            return new BeanWrapper(o,
                                   o.getClass());
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

}
