package com.petros.bringframework.web.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method parameter as being bound to a URI template variable.
 * The name attribute specifies the name of the URI template variable to bind to.
 *
 * @see RestController
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PathVariable {

    /**
     * Specifies the name of the URI template variable to bind to the annotated method parameter.
     *
     * @return The name of the URI template variable to bind the parameter to.
     */
    String name();
}
