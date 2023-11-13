package com.petros.bringframework.context.annotation;

import java.lang.annotation.*;

/**
 * Adds a textual description to bean definitions derived from
 * {@link Component} or {@link Bean}.
 *
 * @author "Maksym Oliinyk"
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Description {

    /**
     * The textual description to associate with the bean definition.
     */
    String value();

}