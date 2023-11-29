package com.petros.bringframework.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated class is a "component". Such classes are considered as candidates for auto-detection
 * when using annotation-based configuration and classpath scanning.
 *
 * @author "Maksym Oliinyk"
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a  bean in case of an autodetected component.
     *
     * @return the suggested component name, if any (or empty String otherwise)
     */
    String value() default "";

}
