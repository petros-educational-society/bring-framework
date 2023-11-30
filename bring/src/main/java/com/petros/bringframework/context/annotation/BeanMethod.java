package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.ConfigurationClass;
import com.petros.bringframework.beans.factory.config.MethodMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a method in a configuration class annotated with the {@link com.petros.bringframework.context.annotation.Bean} annotation.
 * This class encapsulates metadata and configuration information related to a specific bean method.
 *
 * @author "Vadym Vovk"
 */
@Getter
@RequiredArgsConstructor
public class BeanMethod {

    /**
     * Metadata related to the method.
     */
    private final MethodMetadata metadata;

    /**
     * Configuration class to which this bean method belongs.
     */
    private final ConfigurationClass configurationClass;

}
