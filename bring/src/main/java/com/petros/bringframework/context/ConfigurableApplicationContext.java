package com.petros.bringframework.context;

import com.petros.bringframework.beans.BeansException;

/**
 * @author "Maksym Oliinyk"
 */
public interface ConfigurableApplicationContext extends ApplicationContext {
    /**
     * Load or refresh the persistent representation of the configuration, which
     * might be from Java-based configuration, an XML file, a properties file, a
     * relational database schema, or some other format.
     * <p>As this is a startup method, it should destroy already created singletons
     * if it fails, to avoid dangling resources. In other words, after invocation
     * of this method, either all or no singletons at all should be instantiated.
     *
     * @throws BeansException        if the bean factory could not be initialized
     * @throws IllegalStateException if already initialized and multiple refresh
     *                               attempts are not supported
     */
    void refresh() throws BeansException, IllegalStateException;

    /**
     * Register a shutdown hook with the JVM runtime, closing this context
     * on JVM shutdown unless it has already been closed at that time.
     */
    void registerShutdownHook();
}
