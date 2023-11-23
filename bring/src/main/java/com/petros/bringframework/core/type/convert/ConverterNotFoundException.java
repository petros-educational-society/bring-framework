package com.petros.bringframework.core.type.convert;

import lombok.Getter;

import javax.annotation.Nullable;

import static java.lang.String.format;

/**
 * Exception to be thrown when a suitable converter could not be found
 * in a given conversion service.
 * @author Viktor Basanets
 */

@Getter
public class ConverterNotFoundException extends ConversionException {

    @Nullable
    private final TypeDescriptor sourceType;

    private final TypeDescriptor targetType;

    public ConverterNotFoundException(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        super(format("No converter found capable of converting from type [%s] to type [%s]", sourceType, targetType));
        this.sourceType = sourceType;
        this.targetType = targetType;
    }
}
