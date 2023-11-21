package com.petros.bringframework.context.annotation;

import java.lang.annotation.*;

/**
 * Indicates whether a bean is to be lazily initialized.
 * May be used on any class directly or indirectly annotated with @Component or on methods annotated with @Bean.
 *
 * @author "Maksym Oliinyk"
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lazy {

	/**
	 * Whether lazy initialization should occur.
	 */
	boolean value() default true;

}