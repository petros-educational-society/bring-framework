package com.petros.bringframework.beans.exception;


import com.petros.bringframework.beans.FatalBeanException;

/**
 * Exception thrown when a BeanFactory encounters an error when attempting
 * to create a bean from a bean definition.
 */
public class BeanCreationException extends FatalBeanException {
    public BeanCreationException(Class<?> clazz, String message, Throwable e) {
        super("Class " + clazz.getName() + " " + message, e);
    }

    public BeanCreationException(Class<?> clazz, String message) {
        super("Class " + clazz.getName() + " " + message);
    }

    public BeanCreationException(String beanName, String msg) {
        super("Error creating bean with name '" + beanName + "': " + msg);
    }

    public BeanCreationException(String objectCreatedException, String message, Throwable e) {
        super(objectCreatedException + message, e);
    }
}
