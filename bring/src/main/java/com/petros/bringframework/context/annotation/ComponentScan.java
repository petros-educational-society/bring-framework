package com.petros.bringframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configures component scanning directives for use with @Configuration classes. Provides support parallel with Spring XML's  element.
 * Either basePackageClasses or basePackages (or its alias value) may be specified to define specific packages to scan.
 * If specific packages are not defined, scanning will occur from the package of the class that declares this annotation.
 * Note that the element has an annotation-config attribute; however, this annotation does not. This is because in almost
 * all cases when using @ComponentScan, default annotation config processing (e.g. processing @Autowired and friends) is assumed.
 * Furthermore, when using AnnotationConfigApplicationContext, annotation config processors are always registered, meaning that
 * any attempt to disable them at the @ComponentScan level would be ignored.
 *
 * @author "Vasiuk Maryna"
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ComponentScan {

    /**
     * Base packages to scan for annotated components.
     */
    String[] basePackages() default {};

}
