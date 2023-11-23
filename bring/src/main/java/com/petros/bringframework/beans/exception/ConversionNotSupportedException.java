package com.petros.bringframework.beans.exception;

import javax.annotation.Nullable;
import java.beans.PropertyChangeEvent;


/**
 * Exception thrown when no suitable editor or converter can be found for a bean property.
 * @author Viktor Basanets
 * @Project: bring-framework
 */

public class ConversionNotSupportedException extends TypeMismatchException {
    public ConversionNotSupportedException(PropertyChangeEvent propertyChangeEvent,
                                           @Nullable Class<?> requiredType, @Nullable Throwable cause) {
        super(propertyChangeEvent, requiredType, cause);
    }

    public ConversionNotSupportedException(@Nullable Object value, @Nullable Class<?> requiredType, @Nullable Throwable cause) {
        super(value, requiredType, cause);
    }
}
