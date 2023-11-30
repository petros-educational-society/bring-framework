package com.petros.bringframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a class may be processed by the Bring container to generate bean definitions
 * and service requests for those beans at runtime.
 *
 * <p>This annotation serves as a specialization of {@link Component @Component},
 * allowing for implementation classes to be autodetected through classpath scanning.
 * Configuration classes may not only be bootstrapped using component scanning,
 * but may also themselves configure component scanning using the @ComponentScan annotation.
 *
 * @see ComponentScan
 * @see Lazy
 * @see AnnotationConfigApplicationContext
 * @author "Vasiuk Maryna"
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {

    /**
     * Explicitly specify the name of the Spring bean definition associated with the
     * {@code @Configuration} class.
     */
    String value() default "";
}
