package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.factory.BeanFactory;

@FunctionalInterface
public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(BeanFactory beanFactory);
}
