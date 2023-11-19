package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.util.ClassUtils;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link LinkedHashMap} subclass representing annotation attribute
 *
 * @author "Vasiuk Maryna"
 */

public class AnnotationAttributes extends LinkedHashMap<String, Object> {
    private static final String UNKNOWN = "unknown";

    @Nullable
    private final Class<? extends Annotation> annotationType;

    final String displayName;

    /**
     * Create a new {@link AnnotationAttributes} instance, wrapping the provided
     * map and all its <em>key-value</em> pairs.
     * @param map original source of annotation attribute <em>key-value</em> pairs
     */
    public AnnotationAttributes(Map<String, Object> map) {
        super(map);
        this.annotationType = null;
        this.displayName = UNKNOWN;
    }

    @Nullable
    public static AnnotationAttributes fromMap(@Nullable Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        if (map instanceof AnnotationAttributes) {
            return (AnnotationAttributes) map;
        }
        return new AnnotationAttributes(map);
    }

    /**
     * Get the value stored under the specified {@code attributeName} as an
     * array of strings.
     * <p>If the value stored under the specified {@code attributeName} is
     * a string, it will be wrapped in a single-element array before
     * returning it.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @return the value
     * @throws IllegalArgumentException if the attribute does not exist or
     * if it is not of the expected type
     */
    public String[] getStringArray(String attributeName) {
        return getRequiredAttribute(attributeName, String[].class);
    }

    /**
     * Get the value stored under the specified {@code attributeName},
     * ensuring that the value is of the {@code expectedType}.
     * <p>If the {@code expectedType} is an array and the value stored
     * under the specified {@code attributeName} is a single element of the
     * component type of the expected array type, the single element will be
     * wrapped in a single-element array of the appropriate type before
     * returning it.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @param expectedType the expected type; never {@code null}
     * @return the value
     * @throws IllegalArgumentException if the attribute does not exist or
     * if it is not of the expected type
     */
    @SuppressWarnings("unchecked")
    private <T> T getRequiredAttribute(String attributeName, Class<T> expectedType) {
        AssertUtils.hasText(attributeName, "'attributeName' must not be null or empty");
        Object value = get(attributeName);
        assertAttributePresence(attributeName, value);
        assertNotException(attributeName, value);
        if (!expectedType.isInstance(value) && expectedType.isArray() &&
                expectedType.getComponentType().isInstance(value)) {
            Object array = Array.newInstance(expectedType.getComponentType(), 1);
            Array.set(array, 0, value);
            value = array;
        }
        assertAttributeType(attributeName, value, expectedType);
        return (T) value;
    }

    private void assertAttributePresence(String attributeName, Object attributeValue) {
        AssertUtils.notNull(attributeValue, String.format(
                "Attribute '%s' not found in attributes for annotation [%s]",
                attributeName, this.displayName));
    }

    private void assertNotException(String attributeName, Object attributeValue) {
        if (attributeValue instanceof Throwable) {
            throw new IllegalArgumentException(String.format(
                    "Attribute '%s' for annotation [%s] was not resolvable due to exception [%s]",
                    attributeName, this.displayName, attributeValue), (Throwable) attributeValue);
        }
    }

    private void assertAttributeType(String attributeName, Object attributeValue, Class<?> expectedType) {
        if (!expectedType.isInstance(attributeValue)) {
            throw new IllegalArgumentException(String.format(
                    "Attribute '%s' is of type %s, but %s was expected in attributes for annotation [%s]",
                    attributeName, attributeValue.getClass().getSimpleName(), expectedType.getSimpleName(),
                    this.displayName));
        }
    }

    @Override
    public String toString() {
        Iterator<Map.Entry<String, Object>> entries = entrySet().iterator();
        StringBuilder sb = new StringBuilder("{");
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(valueToString(entry.getValue()));
            sb.append(entries.hasNext() ? ", " : "");
        }
        sb.append("}");
        return sb.toString();
    }

    private String valueToString(Object value) {
        if (value == this) {
            return "(this Map)";
        }
        if (value instanceof Object[]) {
            return "[" + ClassUtils.arrayToDelimitedString((Object[]) value, ", ") + "]";
        }
        return String.valueOf(value);
    }

}
