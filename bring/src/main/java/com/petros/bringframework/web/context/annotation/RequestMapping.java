package com.petros.bringframework.web.context.annotation;

import com.petros.bringframework.web.servlet.support.common.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for mapping web requests onto methods in request-handling classes.
 * Used to specify the path and HTTP request method for handling a particular request.
 *
 * @see RestController
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestMapping {

    /**
     * The path mapping for the annotated method, indicating the URL path.
     *
     * @return the URL path for the mapping
     */
    String path();

    /**
     * The HTTP request method for the annotated method.
     *
     * @return the HTTP request method
     */
    RequestMethod method();
}
