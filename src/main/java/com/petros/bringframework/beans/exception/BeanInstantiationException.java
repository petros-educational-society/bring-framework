package com.petros.bringframework.beans.exception;

import com.petros.bringframework.beans.FatalBeanException;

import javax.annotation.Nullable;

/**
 * @author "Maksym Oliinyk"
 */
public class BeanInstantiationException extends FatalBeanException {
    public BeanInstantiationException(String msg) {
        super(msg);
    }

    public BeanInstantiationException(String beanClass, String msg) {
        super("Failed to instantiate [" + beanClass + "]: " + msg);
    }

    public BeanInstantiationException(String beanClass, String msg, @Nullable Throwable cause) {
        super("Failed to instantiate [" + beanClass + "]: " + msg, cause);
    }
}
