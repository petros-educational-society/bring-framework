package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.core.AssertUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author "Maksym Oliinyk"
 */
@Slf4j
public class ReflectionAnnotationMetadata extends ReflectionClassMetadata implements AnnotationMetadata {
    private final Set<Annotation> annotations;

    public ReflectionAnnotationMetadata(@Nonnull Class<?> introspectedClass) {
        super(introspectedClass);
        AssertUtils.notNull(introspectedClass, "Class must not be null");
        annotations = Arrays.stream(introspectedClass.getDeclaredAnnotations()).collect(Collectors.toSet());
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
    public boolean hasAnnotation(String annotationType) {
        return annotations.stream()
                .anyMatch(annotation -> annotation.annotationType().getName().equals(annotationType));
    }

    @Override
    public boolean hasMetaAnnotation(String metaAnnotationName) {
        return getAnnotations().stream().map(annotation -> annotation.annotationType())
                .flatMap(annotationType -> Arrays.stream(annotationType.getDeclaredAnnotations()))
                .map(Annotation::annotationType)
                .map(Class::getName)
                .filter(Objects::nonNull)
                .anyMatch(metaAnnotationName::equals);

    }

    @Override
    public boolean hasAnnotatedMethods(String annotationName) {
        return getDeclaredMethods().stream().anyMatch(methodMetadata -> methodMetadata.isAnnotated(annotationName));
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        return getDeclaredMethods().stream().filter(methodMetadata -> methodMetadata.isAnnotated(annotationName))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<MethodMetadata> getDeclaredMethods() {
        final Method[] declaredMethods = introspectedClass.getDeclaredMethods();
        return Arrays.stream(declaredMethods)
                .map(ReflectionMethodMetadata::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Nullable
    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return getAnnotationAttributes(annotationName, log::error);
    }
}
