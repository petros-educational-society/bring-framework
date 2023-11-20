package com.petros.bringframework.beans.exception;

import com.petros.bringframework.beans.exception.PropertyAccessException;
import com.petros.bringframework.util.ClassUtils;

import javax.annotation.Nullable;
import java.beans.PropertyChangeEvent;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class TypeMismatchException extends PropertyAccessException {

    public static final String ERROR_CODE = "typeMismatch";


    @Nullable
    private String propertyName;

    @Nullable
    private final transient Object value;

    @Nullable
    private final Class<?> requiredType;

    public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, Class<?> requiredType) {
        this(propertyChangeEvent, requiredType, null);
    }

    public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, @Nullable Class<?> requiredType,
                                 @Nullable Throwable cause) {

        super(propertyChangeEvent,
                format("Failed to convert property value of type '%s' '%s'%s",
                        ClassUtils.getDescriptiveType(propertyChangeEvent.getNewValue()),
                        nonNull(requiredType) ? format("to required type '%s'", ClassUtils.getQualifiedName(requiredType))
                                : format("for property '%s'", propertyChangeEvent.getPropertyName()),
                        nonNull(cause) ? format(";%s", cause.getMessage()) : ""),
                cause);
        this.propertyName = propertyChangeEvent.getPropertyName();
        this.value = propertyChangeEvent.getNewValue();
        this.requiredType = requiredType;
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
