package com.petros.bringframework.beans.factory.config;

import java.util.Collections;
import java.util.LinkedHashSet;
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
    default Set<String> getAnnotationTypes() {
        return getAnnotations().stream()
                .filter(MergedAnnotation::isDirectlyPresent)
                .map(annotation -> annotation.getType().getName())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Get the fully qualified class names of all meta-annotation types that
     * are <em>present</em> on the given annotation type on the underlying class.
     *
     * @param annotationName the fully qualified class name of the meta-annotation
     *                       type to look for
     * @return the meta-annotation type names, or an empty set if none found
     */
    default Set<String> getMetaAnnotationTypes(String annotationName) {
        MergedAnnotation<?> annotation = getAnnotations().get(annotationName, MergedAnnotation::isDirectlyPresent);
        if (!annotation.isPresent()) {
            return Collections.emptySet();
        }
        return MergedAnnotations.from(annotation.getType(), SearchStrategy.INHERITED_ANNOTATIONS).stream()
                .map(mergedAnnotation -> mergedAnnotation.getType().getName())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Determine whether an annotation of the given type is <em>present</em> on
     * the underlying class.
     *
     * @param annotationName the fully qualified class name of the annotation
     *                       type to look for
     * @return {@code true} if a matching annotation is present
     */
    default boolean hasAnnotation(String annotationName) {
        return getAnnotations().isDirectlyPresent(annotationName);
    }

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
     * @since 6.0
     */
    Set<MethodMetadata> getDeclaredMethods();


}
