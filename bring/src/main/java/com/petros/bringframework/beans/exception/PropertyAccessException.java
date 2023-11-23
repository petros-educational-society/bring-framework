package com.petros.bringframework.beans.exception;

import com.petros.bringframework.beans.BeansException;

import javax.annotation.Nullable;
import java.beans.PropertyChangeEvent;

import static java.util.Objects.nonNull;

/**
 * Superclass for exceptions related to a property access such as type mismatch or invocation target exception.
 * @author Viktor Basanets
 * @Project: bring-framework
 */

@SuppressWarnings("serial")
public abstract class PropertyAccessException extends BeansException {

    @Nullable
    private final PropertyChangeEvent propertyChangeEvent;


    /**
     * Create a new PropertyAccessException.
     * @param propertyChangeEvent the PropertyChangeEvent that resulted in the problem
     * @param msg the detail message
     * @param cause the root cause
     */
    public PropertyAccessException(PropertyChangeEvent propertyChangeEvent, String msg, @Nullable Throwable cause) {
        super(msg, cause);
        this.propertyChangeEvent = propertyChangeEvent;
    }

    /**
     * Create a new PropertyAccessException without PropertyChangeEvent.
     * @param msg the detail message
     * @param cause the root cause
     */
    public PropertyAccessException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
        propertyChangeEvent = null;
    }


    /**
     * Return the PropertyChangeEvent that resulted in the problem.
     * <p>May be {@code null}; only available if an actual bean property
     * was affected.
     */
    @Nullable
    public PropertyChangeEvent getPropertyChangeEvent() {
        return propertyChangeEvent;
    }

    /**
     * Return the name of the affected property, if available.
     */
    @Nullable
    public String getPropertyName() {
        String propertyName = null;
        if (nonNull(propertyChangeEvent)) {
            propertyName = propertyChangeEvent.getPropertyName();
        }
        return propertyName;
    }

    /**
     * Return the affected value that was about to be set, if any.
     */
    @Nullable
    public Object getValue() {
        Object value = null;
        if (nonNull(propertyChangeEvent)) {
            value = propertyChangeEvent.getNewValue();
        }
        return value;
    }

    /**
     * Return a corresponding error code for this type of exception.
     */
    public abstract String getErrorCode();

}
