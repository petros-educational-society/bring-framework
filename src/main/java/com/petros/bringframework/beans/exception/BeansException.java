package com.petros.bringframework.beans.exception;

import javax.annotation.Nullable;

/**
 * Abstract superclass for all exceptions thrown in the beans package and subpackages.
 * Note that this is a runtime (unchecked) exception. Beans exceptions are usually fatal;
 * there is no reason for them to be checked.
 */
public class BeansException extends RuntimeException {

    /**
     * Create a new BeansException with the specified message.
     *
     * @param msg the detail message
     */
    public BeansException(String msg) {
        super(msg);
    }

    /**
     * Create a new BeansException with the specified message
     * and root cause.
     *
     * @param msg   the detail message
     * @param cause the root cause
     */
    public BeansException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
