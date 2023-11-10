package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.core.AssertUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author "Maksym Oliinyk"
 */
public class ReflectionAnnotationMetadata extends ReflectionClassMetadata implements AnnotationMetadata {
    //todo finish this class
    private final Set<Annotation> annotations;

    public ReflectionAnnotationMetadata(@Nonnull Class<?> introspectedClass) {
        super(introspectedClass);
        AssertUtils.notNull(introspectedClass, "Class must not be null");
        annotations = Arrays.stream(introspectedClass.getAnnotations()).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAnnotationTypes() {
        Set<String> annotationTypes = new HashSet<>();
        annotations.stream()
                .map(Annotation::annotationType)
                .filter(Objects::nonNull)
                .forEach(annotationType -> annotationTypes.add(annotationType.getName()));
        return annotationTypes;
    }

    @Override
    public Set<String> getMetaAnnotationTypes(String annotationName) {
        return null;
    }

    @Override
    public boolean hasAnnotation(String annotationType) {
        return annotations.stream()
                .anyMatch(annotation -> annotation.annotationType().getName().equals(annotationType));
    }

    @Override
    public boolean hasMetaAnnotation(String metaAnnotationName) {
        return false;
    }

    @Override
    public boolean hasAnnotatedMethods(String annotationName) {
        return false;
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        return null;
    }

    @Override
    public Set<MethodMetadata> getDeclaredMethods() {
        return null;
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Nullable
    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return null;
    }


}
