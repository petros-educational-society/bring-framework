package com.petros.bringframework.web.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to bind web request parameters to method parameters.
 * Indicates that a method parameter should be bound to a web request parameter.
 *
 * @see RestController
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    String name();
}
