package com.petros.bringframework.beans.exception;

import javax.annotation.Nullable;

import static com.petros.bringframework.util.ClassUtils.getDescriptiveType;
import static com.petros.bringframework.util.ClassUtils.getQualifiedName;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

/**
 * Exception thrown on a type mismatch when trying to set a bean property.
 * @author Viktor Basanets
 * @Project: bring-framework
 */

public class TypeMismatchException extends PropertyAccessException {

    public static final String ERROR_CODE = "typeMismatch";

    @Nullable
    private final transient Object value;

    @Nullable
    private final Class<?> requiredType;

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
