package com.petros.bringframework.core.type.convert.support;

import com.petros.bringframework.core.type.convert.ConversionService;

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
}
