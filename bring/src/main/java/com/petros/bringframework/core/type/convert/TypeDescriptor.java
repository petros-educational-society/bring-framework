package com.petros.bringframework.core.type.convert;

import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.core.type.ResolvableType;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

/**
 * Contextual descriptor about a type to convert from or to.
 * <p>Capable of representing arrays and generic collection types.
 *
 * @author Viktor Basanets
 *
 * @see ConversionService#canConvert(TypeDescriptor, TypeDescriptor)
 * @see ConversionService#convert(Object, TypeDescriptor, TypeDescriptor)
 */

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

    /**
     * Create a new type descriptor for an object.
     * <p>Use this factory method to introspect a source object before asking the
     * conversion system to convert it to some other type.
     * <p>If the provided object is {@code null}, returns {@code null}, else calls
     * {@link #valueOf(Class)} to build a TypeDescriptor from the object's class.
     * @param source the source object
     * @return the type descriptor
     */
    @Nullable
    public static TypeDescriptor forObject(@Nullable Object source) {
        return (source != null ? valueOf(source.getClass()) : null);
    }

    /**
     * If this type is an array, returns the array's component type.
     * If this type is a {@code Stream}, returns the stream's component type.
     * If this type is a {@link Collection} and it is parameterized, returns the Collection's element type.
     * If the Collection is not parameterized, returns {@code null} indicating the element type is not declared.
     * @return the array component type or Collection element type, or {@code null} if this type is not
     * an array type or a {@code java.util.Collection} or if its element type is not parameterized
     */
    @Nullable
    public TypeDescriptor getElementTypeDescriptor() {
        if (getResolvableType().isArray()) {
            return new TypeDescriptor(getResolvableType().getComponentType(), null, getAnnotations());
        }
        if (Stream.class.isAssignableFrom(getType())) {
            return getRelatedIfResolvable(this, getResolvableType().as(Stream.class).getGeneric(0));
        }
        return getRelatedIfResolvable(this, getResolvableType().asCollection().getGeneric(0));
    }

    public ResolvableType getResolvableType() {
        return this.resolvableType;
    }

    /**
     * Return the annotations associated with this type descriptor, if any.
     * @return the annotations, or an empty array if none
     */
    public Annotation[] getAnnotations() {
        return this.annotatedElement.getAnnotations();
    }

    /**
     * The type of the backing class, method parameter, field, or property
     * described by this TypeDescriptor.
     * variation of this operation that resolves primitive types to their
     * corresponding Object types if necessary.
     */
    public Class<?> getType() {
        return this.type;
    }

    @Nullable
    private static TypeDescriptor getRelatedIfResolvable(TypeDescriptor source, ResolvableType type) {
        if (type.resolve() == null) {
            return null;
        }
        return new TypeDescriptor(type, null, source.getAnnotations());
    }

    /**
     * If this type is a {@link Map} and its key type is parameterized,
     * returns the map's key type. If the Map's key type is not parameterized,
     * returns {@code null} indicating the key type is not declared.
     * @return the Map key type, or {@code null} if this type is a Map
     * but its key type is not parameterized
     * @throws IllegalStateException if this type is not a {@code java.util.Map}
     */
    @Nullable
    public TypeDescriptor getMapKeyTypeDescriptor() {
        AssertUtils.state(isMap(), "Not a [java.util.Map]");
        return getRelatedIfResolvable(this, getResolvableType().asMap().getGeneric(0));
    }

    public boolean isMap() {
        return Map.class.isAssignableFrom(getType());
    }

    /**
     * If this type is a {@link Map} and its value type is parameterized,
     * returns the map's value type.
     * <p>If the Map's value type is not parameterized, returns {@code null}
     * indicating the value type is not declared.
     * @return the Map value type, or {@code null} if this type is a Map
     * but its value type is not parameterized
     * @throws IllegalStateException if this type is not a {@code java.util.Map}
     */
    @Nullable
    public TypeDescriptor getMapValueTypeDescriptor() {
        AssertUtils.state(isMap(), "Not a [java.util.Map]");
        return getRelatedIfResolvable(this, getResolvableType().asMap().getGeneric(1));
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
