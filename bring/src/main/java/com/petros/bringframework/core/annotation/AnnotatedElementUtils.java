package com.petros.bringframework.core.annotation;

import com.petros.bringframework.beans.factory.support.AnnotationAttributes;
import lombok.extern.log4j.Log4j2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for retrieving annotation attributes from annotated elements, such as methods.
 *
 * @author "Maksym Oliinyk"
 */
@Log4j2
public abstract class AnnotatedElementUtils {

    /**
     * Retrieves annotation attributes from the provided method based on the specified annotation type.
     *
     * @param beanMethod      The method from which to retrieve annotation attributes.
     * @param annotationType  The type of annotation for attribute retrieval.
     * @return                Annotation attributes as {@link AnnotationAttributes} if found; otherwise, null.
     */
    public static AnnotationAttributes getAnnotationAttributes(Method beanMethod, Class<?> annotationType) {
        final Function<Annotation, Map<String, Object>> retrieveAnnotationAttributesFunction
                = annotation -> Arrays.stream(annotation.annotationType().getDeclaredMethods())
                .collect(Collectors.toMap(Method::getName,
                        method -> {
                            try {
                                return method.invoke(annotation);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                return null;
                            }
                        },
                        (existing, replacement) -> existing))
                .entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return Arrays.stream(beanMethod.getDeclaredAnnotations())
                .filter(annotation -> annotation.annotationType().equals(annotationType))
                .findFirst()
                .map(retrieveAnnotationAttributesFunction)
                .map(AnnotationAttributes::fromMap)
                .orElse(null);
    }

}
