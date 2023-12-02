package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.AnnotatedBeanDefinition;
import com.petros.bringframework.beans.factory.config.AnnotatedTypeMetadata;
import com.petros.bringframework.beans.factory.config.AnnotationMetadata;
import com.petros.bringframework.beans.factory.config.BeanDefinitionRole;
import com.petros.bringframework.beans.factory.support.AnnotationAttributes;
import com.petros.bringframework.core.AssertUtils;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Centralizes the logic for processing key annotations
 *
 * @author "Maksym Oliinyk"
 */
public abstract class AnnotationConfigUtils {

    public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
        processCommonDefinitionAnnotations(abd, abd.getMetadata());
    }

    public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, AnnotatedTypeMetadata metadata) {
        var lazy = metadata.getAnnotationAttributes(Lazy.class.getName());
        if (lazy != null && !lazy.isEmpty()) {
            abd.setLazyInit(getRequiredAttribute("value", lazy.get("value"), Boolean.class));
        } else if (abd.getMetadata() != metadata) {
            lazy = abd.getMetadata().getAnnotationAttributes(Lazy.class.getName());
            if (lazy != null && !lazy.isEmpty()) {
                abd.setLazyInit(getRequiredAttribute("value", lazy.get("value"), Boolean.class));
            }
        }

        if (metadata.isAnnotated(Primary.class.getName())) {
            abd.setPrimary(true);
        }
        var dependsOn = metadata.getAnnotationAttributes(DependsOn.class.getName());
        if (dependsOn != null && !dependsOn.isEmpty()) {
            abd.setDependsOn(getRequiredAttribute("value", dependsOn.get("value"), String[].class));
        }

        var role = metadata.getAnnotationAttributes(Role.class.getName());
        if (role != null && !role.isEmpty()) {
            abd.setRole(getRequiredAttribute("value", role.get("value"), BeanDefinitionRole.class).getRole());
        }
        var desciption = metadata.getAnnotationAttributes(Description.class.getName());
        if (desciption != null && !desciption.isEmpty()) {
            abd.setDescription(getRequiredAttribute("value", desciption.get("value"), String.class));
        }

    }

    /**
     * Processes the scope metadata for a bean definition based on its annotation metadata.
     * This method is responsible for extracting scope-related attributes from the provided
     * annotation metadata and updating the given ScopeMetadata instance accordingly.
     *
     * The method specifically looks for the Scope annotation on the bean definition. If the
     * annotation is present, it performs the following actions:
     * - Retrieves the 'value' attribute from the Scope annotation and sets the scope name in
     *   the ScopeMetadata object.
     * - Retrieves the 'proxyMode' attribute from the Scope annotation and sets the scoped
     *   proxy mode in the ScopeMetadata object.
     *
     * If the Scope annotation is not present or if it doesn't have the necessary attributes,
     * the ScopeMetadata is left unmodified.
     *
     * @param scopeMetadata The ScopeMetadata instance to be updated with the scope information.
     * @param annotationMetadata The metadata of the annotations present on the bean definition, used to
     *                           extract the scope information.
     */
    public static void processScopeMetadata(ScopeMetadata scopeMetadata, AnnotationMetadata annotationMetadata) {
        var attributes = annotationMetadata.getAnnotationAttributes(Scope.class.getName());

        if (attributes != null && !attributes.isEmpty()) {
            scopeMetadata.setScopeName(getRequiredAttribute("value", attributes.get("value"), String.class));
            scopeMetadata.setScopedProxyMode(getRequiredAttribute("proxyMode", attributes.get("proxyMode"), ScopedProxyMode.class));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getRequiredAttribute(String attributeName, Object value, Class<T> expectedType) {
        AssertUtils.notBlank(attributeName, "'attributeName' must not be null or empty");
        AssertUtils.notNull(value, "Attribute '" + attributeName + "' should not be null");

        if (!expectedType.isInstance(value) && expectedType.isArray() &&
                expectedType.getComponentType().isInstance(value)) {
            Object array = Array.newInstance(expectedType.getComponentType(), 1);
            Array.set(array, 0, value);
            value = array;
        }
        if (!expectedType.isInstance(value)) {
            throw new IllegalArgumentException(String.format(
                    "Attribute '%s' is of type %s, but %s was expected another type",
                    attributeName, value.getClass().getSimpleName(), expectedType.getSimpleName()));
        }
        return (T) value;
    }

    public static Set<AnnotationAttributes> attributesForRepeatable(AnnotationMetadata metadata, String annotationClassName) {
        Set<AnnotationAttributes> result = new LinkedHashSet<>();

        addAttributesIfNotNull(result, metadata.getAnnotationAttributes(annotationClassName));

        return Collections.unmodifiableSet(result);
    }

    private static void addAttributesIfNotNull(Set<AnnotationAttributes> result, @Nullable Map<String, Object> attributes) {

        if (attributes != null) {
            result.add(AnnotationAttributes.fromMap(attributes));
        }
    }

    public static void validateAnnotation(Annotation annotation) {
        AttributeMethods.forAnnotationType(annotation.annotationType()).validate(annotation);
    }

    @Nullable
    public static AnnotationAttributes attributesFor(AnnotatedTypeMetadata metadata, Class<?> annotationClass) {
        return attributesFor(metadata, annotationClass.getName());
    }

    @Nullable
    static AnnotationAttributes attributesFor(AnnotatedTypeMetadata metadata, String annotationClassName) {
        return AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(annotationClassName));
    }
}
