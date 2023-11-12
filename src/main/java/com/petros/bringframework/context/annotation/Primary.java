package com.petros.bringframework.context.annotation;

import java.lang.annotation.*;

/**
 * @author "Maksym Oliinyk"
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Primary {

}