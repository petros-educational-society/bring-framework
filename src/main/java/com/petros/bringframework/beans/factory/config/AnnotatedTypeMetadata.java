package com.petros.bringframework.beans.factory.config;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Defines access to the annotations of a specific type ({@link AnnotationMetadata class}
 * or {@link MethodMetadata method}), in a form that does not necessarily require the
 * class-loading.
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Mark Pollack
 * @author Chris Beams
 * @author Phillip Webb
 * @author Sam Brannen
 * @see AnnotationMetadata
 * @see MethodMetadata
 * @since 4.0
 */
public interface AnnotatedTypeMetadata {

    /**
     * Return annotation details based on the direct annotations of the
     * underlying element.
     *
     * @return merged annotations based on the direct annotations
     * @since 5.2
     */
    Set<Annotation> getAnnotations();

    /**
     * Determine whether the underlying element has an annotation or meta-annotation
     * of the given type defined.
     * <p>If this method returns {@code true}, then
     * {@link #getAnnotationAttributes} will return a non-null Map.
     *
     * @param annotationName the fully qualified class name of the annotation
     *                       type to look for
     * @return whether a matching annotation is defined
     */
    default boolean isAnnotated(String annotationName) {
        return getAnnotations().stream().anyMatch(annotation -> annotation.annotationType().getName().equals(annotationName));
    }

    /**
     * Retrieve the attributes of the annotation of the given type, if any (i.e. if
     * defined on the underlying element, as direct annotation or meta-annotation),
     * also taking attribute overrides on composed annotations into account.
     *
     * @param annotationName the fully qualified class name of the annotation
     *                       type to look for
     * @return a Map of attributes, with the attribute name as key (e.g. "value")
     * and the defined attribute value as Map value. This return value will be
     * {@code null} if no matching annotation is defined.
     */
    @Nullable
    Map<String, Object> getAnnotationAttributes(String annotationName);

    default Annotation getAnnotation(final String annotationName) {
        return getAnnotations().stream()
                .filter(annotation -> annotation.annotationType().getName().equals(annotationName))
                .findFirst().orElse(null);
    }

    default Map<String, Object> getAnnotationAttributes(String annotationName, BiConsumer<String, Throwable> logger) {
        Map<String, Object> attributes = new HashMap<>();

        Annotation annotation = getAnnotation(annotationName);
        if (annotation != null) {
            for (Method method : annotation.annotationType().getDeclaredMethods()) {
                try {
                    Object value = method.invoke(annotation);
                    attributes.put(method.getName(), value);
                } catch (Exception e) {
                    logger.accept(e.getMessage(), e);
                }
            }
        }

        return attributes;
    }

    /**
     * Retrieve the attributes of the annotation of the given type, if any (i.e. if
     * defined on the underlying element, as direct annotation or meta-annotation),
     * also taking attribute overrides on composed annotations into account.
     *
     * @param annotationName      the fully qualified class name of the annotation
     *                            type to look for
     * @param classValuesAsString whether to convert class references to String
     *                            class names for exposure as values in the returned Map, instead of Class
     *                            references which might potentially have to be loaded first
     * @return a Map of attributes, with the attribute name as key (e.g. "value")
     * and the defined attribute value as Map value. This return value will be
     * {@code null} if no matching annotation is defined.
     */
    @Nullable
    default Map<String, Object> getAnnotationAttributes(String annotationName,
                                                        boolean classValuesAsString) {
        throw new UnsupportedOperationException();
    }


}