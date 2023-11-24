package com.petros.bringframework.core.type.convert;

import com.petros.bringframework.util.ObjectUtils;
import lombok.Getter;

import javax.annotation.Nullable;

import static java.lang.String.format;

/**
 * Exception to be thrown when an actual type conversion attempt fails.
 *
 * @author Viktor Basanets
 */

@Getter
public class ConversionFailedException extends ConversionException {
    @Nullable
    private final TypeDescriptor sourceType;

    private final TypeDescriptor targetType;

    @Nullable
    private final Object value;

    public ConversionFailedException(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType,
                                     @Nullable Object value, Throwable cause) {

        super(format("Failed to convert from type [%s] to type [%s] for value [%s]",
                sourceType, targetType, ObjectUtils.nullSafeConciseToString(value)), cause);
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.value = value;
    }
}
