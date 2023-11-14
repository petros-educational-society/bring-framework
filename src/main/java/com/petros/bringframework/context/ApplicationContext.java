package com.petros.bringframework.context;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.factory.config.BeanFactoryPostProcessor;

public interface ApplicationContext {

    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

    void initApplicationContext() throws BeansException, IllegalStateException;
}
