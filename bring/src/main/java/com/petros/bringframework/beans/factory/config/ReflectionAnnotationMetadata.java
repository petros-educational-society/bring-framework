package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.core.AssertUtils;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link AnnotationMetadata} interface that provides metadata
 * information about annotations and annotated methods for a given introspected class
 * using reflection.
 * <p>
 * Extends {@link ReflectionClassMetadata} to handle class-level metadata using reflection.
 *
 * @see AnnotationMetadata
 * @see ReflectionClassMetadata
 * @author "Maksym Oliinyk"
 */
@Log4j2
public class ReflectionAnnotationMetadata extends ReflectionClassMetadata implements AnnotationMetadata {
    private final Set<Annotation> annotations;

    public ReflectionAnnotationMetadata(@Nonnull Class<?> introspectedClass) {
        super(introspectedClass);
        AssertUtils.notNull(introspectedClass, "Class must not be null");
        annotations = Arrays.stream(introspectedClass.getDeclaredAnnotations()).collect(Collectors.toSet());
    }

    /**
     * Retrieves a set of annotation types present on the introspected class.
     *
     * @return a set containing the names of all annotations present on the class
     */
    @Override
    public Set<String> getAnnotationTypes() {
        Set<String> annotationTypes = new HashSet<>();
        annotations.stream()
                .map(Annotation::annotationType)
                .filter(Objects::nonNull)
                .forEach(annotationType -> annotationTypes.add(annotationType.getName()));
        return annotationTypes;
    }

    /**
     * Checks if the introspected class has the specified annotation type.
     *
     * @param annotationType the name of the annotation type to check for
     * @return true if the class has the specified annotation
     */
    @Override
    public boolean hasAnnotation(String annotationType) {
        return annotations.stream()
                .anyMatch(annotation -> annotation.annotationType().getName().equals(annotationType));
    }

    /**
     * Checks if the introspected class has a meta-annotation of the specified name.
     *
     * @param metaAnnotationName the name of the meta-annotation to check for
     * @return true if the class has the specified meta-annotation
     */
    @Override
    public boolean hasMetaAnnotation(String metaAnnotationName) {
        return getAnnotations().stream().map(annotation -> annotation.annotationType())
                .flatMap(annotationType -> Arrays.stream(annotationType.getDeclaredAnnotations()))
                .map(Annotation::annotationType)
                .map(Class::getName)
                .filter(Objects::nonNull)
                .anyMatch(metaAnnotationName::equals);

    }

    /**
     * Checks if the introspected class has methods annotated with the specified annotation.
     *
     * @param annotationName the name of the annotation to check for on methods
     * @return true if any method in the class is annotated with the specified annotation
     */
    @Override
    public boolean hasAnnotatedMethods(String annotationName) {
        return getDeclaredMethods().stream().anyMatch(methodMetadata -> methodMetadata.isAnnotated(annotationName));
    }

    /**
     * Retrieves a set of method metadata objects for methods annotated with the specified annotation.
     *
     * @param annotationName the name of the annotation to filter annotated methods
     * @return a set of method metadata for methods annotated with the specified annotation
     */
    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        return getDeclaredMethods().stream().filter(methodMetadata -> methodMetadata.isAnnotated(annotationName))
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves a set of method metadata objects for all declared methods in the introspected class.
     *
     * @return a set of method metadata for all declared methods in the class
     */
    @Override
    public Set<MethodMetadata> getDeclaredMethods() {
        final Method[] declaredMethods = introspectedClass.getDeclaredMethods();
        return Arrays.stream(declaredMethods)
                .map(ReflectionMethodMetadata::new)
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves a set of annotations present on the introspected class.
     *
     * @return a set containing all annotations present on the class
     */
    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * Retrieves the attributes of the specified annotation from the introspected class.
     * Logs an error if the attributes cannot be retrieved.
     *
     * @param annotationName the name of the annotation to retrieve attributes for
     * @return a map containing the attributes of the specified annotation, or null if not found
     */
    @Nullable
    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return getAnnotationAttributes(annotationName, log::error);
    }
}
