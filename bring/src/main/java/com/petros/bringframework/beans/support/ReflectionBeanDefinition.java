package com.petros.bringframework.beans.support;

import com.petros.bringframework.beans.factory.config.AnnotatedBeanDefinition;
import com.petros.bringframework.beans.factory.config.AnnotationMetadata;
import com.petros.bringframework.beans.factory.config.MethodMetadata;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.type.reading.ReflectionMetadataReader;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public class ReflectionBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

    private final AnnotationMetadata metadata;

    @Nullable
    private MethodMetadata factoryMethodMetadata;

    /**
     * Create a new ScannedGenericBeanDefinition for the class that the
     * given MetadataReader describes.
     *
     * @param metadataReader the MetadataReader for the scanned target class
     */
    public ReflectionBeanDefinition(ReflectionMetadataReader metadataReader) {
        AssertUtils.notNull(metadataReader, "MetadataReader must not be null");
        this.metadata = metadataReader.getAnnotationMetadata();
        setBeanClassName(this.metadata.getClassName());

    }

    public ReflectionBeanDefinition(AnnotationMetadata metadata) {
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
        return factoryMethodMetadata;
    }

}