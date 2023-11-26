package com.petros.bringframework.beans.factory.annotation;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * The <code>InitPlease</code> annotation is used on a method that
 * needs to be executed after dependency injection is done to perform
 * any initialization. This  method must be invoked before the class
 * is put into service. This annotation must be supported on all classes
 * that support dependency injection. The method annotated with
 * <code>InitPlease</code> must be invoked even if the class does
 * not request any resources to be injected. Only one
 * method in a given class can be annotated with this annotation.
 *
 * @author "Vasiuk Maryna"
 */
@Documented
@Retention (RUNTIME)
@Target(METHOD)
public @interface InitPlease {
}
