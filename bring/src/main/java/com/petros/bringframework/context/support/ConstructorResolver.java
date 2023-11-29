package com.petros.bringframework.context.support;

import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.exception.BeanInstantiationException;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import com.petros.bringframework.beans.factory.support.BeanWrapper;
import com.petros.bringframework.beans.support.AbstractBeanDefinition;
import com.petros.bringframework.beans.support.GenericBeanDefinition;
import com.petros.bringframework.util.BeanUtils;
import com.petros.bringframework.util.ClassUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public BeanWrapper instantiateUsingFactoryMethod(String beanName, GenericBeanDefinition mbd, Object[] explicitArgs) {
        Object factoryBean;
        Class<?> factoryClass;
        boolean isStatic;

        String factoryBeanName = mbd.getFactoryBeanName();
        factoryBean = this.beanFactory.getBean(factoryBeanName);
        if (factoryBeanName != null) {
            //factoryBean = this.beanFactory.getBean(factoryBeanName);
            //factoryBean = this.beanFactory.getBean(factoryBeanName);
            if (mbd.isSingleton() && this.beanFactory.containsSingleton(beanName)) {
                // TODO :: add exception
                throw new RuntimeException();
            }
            this.beanFactory.registerDependentBean(factoryBeanName, beanName);
            factoryClass = factoryBean.getClass();
            isStatic = false;
        } else {
            factoryClass = mbd.getBeanClass();
        }

        Method factoryMethodToUse = null;
        ArgumentsHolder argsHolderToUse = null;
        Object[] argsToUse = null;

        if (explicitArgs != null) {
            argsToUse = explicitArgs;
        }

        factoryClass = ClassUtils.getUserClass(factoryClass);
        List<Method> candidates = null;

        factoryMethodToUse = mbd.getResolvedFactoryMethod();

        beanFactory.instantiateBean(mbd, beanName, this.beanFactory, factoryBean, factoryMethodToUse, explicitArgs);



        return null;
    }

    private static class ArgumentsHolder {

        public final Object[] rawArguments;

        public final Object[] arguments;

        public final Object[] preparedArguments;

        public boolean resolveNecessary = false;

        public ArgumentsHolder(int size) {
            this.rawArguments = new Object[size];
            this.arguments = new Object[size];
            this.preparedArguments = new Object[size];
        }

        public ArgumentsHolder(Object[] args) {
            this.rawArguments = args;
            this.arguments = args;
            this.preparedArguments = args;
        }

        /*public int getTypeDifferenceWeight(Class<?>[] paramTypes) {
            // If valid arguments found, determine type difference weight.
            // Try type difference weight on both the converted arguments and
            // the raw arguments. If the raw weight is better, use it.
            // Decrease raw weight by 1024 to prefer it over equal converted weight.
            int typeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.arguments);
            int rawTypeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.rawArguments) - 1024;
            return Math.min(rawTypeDiffWeight, typeDiffWeight);
        }*/

        public int getAssignabilityWeight(Class<?>[] paramTypes) {
            for (int i = 0; i < paramTypes.length; i++) {
                if (!ClassUtils.isAssignableValue(paramTypes[i], this.arguments[i])) {
                    return Integer.MAX_VALUE;
                }
            }
            for (int i = 0; i < paramTypes.length; i++) {
                if (!ClassUtils.isAssignableValue(paramTypes[i], this.rawArguments[i])) {
                    return Integer.MAX_VALUE - 512;
                }
            }
            return Integer.MAX_VALUE - 1024;
        }

        /*public void storeCache(RootBeanDefinition mbd, Executable constructorOrFactoryMethod) {
            synchronized (mbd.constructorArgumentLock) {
                mbd.resolvedConstructorOrFactoryMethod = constructorOrFactoryMethod;
                mbd.constructorArgumentsResolved = true;
                if (this.resolveNecessary) {
                    mbd.preparedConstructorArguments = this.preparedArguments;
                }
                else {
                    mbd.resolvedConstructorArguments = this.arguments;
                }
            }
        }*/
    }
}
