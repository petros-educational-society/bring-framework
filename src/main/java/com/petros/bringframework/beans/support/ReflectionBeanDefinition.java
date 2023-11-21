package com.petros.bringframework.beans.support;

import com.petros.bringframework.beans.factory.config.AnnotatedBeanDefinition;
import com.petros.bringframework.beans.factory.config.AnnotationMetadata;
import com.petros.bringframework.beans.factory.config.MethodMetadata;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.type.reading.ReflectionMetadataReader;

import javax.annotation.Nullable;

public class ReflectionBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

    private final AnnotationMetadata metadata;

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
//		setResource(metadataReader.getResource());
    }


    @Override
    public final AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    @Nullable
    public MethodMetadata getFactoryMethodMetadata() {
        return null;
    }

}