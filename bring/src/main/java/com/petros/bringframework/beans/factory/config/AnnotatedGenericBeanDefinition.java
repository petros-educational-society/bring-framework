package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.support.GenericBeanDefinition;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public class AnnotatedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

    private final AnnotationMetadata metadata;

    @Nullable
    private MethodMetadata factoryMethodMetadata;

    /**
     * Create a new AnnotatedGenericBeanDefinition for the given annotation metadata,
     * allowing for ASM-based processing and avoidance of early loading of the bean class.
     * Note that this constructor is functionally equivalent to
     * {@link org.springframework.context.annotation.ScannedGenericBeanDefinition
     * ScannedGenericBeanDefinition}, however the semantics of the latter indicate that a
     * bean was discovered specifically via component-scanning as opposed to other means.
     * @param metadata the annotation metadata for the bean class in question
     * @since 3.1.1
     */
    public AnnotatedGenericBeanDefinition(AnnotationMetadata metadata) {
        requireNonNull(metadata, "AnnotationMetadata must not be null");
        setBeanClassName(metadata.getClassName());
        this.metadata = metadata;
    }

    @Override
    public final AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    @Nullable
    public final MethodMetadata getFactoryMethodMetadata() {
        return this.factoryMethodMetadata;
    }
}
