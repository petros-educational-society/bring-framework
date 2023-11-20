package com.petros.bringframework.core.type.convert;

import com.petros.bringframework.core.type.ResolvableType;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.annotation.Annotation;
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
}
