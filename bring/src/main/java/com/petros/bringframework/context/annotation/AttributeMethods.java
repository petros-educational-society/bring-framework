package com.petros.bringframework.context.annotation;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * Provides a quick way to access the attribute methods of an {@link Annotation}
 * with consistent ordering as well as a few useful utility methods.
 *
 * @author "Vasiuk Maryna"
 */
final class AttributeMethods {

    static final AttributeMethods NONE = new AttributeMethods(null, new Method[0]);

    private static final Map<Class<? extends Annotation>, AttributeMethods> cache = new ConcurrentHashMap<>();

    private static final Comparator<Method> methodComparator = (m1, m2) -> {
        if (m1 != null && m2 != null) {
            return m1.getName().compareTo(m2.getName());
        }
        return m1 != null ? -1 : 1;
    };


    @Nullable
    private final Class<? extends Annotation> annotationType;

    private final Method[] attributeMethods;

    private final boolean[] canThrowTypeNotPresentException;

    private final boolean hasDefaultValueMethod;

    private final boolean hasNestedAnnotation;


    private AttributeMethods(@Nullable Class<? extends Annotation> annotationType, Method[] attributeMethods) {
        this.annotationType = annotationType;
        this.attributeMethods = attributeMethods;
        this.canThrowTypeNotPresentException = new boolean[attributeMethods.length];
        boolean foundDefaultValueMethod = false;
        boolean foundNestedAnnotation = false;
        for (int i = 0; i < attributeMethods.length; i++) {
            Method method = this.attributeMethods[i];
            Class<?> type = method.getReturnType();
            if (method.getDefaultValue() != null) {
                foundDefaultValueMethod = true;
            }
            if (type.isAnnotation() || (type.isArray() && type.getComponentType().isAnnotation())) {
                foundNestedAnnotation = true;
            }
            method.setAccessible(true);
            this.canThrowTypeNotPresentException[i] = (type == Class.class || type == Class[].class || type.isEnum());
        }
        this.hasDefaultValueMethod = foundDefaultValueMethod;
        this.hasNestedAnnotation = foundNestedAnnotation;
    }

    /**
     * Check if values from the given annotation can be safely accessed without causing
     * any {@link TypeNotPresentException TypeNotPresentExceptions}. In particular,
     * this method is designed to cover Google App Engine's late arrival of such
     * exceptions for {@code Class} values (instead of the more typical early
     * {@code Class.getAnnotations() failure}.
     *
     * @param annotation the annotation to validate
     * @throws IllegalStateException if a declared {@code Class} attribute could not be read
     */
    void validate(Annotation annotation) {
        assertAnnotation(annotation);
        for (int i = 0; i < size(); i++) {
            if (canThrowTypeNotPresentException(i)) {
                try {
                    get(i).invoke(annotation);
                } catch (Throwable ex) {
                    throw new IllegalStateException("Could not obtain annotation attribute value for " +
                            get(i).getName() + " declared on " + annotation.annotationType(), ex);
                }
            }
        }
    }

    private void assertAnnotation(Annotation annotation) {
        requireNonNull(annotation, "Annotation must not be null");
        if (annotationType != null && !annotationType.isInstance(annotation)) {
            throw new IllegalArgumentException("Annotation is not an instance of the expected type");
        }
    }

    /**
     * Get the attribute at the specified index.
     *
     * @param index the index of the attribute to return
     * @return the attribute method
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    Method get(int index) {
        return this.attributeMethods[index];
    }

    /**
     * Determine if the attribute at the specified index could throw a
     * {@link TypeNotPresentException} when accessed.
     *
     * @param index the index of the attribute to check
     * @return {@code true} if the attribute can throw a
     * {@link TypeNotPresentException}
     */
    boolean canThrowTypeNotPresentException(int index) {
        return this.canThrowTypeNotPresentException[index];
    }

    /**
     * Get the number of attributes in this collection.
     *
     * @return the number of attributes
     */
    int size() {
        return this.attributeMethods.length;
    }

    /**
     * Get the attribute methods for the given annotation type.
     *
     * @param annotationType the annotation type
     * @return the attribute methods for the annotation type
     */
    static AttributeMethods forAnnotationType(@Nullable Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            return NONE;
        }
        return cache.computeIfAbsent(annotationType, AttributeMethods::compute);
    }

    private static AttributeMethods compute(Class<? extends Annotation> annotationType) {
        Method[] methods = annotationType.getDeclaredMethods();
        int size = methods.length;
        for (int i = 0; i < methods.length; i++) {
            if (!isAttributeMethod(methods[i])) {
                methods[i] = null;
                size--;
            }
        }
        if (size == 0) {
            return NONE;
        }
        Arrays.sort(methods, methodComparator);
        Method[] attributeMethods = Arrays.copyOf(methods, size);
        return new AttributeMethods(annotationType, attributeMethods);
    }

    private static boolean isAttributeMethod(Method method) {
        return (method.getParameterCount() == 0 && method.getReturnType() != void.class);
    }
}
