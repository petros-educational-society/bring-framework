package com.petros.bringframework.type.reading;

import com.petros.bringframework.beans.factory.config.AnnotationMetadata;
import com.petros.bringframework.beans.factory.config.ClassMetadata;
import com.petros.bringframework.beans.factory.config.ReflectionAnnotationMetadata;
import com.petros.bringframework.beans.factory.config.ReflectionClassMetadata;
import lombok.Getter;

/**
 * @author "Maksym Oliinyk"
 */
public class ReflectionMetadataReader implements MetadataReader {

    @Getter
    private final Class<?> introspectedClass;
    private final ClassMetadata classMetadata;
    private final AnnotationMetadata annotationMetadata;

    public ReflectionMetadataReader(Class<?> introspectedClass) {
        this.introspectedClass = introspectedClass;
        this.classMetadata = new ReflectionClassMetadata(introspectedClass);
        this.annotationMetadata = new ReflectionAnnotationMetadata(introspectedClass);
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
