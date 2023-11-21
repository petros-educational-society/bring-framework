package com.petros.bringframework.core.type;

import com.petros.bringframework.core.type.provider.TypeProvider;
import com.petros.bringframework.core.type.resolver.VariableResolver;
import com.petros.bringframework.util.ObjectUtils;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.Objects;

/**
 * @author "Maksym Oliinyk"
 */
public class ResolvableType implements Serializable {

    /**
     * {@code ResolvableType} returned when no value is available. {@code NONE} is used
     * in preference to {@code null} so that multiple method calls can be safely chained.
     */
    public static final ResolvableType NONE = new ResolvableType(EmptyType.INSTANCE, null, null, null);

    /**
     * The underlying Java type being managed.
     */
    private final Type type;
    /**
     * Optional provider for the type.
     */
    @Nullable
    private final TypeProvider typeProvider;

    /**
     * The {@code VariableResolver} to use or {@code null} if no resolver is available.
     */
    @Nullable
    private final VariableResolver variableResolver;

    /**
     * The component type for an array or {@code null} if the type should be deduced.
     */
    @Nullable
    private final ResolvableType componentType;

    @Nullable
    private Class<?> resolved;

    @Nullable
    private volatile ResolvableType superType;

    @Nullable
    private volatile ResolvableType[] interfaces;

    @Nullable
    private volatile ResolvableType[] generics;

    private ResolvableType(Type type, @Nullable TypeProvider typeProvider,
                           @Nullable VariableResolver variableResolver, @Nullable ResolvableType componentType) {

        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.componentType = componentType;
        this.resolved = resolveClass();
    }

    private ResolvableType(@Nullable Class<?> clazz) {
        this.resolved = (clazz != null ? clazz : Object.class);
        this.type = this.resolved;
        this.typeProvider = null;
        this.variableResolver = null;
        this.componentType = null;
    }

    public static <T> ResolvableType forRawClass(Class<T> requiredType) {
        Objects.requireNonNull(requiredType);
        return new ResolvableType(requiredType);
    }

    public Type getType() {
        return type;
    }

    /**
     * Return the underlying Java {@link Class} being managed, if available;
     * otherwise {@code null}.
     */
    @Nullable
    public Class<?> getRawClass() {
        if (this.type == this.resolved) {
            return this.resolved;
        }
        Type rawType = this.type;
        if (rawType instanceof ParameterizedType parameterizedType) {
            rawType = parameterizedType.getRawType();
        }
        return (rawType instanceof Class<?> rawClass ? rawClass : null);
    }

    /**
     * Return this type as a resolved {@code Class}, falling back to
     * {@link java.lang.Object} if no specific class can be resolved.
     *
     * @return the resolved {@link Class} or the {@code Object} fallback
     */
    public Class<?> toClass() {
        return this.resolved != null ? this.resolved : Object.class;
    }

    /**
     * Determine whether the given object is an instance of this {@code ResolvableType}.
     *
     * @param obj the object to check
     * @see #isAssignableFrom(Class)
     * @since 4.2
     */
    public <T> boolean isInstance(@Nullable T obj) {
        return (obj != null && isAssignableFrom(obj.getClass()));
    }

    public boolean isAssignableFrom(Class<?> other) {
        return isAssignableFrom(new ResolvableType(other));
    }

    public boolean isAssignableFrom(ResolvableType other) {
        if (this == NONE || other == NONE) {
            return false;
        }

        // Deal with array by delegating to the component type
        if (isArray()) {
            return (other.isArray() && getComponentType().isAssignableFrom(other.getComponentType()));
        }

        //todo implement
        boolean isAssignableFrom = false;
        if (!isAssignableFrom) {
            isAssignableFrom = this.getRawClass().isInstance(other.getRawClass());
            if (!isAssignableFrom) {
                isAssignableFrom = this.getRawClass().isAssignableFrom(other.getRawClass());
            }
        }

        return isAssignableFrom;
    }


