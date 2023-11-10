package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.factory.BeanDefinitionStoreException;
import com.petros.bringframework.beans.factory.config.BeanDefinition;

import static java.lang.String.format;

public class BeanDefinitionOverrideException extends BeanDefinitionStoreException {

    private final BeanDefinition beanDefinition;

    private final BeanDefinition existingDefinition;

    public BeanDefinitionOverrideException(String beanName, BeanDefinition def, BeanDefinition existDef) {
        super(def.getResourceDescription(), beanName,
                format("Cannot register bean definition [%s] for bean '%s' since there is already [%s] bound.", def, beanName, existDef)
        );
        this.beanDefinition = def;
        this.existingDefinition = existDef;
    }
}
