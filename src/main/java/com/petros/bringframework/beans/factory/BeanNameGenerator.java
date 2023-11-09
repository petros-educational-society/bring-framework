package com.petros.bringframework.beans.factory;

import com.petros.bringframework.beans.factory.config.BeanDefinition;

public interface BeanNameGenerator {

    String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry);

}
