package com.petros.bringframework.context.annotation;

import java.lang.annotation.*;

/**
 * Marks a constructor, field, setter method, or config method to be injected.
 *
 * @author "Vasiuk Maryna"
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectPlease {

}
