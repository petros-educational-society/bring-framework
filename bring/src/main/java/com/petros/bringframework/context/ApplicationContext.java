package com.petros.bringframework.context;

import com.petros.bringframework.beans.BeansException;

/**
 * Central interface to provide configuration for an application.
 *
 * @author "Viktor Basanets"
 * @see ConfigurableApplicationContext
 */
public interface ApplicationContext {

    void init() throws BeansException, IllegalStateException;
}
