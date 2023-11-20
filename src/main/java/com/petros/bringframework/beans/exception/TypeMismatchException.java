package com.petros.bringframework.beans.exception;

import javax.annotation.Nullable;

import static com.petros.bringframework.util.ClassUtils.getDescriptiveType;
import static com.petros.bringframework.util.ClassUtils.getQualifiedName;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class TypeMismatchException extends PropertyAccessException {

    public static final String ERROR_CODE = "typeMismatch";


//    @Nullable
//    private String propertyName;

    @Nullable
    private final transient Object value;

    @Nullable
    private final Class<?> requiredType;

//    public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, @Nullable Class<?> requiredType,
//                                 @Nullable Throwable cause) {
//
//        super(propertyChangeEvent,
//                format("Failed to convert property value of type '%s' '%s'%s", getDescriptiveType(propertyChangeEvent.getNewValue()),
//                        nonNull(requiredType) ? format("to required type '%s'", getQualifiedName(requiredType))
//                                : format("for property '%s'", propertyChangeEvent.getPropertyName()),
//                        nonNull(cause) ? format(";%s", cause.getMessage()) : ""),
//                cause);
//        this.propertyName = propertyChangeEvent.getPropertyName();
//        this.value = propertyChangeEvent.getNewValue();
//        this.requiredType = requiredType;
//    }

    public TypeMismatchException(@Nullable Object value, @Nullable Class<?> requiredType, @Nullable Throwable cause) {
        super(format("Failed to convert value of type '%s' '%s'%s", getDescriptiveType(value),
                        nonNull(requiredType) ? format("to required type '%s'", getQualifiedName(requiredType)) : "",
                        nonNull(cause) ? format("; %s", cause.getMessage()) : ""),
                cause);
        this.value = value;
        this.requiredType = requiredType;
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
