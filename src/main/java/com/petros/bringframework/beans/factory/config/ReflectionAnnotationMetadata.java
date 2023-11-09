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
public class ReflectionAnnotationMetadata implements AnnotationMetadata {

    private final Class<?> clazz;
    private final Set<Annotation> annotations;

    public ReflectionAnnotationMetadata(@Nonnull Class<?> clazz) {
        AssertUtils.notNull(clazz, "Class must not be null");
        this.clazz = clazz;
        annotations = Arrays.stream(clazz.getAnnotations()).collect(Collectors.toSet());
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
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Nullable
    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return null;
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
    public String getClassName() {
        return null;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isAnnotation() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isIndependent() {
        return false;
    }

    @Nullable
    @Override
    public String getEnclosingClassName() {
        return null;
    }

    @Nullable
    @Override
    public String getSuperClassName() {
        return null;
    }

    @Override
    public String[] getInterfaceNames() {
        return new String[0];
    }

    @Override
    public String[] getMemberClassNames() {
        return new String[0];
    }
}
