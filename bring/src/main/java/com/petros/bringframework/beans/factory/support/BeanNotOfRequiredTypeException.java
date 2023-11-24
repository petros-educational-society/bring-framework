package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.util.ClassUtils;
import lombok.Getter;

import static java.lang.String.format;

@Getter
public class BeanNotOfRequiredTypeException extends BeansException {

    /** The name of the instance that was of the wrong type. */
    private final String beanName;

    /** The required type. */
    private final Class<?> requiredType;

    /** The offending type. */
    private final Class<?> actualType;



    public BeanNotOfRequiredTypeException(String beanName, Class<?> requiredType, Class<?> actualType) {
        super(format("Bean named '%s' is expected to be of type '%s' but was actually of type '%s'",
                beanName, ClassUtils.getQualifiedName(requiredType), ClassUtils.getQualifiedName(actualType)));
        this.beanName = beanName;
        this.requiredType = requiredType;
        this.actualType = actualType;
    }
}
