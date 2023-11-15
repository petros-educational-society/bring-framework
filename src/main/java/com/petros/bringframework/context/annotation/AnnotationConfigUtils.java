package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.AnnotatedBeanDefinition;
import com.petros.bringframework.beans.factory.config.AnnotatedTypeMetadata;
import com.petros.bringframework.beans.factory.config.BeanDefinitionRole;
import com.petros.bringframework.core.AssertUtils;

import java.lang.reflect.Array;

/**
 * Centralizes the logic for processing key annotations
 *
 * @author "Maksym Oliinyk"
 */
public abstract class AnnotationConfigUtils {

    public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
        processCommonDefinitionAnnotations(abd, abd.getMetadata());
    }

    static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, AnnotatedTypeMetadata metadata) {
        var lazy = metadata.getAnnotationAttributes(Lazy.class.getName());
        if (!lazy.isEmpty()) {
            abd.setLazyInit(getRequiredAttribute("value", lazy.get("value"), Boolean.class));
        } else if (abd.getMetadata() != metadata) {
            lazy = abd.getMetadata().getAnnotationAttributes(Lazy.class.getName());
            if (!lazy.isEmpty()) {
                abd.setLazyInit(getRequiredAttribute("value", lazy.get("value"), Boolean.class));
            }
        }

        if (metadata.isAnnotated(Primary.class.getName())) {
            abd.setPrimary(true);
        }
        var dependsOn = metadata.getAnnotationAttributes(DependsOn.class.getName());
        if (!dependsOn.isEmpty()) {
            abd.setDependsOn(getRequiredAttribute("value", dependsOn.get("value"), String[].class));
        }

        var role = metadata.getAnnotationAttributes(Role.class.getName());
        if (!role.isEmpty()) {
            abd.setRole(getRequiredAttribute("value", role.get("value"), BeanDefinitionRole.class).getRole());
        }
        var desciption = metadata.getAnnotationAttributes(Description.class.getName());
        if (!desciption.isEmpty()) {
            abd.setDescription(getRequiredAttribute("value", desciption.get("value"), String.class));
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
}