    /**
     * Return {@code true} if this type resolves to a Class that represents an array.
     *
     * @see #getComponentType()
     */
    public boolean isArray() {
        if (this == NONE) {
            return false;
        }
        return ((this.type instanceof Class<?> clazz && clazz.isArray()) ||
                this.type instanceof GenericArrayType || resolveType().isArray());
    }

    /**
     * Resolve this type to a {@link java.lang.Class}, returning {@code null}
     * if the type cannot be resolved.
     *
     * @return the resolved {@link Class}, or {@code null} if not resolvable
     */
    @Nullable
    public Class<?> resolve() {
        return this.resolved;
    }


    @Nullable
    private Class<?> resolveClass() {
        if (this.type == EmptyType.INSTANCE) {
            return null;
        }
        if (this.type instanceof Class<?> clazz) {
            return clazz;
        }
        if (this.type instanceof GenericArrayType) {
            Class<?> resolvedComponent = getComponentType().resolve();
            return (resolvedComponent != null ? Array.newInstance(resolvedComponent, 0).getClass() : null);
        }
        return resolveType().resolve();
    }

    /**
     * Return the ResolvableType representing the component type of the array or
     * {@link #NONE} if this type does not represent an array.
     *
     * @see #isArray()
     */
    public ResolvableType getComponentType() {
        if (this == NONE) {
            return NONE;
        }
        if (this.componentType != null) {
            return this.componentType;
        }
        if (this.type instanceof Class<?> clazz) {
            Class<?> componentType = clazz.getComponentType();
            return forType(componentType, this.variableResolver);
        }
        if (this.type instanceof GenericArrayType genericArrayType) {
            return forType(genericArrayType.getGenericComponentType(), this.variableResolver);
        }
        return resolveType().getComponentType();
    }

    /**
     * Resolve this type by a single level, returning the resolved value or {@link #NONE}.
     * <p>Note: The returned {@link ResolvableType} should only be used as an intermediary
     * as it cannot be serialized.
     */
    ResolvableType resolveType() {
        if (this.type instanceof ParameterizedType parameterizedType) {
            return forType(parameterizedType.getRawType(), this.variableResolver);
        }
        if (this.type instanceof WildcardType wildcardType) {
            Type resolved = resolveBounds(wildcardType.getUpperBounds());
            if (resolved == null) {
                resolved = resolveBounds(wildcardType.getLowerBounds());
            }
            return forType(resolved, this.variableResolver);
        }
        if (this.type instanceof TypeVariable<?> variable) {
            // Try default variable resolution
            if (this.variableResolver != null) {
                ResolvableType resolved = this.variableResolver.resolveVariable(variable);
                if (resolved != null) {
                    return resolved;
                }
            }
            // Fallback to bounds
            return forType(resolveBounds(variable.getBounds()), this.variableResolver);
        }
        return NONE;
    }

    @Nullable
    private Type resolveBounds(Type[] bounds) {
        if (bounds.length == 0 || bounds[0] == Object.class) {
            return null;
        }
        return bounds[0];
    }

    static ResolvableType forType(@Nullable Type type, @Nullable VariableResolver variableResolver) {
        return forType(type, null, variableResolver);
    }

    /**
     * Return a {@link ResolvableType} for the specified {@link Type} backed by a given
     * {@link VariableResolver}.
     *
     * @param type             the source type or {@code null}
     * @param typeProvider     the type provider or {@code null}
     * @param variableResolver the variable resolver or {@code null}
     * @return a {@link ResolvableType} for the specified {@link Type} and {@link VariableResolver}
     */
    static ResolvableType forType(@Nullable Type type, @Nullable TypeProvider typeProvider, @Nullable VariableResolver variableResolver) {

        if (type == null && typeProvider != null) {
            type = typeProvider.getType();
        }
        if (type == null) {
            return NONE;
        }

        return new ResolvableType(type, typeProvider, variableResolver, (ResolvableType) null);
    }

    public boolean hasGenerics() {
        return (getGenerics().length > 0);
    }

