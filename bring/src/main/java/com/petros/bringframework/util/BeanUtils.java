package com.petros.bringframework.util;

import com.petros.bringframework.beans.exception.BeanInstantiationException;
import com.petros.bringframework.core.AssertUtils;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static com.petros.bringframework.core.AssertUtils.notNull;

/**
 * @author "Maksym Oliinyk"
 * @author "Viktor Basanets"
 */
@Log4j2
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
     * non-accessible (that is, non-public) constructor with optional parameters and default values.
     *
     * @param ctor the constructor to instantiate
     * @param args the constructor arguments to apply (use {@code null} for an unspecified
     *             parameter and Java primitive types are supported)
     * @return the new instance
     * @throws BeanInstantiationException if the bean cannot be instantiated
     */
    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
        notNull(ctor, "Constructor must not be null");
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
            log.debug("Failed to instantiate bean: Is it an abstract class? {}", ex.getMessage(), ex);
            throw new BeanInstantiationException(ctor, "Is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            log.debug("Failed to instantiate bean: Is the constructor accessible? {}", ex.getMessage(), ex);
            throw new BeanInstantiationException(ctor, "Is the constructor accessible?", ex);
        } catch (IllegalArgumentException ex) {
            log.debug("Failed to instantiate bean: Illegal arguments for constructor {}", ex.getMessage(), ex);
            throw new BeanInstantiationException(ctor, "Illegal arguments for constructor", ex);
        } catch (InvocationTargetException ex) {
            log.debug("Failed to instantiate bean: Constructor threw exception {}", ex.getMessage(), ex.getTargetException());
            throw new BeanInstantiationException(ctor, "Constructor threw exception", ex.getTargetException());
        }
    }

    /**
     * Instantiate a class using its 'primary' constructor or its default constructor
     * (for regular Java classes, expecting a standard no-arg setup).
     * @param clazz the class to instantiate
     * @return the new instance
     * @throws BeanInstantiationException if the bean cannot be instantiated.
     * The cause may notably indicate a {@link NoSuchMethodException} if no
     * primary/default constructor was found, a {@link NoClassDefFoundError}
     * or other {@link LinkageError} in case of an unresolvable class definition
     * (e.g. due to a missing dependency at runtime), or an exception thrown
     * from the constructor invocation itself.
     */
    public static <T> T instantiateClass(Class<T> clazz) throws BeanInstantiationException {
        notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        try {
            return instantiateClass(clazz.getDeclaredConstructor());
        } catch (NoSuchMethodException ex) {
            log.debug("Failed to instantiate bean: No default constructor found {}", ex.getMessage(), ex);
            throw new BeanInstantiationException(clazz, "No default constructor found", ex);
        } catch (LinkageError err) {
            log.debug("Failed to instantiate bean: Unresolvable class definition {}", err.getMessage(), err);
            throw new BeanInstantiationException(clazz, "Unresolvable class definition", err);
        }
    }
}
