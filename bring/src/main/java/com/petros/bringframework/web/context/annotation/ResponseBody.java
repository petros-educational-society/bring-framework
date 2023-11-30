package com.petros.bringframework.web.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation that indicates a method return value should be bound to the web
 * response body. Supported for annotated handler methods.
 *
 * @see RestController
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBody {
}
