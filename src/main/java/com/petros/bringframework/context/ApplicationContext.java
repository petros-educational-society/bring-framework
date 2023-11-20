package com.petros.bringframework.context;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.factory.config.BeanFactoryPostProcessor;

public interface ApplicationContext {

    void init() throws BeansException, IllegalStateException;
}