    /**
     * Return a {@link ResolvableType} representing the direct supertype of this type.
     * <p>If no supertype is available this method returns {@link #NONE}.
     * <p>Note: The resulting {@link ResolvableType} instance may not be {@link Serializable}.
     */
    public ResolvableType getSuperType() {
        Class<?> resolved = resolve();
        if (resolved == null) {
            return NONE;
        }
        ResolvableType supType = this.getSuperType();
        if (supType == null) {
            final Type superclass = resolved.getGenericSuperclass();
            if (superclass == null) {
                supType = NONE;
            } else {
                supType = forType(superclass, this.variableResolver);
                this.superType = supType;
            }
        }
        return supType;
    }

    /**
     * Return a {@link ResolvableType} array representing the direct interfaces
     * implemented by this type. If this type does not implement any interfaces an
     * empty array is returned.
     * <p>Note: The resulting {@link ResolvableType} instances may not be {@link Serializable}.
     */
    public ResolvableType[] getInterfaces() {
        Class<?> resolved = resolve();
        if (resolved == null) {
            return new ResolvableType[0];
        }
        ResolvableType[] interfaces = this.interfaces;
        if (interfaces == null) {
            final Type[] genericInterfaces = resolved.getGenericInterfaces();
            interfaces = new ResolvableType[genericInterfaces.length];
            for (int i = 0; i < genericInterfaces.length; i++) {
                final Type genericInterface = genericInterfaces[i];
                interfaces[i] = forType(genericInterface, this.variableResolver);
            }
            this.interfaces = interfaces;
        }
        return interfaces;
    }

    /**
     * Return an array of {@link ResolvableType ResolvableTypes} representing the generic parameters of
     * this type. If no generics are available an empty array is returned. If you need to
     * access a specific generic consider using the {@link #getGeneric(int...)} method as
     * it allows access to nested generics and protects against
     * {@code IndexOutOfBoundsExceptions}.
     *
     * @return an array of {@link ResolvableType ResolvableTypes} representing the generic parameters
     */
    public ResolvableType[] getGenerics() {
        if (this == NONE) {
            return new ResolvableType[0];
        }
        ResolvableType[] generics = this.generics;
        if (generics == null) {
            if (this.type instanceof Class<?> clazz) {
                Type[] typeParams = clazz.getTypeParameters();
                generics = new ResolvableType[typeParams.length];
                for (int i = 0; i < generics.length; i++) {
                    generics[i] = forType(typeParams[i], this.variableResolver);
                }
            } else if (this.type instanceof ParameterizedType parameterizedType) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                generics = new ResolvableType[actualTypeArguments.length];
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    generics[i] = forType(actualTypeArguments[i], this.variableResolver);
                }
            } else {
                generics = resolveType().getGenerics();
            }
            this.generics = generics;
        }
        return generics;
    }

    @Nullable
    public ResolvableType resolveVariable(TypeVariable<?> variable) {
        if (this.type instanceof TypeVariable) {
            return resolveType().resolveVariable(variable);
        }
        if (this.type instanceof ParameterizedType parameterizedType) {
            Class<?> resolved = resolve();
            if (resolved == null) {
                return null;
            }
            TypeVariable<?>[] variables = resolved.getTypeParameters();
            for (int i = 0; i < variables.length; i++) {
                if (ObjectUtils.nullSafeEquals(variables[i].getName(), variable.getName())) {
                    Type actualType = parameterizedType.getActualTypeArguments()[i];
                    return forType(actualType, this.variableResolver);
                }
            }
            Type ownerType = parameterizedType.getOwnerType();
            if (ownerType != null) {
                return forType(ownerType, this.variableResolver).resolveVariable(variable);
            }
        }
        if (this.type instanceof WildcardType) {
            ResolvableType resolved = resolveType().resolveVariable(variable);
            if (resolved != null) {
                return resolved;
            }
        }
        if (this.variableResolver != null) {
            return this.variableResolver.resolveVariable(variable);
        }
        return null;
    }

    /**
     * Internal {@link Type} used to represent an empty value.
     */
    @SuppressWarnings("serial")
    static class EmptyType implements Type, Serializable {

        static final Type INSTANCE = new EmptyType();

        Object readResolve() {
            return INSTANCE;
        }
    }
}
