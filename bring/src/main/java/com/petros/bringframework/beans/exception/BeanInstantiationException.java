package com.petros.bringframework.beans.exception;

import com.petros.bringframework.beans.FatalBeanException;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author "Maksym Oliinyk"
 * @author "Viktor Basanets"
 */
public class BeanInstantiationException extends FatalBeanException {
    private final Class<?> beanClass;

    @Nullable
    private final Constructor<?> constructor;

    @Nullable
    private final Method constructingMethod;

    public BeanInstantiationException(String msg) {
        super(msg);
        this.beanClass = null;
        this.constructor = null;
        this.constructingMethod = null;
    }

    public BeanInstantiationException(Class<?> beanClass, String msg) {
        this(beanClass, msg, null);
    }

    public BeanInstantiationException(String beanClass, String msg) {
        super("Failed to instantiate [" + beanClass + "]: " + msg);
        this.beanClass = null;
        this.constructor = null;
        this.constructingMethod = null;
    }

    public BeanInstantiationException(String beanClass, String msg, @Nullable Throwable cause) {
        super("Failed to instantiate [" + beanClass + "]: " + msg, cause);
        this.beanClass = null;
        this.constructor = null;
        this.constructingMethod = null;
    }

    public BeanInstantiationException(Class<?> beanClass, String msg, @Nullable Throwable cause) {
        super("Failed to instantiate [" + beanClass.getName() + "]: " + msg, cause);
        this.beanClass = beanClass;
        this.constructor = null;
        this.constructingMethod = null;
    }

    public BeanInstantiationException(Constructor<?> constructor, String msg, @Nullable Throwable cause) {
        super("Failed to instantiate [" + constructor.getDeclaringClass().getName() + "]: " + msg, cause);
        this.beanClass = constructor.getDeclaringClass();
        this.constructor = constructor;
        this.constructingMethod = null;
    }

    public BeanInstantiationException(Method constructingMethod, String msg, @Nullable Throwable cause) {
        super("Failed to instantiate [" + constructingMethod.getReturnType().getName() + "]: " + msg, cause);
        this.beanClass = constructingMethod.getReturnType();
        this.constructor = null;
        this.constructingMethod = constructingMethod;
    }
}
