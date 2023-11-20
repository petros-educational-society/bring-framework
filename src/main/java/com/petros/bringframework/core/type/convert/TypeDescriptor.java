package com.petros.bringframework.core.type.convert;

import com.petros.bringframework.core.type.ResolvableType;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@SuppressWarnings("serial")
public class TypeDescriptor implements Serializable {

    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];

    private static final Map<Class<?>, TypeDescriptor> commonTypesCache = new HashMap<>(32);

    private static final Class<?>[] CACHED_COMMON_TYPES = {
            boolean.class, Boolean.class, byte.class, Byte.class, char.class, Character.class,
            double.class, Double.class, float.class, Float.class, int.class, Integer.class,
            long.class, Long.class, short.class, Short.class, String.class, Object.class};

    static {
        for (Class<?> preCachedClass : CACHED_COMMON_TYPES) {
            commonTypesCache.put(preCachedClass, valueOf(preCachedClass));
        }
    }

    private final Class<?> type;

    private final ResolvableType resolvableType;

    private final AnnotatedElementAdapter annotatedElement;

    public TypeDescriptor(MethodParameter methodParameter) {
        this.resolvableType = ResolvableType.forMethodParameter(methodParameter);
        this.type = this.resolvableType.resolve(methodParameter.getNestedParameterType());
        this.annotatedElement = new AnnotatedElementAdapter(methodParameter.getParameterIndex() == -1 ?
                methodParameter.getMethodAnnotations() : methodParameter.getParameterAnnotations());
    }

    public TypeDescriptor(ResolvableType resolvableType, @Nullable Class<?> type, @Nullable Annotation[] annotations) {
        this.resolvableType = resolvableType;
        this.type = (type != null ? type : resolvableType.toClass());
        this.annotatedElement = new AnnotatedElementAdapter(annotations);
    }
    /**
     * Create a new type descriptor from the given type
     * <p>Use this to instruct the conversion system to convert an object to a
     * specific target type, when no type location such as a method parameter or
     * field is available to provide additional conversion context.
     * @param type the class (may be {@code null} to indicate {@code Object.class})
     * @return the corresponding type descriptor
     */
    public static TypeDescriptor valueOf(@Nullable Class<?> type) {
        if (type == null) {
            type = Object.class;
        }
        var descriptor = commonTypesCache.get(type);
        if (nonNull(descriptor)) {
            return descriptor;
        }
        return new TypeDescriptor(ResolvableType.forRawClass(type), null, null);
    }

    private class AnnotatedElementAdapter implements AnnotatedElement, Serializable {

        @Nullable
        private final Annotation[] annotations;

        public AnnotatedElementAdapter(@Nullable Annotation[] annotations) {
            this.annotations = annotations;
        }

        @Override
        public boolean isAnnotationPresent(@Nonnull Class<? extends Annotation> annotationClass) {
            return Arrays.stream(getAnnotations())
                    .anyMatch(a -> a.annotationType() == annotationClass);
        }

        @Override
        @Nullable
        @SuppressWarnings("unchecked")
        public <T extends Annotation> T getAnnotation(@Nonnull Class<T> annotationClass) {
            return (T) Arrays.stream(getAnnotations())
                    .filter(a -> a.annotationType() == annotationClass)
                    .findAny().orElse(null);
        }

        @Override
        public Annotation[] getAnnotations() {
            return annotations != null ? annotations.clone() : EMPTY_ANNOTATION_ARRAY;
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            return getAnnotations();
        }

        public boolean isEmpty() {
            return ObjectUtils.isEmpty(annotations);
        }

        @Override
        public boolean equals(@Nullable Object other) {
            return (this == other || (other instanceof AnnotatedElementAdapter that &&
                    Arrays.equals(this.annotations, that.annotations)));
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.annotations);
        }

        @Override
        public String toString() {
            return TypeDescriptor.this.toString();
        }
    }
}
