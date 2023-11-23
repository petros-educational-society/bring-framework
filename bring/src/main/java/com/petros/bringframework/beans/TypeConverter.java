package com.petros.bringframework.beans;

import com.petros.bringframework.beans.exception.TypeMismatchException;
import com.petros.bringframework.core.type.convert.TypeDescriptor;

import javax.annotation.Nullable;


/**
 * Interface that defines type conversion methods. Typically (but not necessarily)
 * implemented in conjunction with the {@link PropertyEditorRegistry} interface.
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */

public interface TypeConverter {
    /**
     * Convert the value to the required type
     * <p>Conversions from String to any type will typically use the {@code setAsText}
     * method of the PropertyEditor class in a ConversionService
     * @param value the value to convert
     * @param requiredType the type we must convert to
     * @return the new value, possibly the result of type conversion
     * @throws TypeMismatchException if type conversion failed
     */
    @Nullable
    <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType) throws TypeMismatchException;


    /**
     * Convert the value to the required type.
     * <p>Conversions from String to any type will typically use the {@code setAsText}
     * method of the PropertyEditor class in a ConversionService
     * @param value the value to convert
     * @param requiredType the type we must convert to
     * @param typeDescriptor the type descriptor to use (may be {@code null}))
     * @return the new value, possibly the result of type conversion
     * @throws TypeMismatchException if type conversion failed
     */
    @Nullable
    default <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType,
                                     @Nullable TypeDescriptor typeDescriptor) throws TypeMismatchException {

        throw new UnsupportedOperationException("TypeDescriptor resolution not supported");
    }
}
