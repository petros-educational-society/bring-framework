package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.core.AssertUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author "Maksym Oliinyk"
 */
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
    public Set<String> getMetaAnnotationTypes(String annotationName) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean hasAnnotation(String annotationType) {
        return annotations.stream()
                .anyMatch(annotation -> annotation.annotationType().getName().equals(annotationType));
    }

    @Override
    public boolean hasMetaAnnotation(String metaAnnotationName) {
        throw new UnsupportedOperationException("Not implemented yet");
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
        Map<String, Object> attributes = new HashMap<>();

        Annotation annotation = getAnnotation(annotationName);
        if (annotation != null) {
            for (Method method : annotation.annotationType().getDeclaredMethods()) {
                try {
                    Object value = method.invoke(annotation);
                    attributes.put(method.getName(), value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return attributes;
    }


}
