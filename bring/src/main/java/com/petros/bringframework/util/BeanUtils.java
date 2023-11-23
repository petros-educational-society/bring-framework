package com.petros.bringframework.util;

import com.petros.bringframework.beans.exception.BeanInstantiationException;
import com.petros.bringframework.core.AssertUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author "Maksym Oliinyk"
 */
public abstract class BeanUtils {
    public static final Map<Class<?>, Object> DEFAULT_TYPE_VALUES = Map.of(
            boolean.class, false,
            byte.class, (byte) 0,
            short.class, (short) 0,
            int.class, 0,
            long.class, 0L,
            float.class, 0F,
            double.class, 0D,
            char.class, '\0');

    /**
     * Convenience method to instantiate a class using the given constructor.
     * <p>Note that this method tries to set the constructor accessible if given a
     * non-accessible (that is, non-public) constructor, and supports Kotlin classes
     * with optional parameters and default values.
     *
     * @param ctor the constructor to instantiate
     * @param args the constructor arguments to apply (use {@code null} for an unspecified
     *             parameter, Kotlin optional parameters and Java primitive types are supported)
     * @return the new instance
     * @throws BeanInstantiationException if the bean cannot be instantiated
     */
    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
        AssertUtils.notNull(ctor, "Constructor must not be null");
        try {
            int parameterCount = ctor.getParameterCount();
            AssertUtils.isTrue(args.length <= parameterCount, "Can't specify more arguments than constructor parameters");
            if (parameterCount == 0) {
                return ctor.newInstance();
            }
            Class<?>[] parameterTypes = ctor.getParameterTypes();
            Object[] argsWithDefaultValues = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null) {
                    Class<?> parameterType = parameterTypes[i];
                    argsWithDefaultValues[i] = (parameterType.isPrimitive() ? DEFAULT_TYPE_VALUES.get(parameterType) : null);
                } else {
                    argsWithDefaultValues[i] = args[i];
                }
            }
            return ctor.newInstance(argsWithDefaultValues);
        } catch (InstantiationException ex) {
            throw new BeanInstantiationException(ctor, "Is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(ctor, "Is the constructor accessible?", ex);
        } catch (IllegalArgumentException ex) {
            throw new BeanInstantiationException(ctor, "Illegal arguments for constructor", ex);
        } catch (InvocationTargetException ex) {
            throw new BeanInstantiationException(ctor, "Constructor threw exception", ex.getTargetException());
        }
    }
}
