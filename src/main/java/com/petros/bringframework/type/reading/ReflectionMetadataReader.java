package com.petros.bringframework.type.reading;

import com.petros.bringframework.beans.factory.config.AnnotationMetadata;
import com.petros.bringframework.beans.factory.config.ClassMetadata;
import com.petros.bringframework.beans.factory.config.ReflectionAnnotationMetadata;
import com.petros.bringframework.beans.factory.config.ReflectionClassMetadata;

/**
 * @author "Maksym Oliinyk"
 */
public class ReflectionMetadataReader implements MetadataReader {

    private final Class<?> clazz;
    private final ClassMetadata classMetadata;
    private final AnnotationMetadata annotationMetadata;

    public ReflectionMetadataReader(Class<?> clazz) {
        this.clazz = clazz;
        this.classMetadata = new ReflectionClassMetadata(clazz);
        this.annotationMetadata = new ReflectionAnnotationMetadata(clazz);
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
