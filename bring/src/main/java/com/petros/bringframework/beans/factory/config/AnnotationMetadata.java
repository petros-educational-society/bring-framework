package com.petros.bringframework.beans.factory.config;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author "Maksym Oliinyk"
 */
public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata {
    /**
     * Get the fully qualified class names of all annotation types that
     * are <em>present</em> on the underlying class.
     *
     * @return the annotation type names
     */
    Set<String> getAnnotationTypes();

    /**
     * Get the fully qualified class names of all meta-annotation types that
     * are <em>present</em> on the given annotation type on the underlying class.
     *
     * @param annotationName the fully qualified class name of the meta-annotation
     *                       type to look for
     * @return the meta-annotation type names, or an empty set if none found
     */
    default Set<String> getMetaAnnotationTypes(String annotationName) {
        return Optional.ofNullable(getAnnotation(annotationName))
                .map(annotation -> {
                    final Annotation[] declaredAnnotations = annotation.annotationType().getDeclaredAnnotations();
                    return Arrays.stream(declaredAnnotations)
                            .map(Annotation::annotationType)
                            .map(Class::getName)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                }).orElseGet(LinkedHashSet::new);
    }

    /**
     * Determine whether an annotation of the given type is <em>present</em> on
     * the underlying class.
     *
     * @param annotationName the fully qualified class name of the annotation
     *                       type to look for
     * @return {@code true} if a matching annotation is present
     */
    boolean hasAnnotation(String annotationName);

    /**
     * Determine whether the underlying class has an annotation that is itself
     * annotated with the meta-annotation of the given type.
     *
     * @param metaAnnotationName the fully qualified class name of the
     *                           meta-annotation type to look for
     * @return {@code true} if a matching meta-annotation is present
     */
    boolean hasMetaAnnotation(String metaAnnotationName);

    /**
     * Determine whether the underlying class has any methods that are
     * annotated (or meta-annotated) with the given annotation type.
     *
     * @param annotationName the fully qualified class name of the annotation
     *                       type to look for
     */
    boolean hasAnnotatedMethods(String annotationName);

    /**
     * Retrieve the method metadata for all methods that are annotated
     * (or meta-annotated) with the given annotation type.
     * <p>For any returned method, {@link MethodMetadata#isAnnotated} will
     * return {@code true} for the given annotation type.
     *
     * @param annotationName the fully qualified class name of the annotation
     *                       type to look for
     * @return a set of {@link MethodMetadata} for methods that have a matching
     * annotation. The return value will be an empty set if no methods match
     * the annotation type.
     */
    Set<MethodMetadata> getAnnotatedMethods(String annotationName);

    /**
     * Retrieve the method metadata for all user-declared methods on the
     * underlying class, preserving declaration order as far as possible.
     *
     * @return a set of {@link MethodMetadata}
     */
    Set<MethodMetadata> getDeclaredMethods();


}
