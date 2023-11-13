package com.petros.bringframework.context.annotation;

import java.lang.annotation.*;

/**
 * Annotation used at the field or method/constructor parameter level that indicates a default value expression for the annotated element.
 * Typically used for expression-driven or property-driven dependency injection.
 *
 * @author "Vasiuk Maryna"
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    /**
     * The actual value expression such as <code>#{systemProperties.myProp}</code>
     * or property placeholder such as <code>${my.app.myProp}</code>.
     */
    String value();

}
