package com.petros.bringframework.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * When used as a type-level annotation in conjunction with @Component, @Scope indicates the name of a scope to use for instances of the annotated type.
 * When used as a method-level annotation in conjunction with @Bean, @Scope indicates the name of a scope to use for the instance returned from the method.
 *
 * @author "Maksym Oliinyk"
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Scope {
    String value() default "";

    ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT;
}
