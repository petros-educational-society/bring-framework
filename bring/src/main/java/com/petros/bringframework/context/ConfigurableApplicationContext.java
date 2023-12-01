package com.petros.bringframework.context;

import com.petros.bringframework.beans.BeansException;

/**
 * Provides facilities to configure an application context in addition to the
 * application context client methods in the ApplicationContext interface.
 * This interface defines methods to manage the lifecycle of an application context,
 * specifically focusing on initialization and shutdown procedures.
 * It provides capabilities to load or refresh the configuration and to register a
 * shutdown hook for context closure on JVM shutdown.
 *
 * @see #refresh
 * @see #registerShutdownHook
 * @see ApplicationContext
 * @author "Maksym Oliinyk"
 */
public interface ConfigurableApplicationContext extends ApplicationContext {
    /**
     * Load or refresh the persistent representation of the configuration, which
     * might be from Java-based configuration.
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
