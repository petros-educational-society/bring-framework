package com.petros.bringframework.core.type.convert.support;

import com.petros.bringframework.core.type.convert.ConversionService;
import com.petros.bringframework.core.type.convert.TypeDescriptor;

import javax.annotation.Nullable;

import static java.util.Objects.isNull;

public class DefaultConversionService implements ConversionService {

    @Nullable
    private static volatile DefaultConversionService sharedInstance;

    public static ConversionService getSharedInstance() {
        if (isNull(sharedInstance)) {
            synchronized (DefaultConversionService.class) {
                if (isNull(sharedInstance)) {
                    sharedInstance = new DefaultConversionService();
                }
            }
        }
        return sharedInstance;
    }

    @Override
    public boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType) {
        return false;
    }

    @Override
    public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        return false;
    }

    @Nullable
    @Override
    public <T> T convert(@Nullable Object source, Class<T> targetType) {
        return null;
    }

    @Nullable
    @Override
    public Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        return null;
    }
}
