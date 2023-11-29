package com.petros.bringframework.web.context.annotation;

import com.petros.bringframework.context.annotation.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A convenience annotation that is itself annotated with
 * Types that carry this annotation are treated as controllers where
 * {@link ResponseBody @ResponseBody} semantics by default.
 * @author Viktor Basanets
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@ResponseBody
public @interface RestController {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a  bean in case of an autodetected component.
     *
     * @return the suggested component name, if any (or empty String otherwise)
     */
    String value() default "";

}
