package com.petros.bringframework.beans.exception;


/**
 * Exception thrown when a BeanFactory encounters an error when attempting
 * to create a bean from a bean definition.
 */
public class BeanCreationException extends RuntimeException {
    public BeanCreationException(Class<?> clazz, String message, Throwable e) {
        super("Class " + clazz.getName() + " " + message, e);
    }

    public BeanCreationException(Class<?> clazz, String message) {
        super("Class " + clazz.getName() + " " + message);
    }

    public BeanCreationException(String objectCreatedException, String message, Throwable e) {
        super(objectCreatedException + message, e);
    }
}
