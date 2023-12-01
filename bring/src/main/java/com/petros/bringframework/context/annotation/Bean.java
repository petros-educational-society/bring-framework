package com.petros.bringframework.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method produces a bean to be managed by the Bring container.
 *
* @author "Vadym Vovk"
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {

    String[] value() default {};

    String[] name() default {};

    boolean autowireCandidate() default true;

    String initMethod() default "";

//    String destroyMethod() default AbstractBeanDefinition.INFER_METHOD;
    String destroyMethod() default "(inferred)";

}
