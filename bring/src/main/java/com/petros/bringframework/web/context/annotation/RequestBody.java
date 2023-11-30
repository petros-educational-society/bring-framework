package com.petros.bringframework.web.context.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a method parameter as being bound to the body of the HTTP request.
 * This annotation indicates that the method parameter should be populated
 * with the contents of the HTTP request body.
 *
 * @see RestController
 * @author Serhii Dorodko
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {
}
