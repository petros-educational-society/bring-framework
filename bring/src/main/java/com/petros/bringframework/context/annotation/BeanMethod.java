package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.ConfigurationClass;
import com.petros.bringframework.beans.factory.config.MethodMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BeanMethod {

    private final MethodMetadata metadata;

    private final ConfigurationClass configurationClass;

}
