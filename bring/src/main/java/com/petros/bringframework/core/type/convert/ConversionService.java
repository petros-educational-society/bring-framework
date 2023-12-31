package com.petros.bringframework.core.type.convert;

import javax.annotation.Nullable;

/**
 * A service interface for type conversion. This is the entry point into the convert system
 *
 * @author Viktor Basanets
 */
public interface ConversionService {

    /**
     * Return {@code true} if objects of {@code sourceType} can be converted to the {@code targetType}.
     * The TypeDescriptors provide additional context about the source and target locations
     * where conversion would occur, often object fields or property locations.
     * <p>If this method returns {@code true}, it means {@link #convert(Object, TypeDescriptor, TypeDescriptor)}
     * is capable of converting an instance of {@code sourceType} to {@code targetType}.
       * @param sourceType context about the source type to convert from
     * (maybe {@code null} if source is {@code null})
     * @param targetType context about the target type to convert to (required)
     * @return {@code true} if a conversion can be performed between the source and target types,
     * {@code false} if not
     * @throws IllegalArgumentException if {@code targetType} is {@code null}
     */
    boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

    /**
     * Convert the given {@code source} to the specified {@code targetType}.
     * The TypeDescriptors provide additional context about the source and target locations
     * where conversion will occur, often object fields or property locations.
     * @param source the source object to convert (may be {@code null})
     * @param sourceType context about the source type to convert from
     * (maybe {@code null} if source is {@code null})
     * @param targetType context about the target type to convert to (required)
     * @throws ConversionException if a conversion exception occurred
     * @throws IllegalArgumentException if targetType is {@code null},
     * or {@code sourceType} is {@code null} but source is not {@code null}
     */
    @Nullable
    Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

}
