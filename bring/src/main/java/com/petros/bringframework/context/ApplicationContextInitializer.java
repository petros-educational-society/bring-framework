package com.petros.bringframework.context;

/**
 * Callback interface for initializing a Spring {@link ConfigurableApplicationContext}
 * prior to being {@linkplain ConfigurableApplicationContext#refresh() refreshed}.
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@FunctionalInterface
public interface ApplicationContextInitializer<C extends ConfigurableApplicationContext> {
    /**
     * Initialize the given application context.
     * @param applicationContext the application to configure
     */
    void initialize(C applicationContext);
}
