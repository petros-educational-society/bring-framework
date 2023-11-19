package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.factory.support.AnnotationBeanNameGenerator;

import static java.util.Objects.requireNonNull;

public class FullyQualifiedAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {

    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        requireNonNull(beanClassName, "No bean class name set");
        return beanClassName;
    }

}
