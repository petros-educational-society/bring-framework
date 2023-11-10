package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.factory.BeanDefinitionStoreException;
import com.petros.bringframework.beans.factory.config.BeanDefinition;

public class BeanDefinitionOverrideException extends BeanDefinitionStoreException {

    private final BeanDefinition beanDefinition;

    private final BeanDefinition existingDefinition;

    public BeanDefinitionOverrideException(
            String beanName, BeanDefinition beanDefinition, BeanDefinition existingDefinition) {

        super(/*beanDefinition.getResourceDescription()*/null, beanName,
                "Cannot register bean definition [" + beanDefinition + "] for bean '" + beanName +
                        "' since there is already [" + existingDefinition + "] bound.");
        this.beanDefinition = beanDefinition;
        this.existingDefinition = existingDefinition;
    }
}
