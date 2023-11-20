package com.petros.bringframework.beans;

import com.petros.bringframework.beans.exception.TypeMismatchException;
import com.petros.bringframework.core.type.convert.TypeDescriptor;

import javax.annotation.Nullable;

public interface TypeConverter {
    /**
     * Convert the value to the required type
     * <p>Conversions from String to any type will typically use the {@code setAsText}
     * method of the PropertyEditor class, or a Spring Converter in a ConversionService.
     * @param value the value to convert
     * @param requiredType the type we must convert to
     * (or {@code null} if not known, for example in case of a collection element)
     * @return the new value, possibly the result of type conversion
     * @throws TypeMismatchException if type conversion failed
     */
    @Nullable
    <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType) throws TypeMismatchException;


    /**
     * Convert the value to the required type.
     * <p>Conversions from String to any type will typically use the {@code setAsText}
     * method of the PropertyEditor class, or a Spring Converter in a ConversionService.
     * @param value the value to convert
     * @param requiredType the type we must convert to
     * (or {@code null} if not known, for example in case of a collection element)
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
