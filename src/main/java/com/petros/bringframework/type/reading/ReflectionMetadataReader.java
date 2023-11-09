package com.petros.bringframework.type.reading;

import com.petros.bringframework.beans.factory.config.AnnotationMetadata;
import com.petros.bringframework.beans.factory.config.ClassMetadata;

/**
 * @author "Maksym Oliinyk"
 */
public class ReflectionMetadataReader implements MetadataReader {

    private final Class<?> clazz;
    private final ClassMetadata classMetadata;
    private final AnnotationMetadata annotationMetadata;

    public ReflectionMetadataReader(Class<?> clazz, ClassMetadata classMetadata, AnnotationMetadata annotationMetadata) {
        this.clazz = clazz;
        this.classMetadata = classMetadata;
        this.annotationMetadata = annotationMetadata;
    }

    @Override
    public ClassMetadata getClassMetadata() {
        return classMetadata;
    }

    @Override
    public AnnotationMetadata getAnnotationMetadata() {
        return annotationMetadata;
    }
}
