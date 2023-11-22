package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.BeanDefinitionRole;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the 'role' hint for a given bean.
 *
 * <p>May be used on any class directly or indirectly annotated with
 * {@link Component}
 *
 * <p>If Role is present on a {@link Configuration @Configuration} class,
 * this indicates the role of the configuration class bean definition and
 * does not cascade to all @{@code Bean} methods defined within. This behavior
 * is different from the behavior of the @{@link Lazy} annotation, for example.
 *
 * @author Maksym Oliinyk
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Role {

    /**
     * Set the role hint for the associated bean.
     *
     * @see BeanDefinitionRole#ROLE_APPLICATION
     * @see BeanDefinitionRole#ROLE_INFRASTRUCTURE
     * @see BeanDefinitionRole#ROLE_SUPPORT
     */
    BeanDefinitionRole value();

}