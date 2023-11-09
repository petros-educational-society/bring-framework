package com.petros.bringframework.context.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author "Maksym Oliinyk"
 */
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Component {
}
