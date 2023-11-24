package com.petros.bringframework.core.type.convert.support;

import com.petros.bringframework.core.type.convert.ConversionService;
import com.petros.bringframework.core.type.convert.TypeDescriptor;

import javax.annotation.Nullable;

import static java.util.Objects.isNull;

/**
 * A specialization of {@link ConversionService} configured by default
 * with converters appropriate for most environments.
 *
 * @author Viktor Basanets
 */

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
    public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        //todo: implement method
        return false;
    }

    @Nullable
    @Override
    public Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        //todo: implement method
        return source;
    }
}
