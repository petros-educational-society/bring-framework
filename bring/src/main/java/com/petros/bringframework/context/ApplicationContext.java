package com.petros.bringframework.context;

import com.petros.bringframework.beans.BeansException;

public interface ApplicationContext {

    void init() throws BeansException, IllegalStateException;
}
