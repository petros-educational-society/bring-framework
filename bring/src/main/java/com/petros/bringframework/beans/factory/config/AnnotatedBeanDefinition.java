package com.petros.bringframework.beans.factory.config;

/**
 * @author "Maksym Oliinyk"
 */

import javax.annotation.Nullable;

/**
 * Extended {@link BeanDefinition}
 * interface that exposes {@link AnnotationMetadata}
 * about its bean class - without requiring the class to be loaded yet.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see AnnotationMetadata
 */
public interface AnnotatedBeanDefinition extends BeanDefinition {

    /**
     * Obtain the annotation metadata (as well as basic class metadata)
     * for this bean definition's bean class.
     * @return the annotation metadata object (never {@code null})
     */
    AnnotationMetadata getMetadata();

    /**
     * Obtain metadata for this bean definition's factory method, if any.
     * @return the factory method metadata, or {@code null} if none
     */
    @Nullable
    MethodMetadata getFactoryMethodMetadata();

}