package com.petros.bringframework.beans.exception;

import com.petros.bringframework.beans.FatalBeanException;

import javax.annotation.Nullable;

/**
 * @author "Maksym Oliinyk"
 */
public class BeanCurrentlyInCreationException
        extends FatalBeanException {

    public BeanCurrentlyInCreationException(String beanName) {
        super(getMsg(beanName));
    }

    public BeanCurrentlyInCreationException(String beanName, @Nullable Throwable cause) {
        super(getMsg(beanName), cause);
    }

    private static String getMsg(String beanName) {
        return String.format("Requested bean '%s' is currently in creation: Is there an unresolvable circular reference?",
                             beanName);
    }

}
