package com.petros.bringframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds a textual description to bean definitions derived from
 * {@link Component} or Bean
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