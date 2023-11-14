package com.petros.bringframework.context.annotation;

public interface AnnotationConfigRegistry {

    /**
     * Register one or more component classes to be processed.
     * adding the same component class more than once has no additional effect.
     * @param componentClasses one or more component classes,
     */
    void register(Class<?>... componentClasses);

    /**
     * Perform a scan within the specified base packages.
     * @param basePackages the packages to scan for component classes
     */
    void scan(String... basePackages);

}
