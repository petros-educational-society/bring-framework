package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.exception.BeanInstantiationException;
import com.petros.bringframework.beans.support.GenericBeanDefinition;
import com.petros.bringframework.core.AssertUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;

/**
 * @author "Maksym Oliinyk"
 */
public class SimpleInstantiationStrategy implements InstantiationStrategy {
    @Override
    public Object instantiate(GenericBeanDefinition gbd, String beanName) {
        final Executable resolvedConstructor = gbd.getResolvedConstructor();
        Constructor<?> noArgsConstructor = null;
        if (resolvedConstructor != null && resolvedConstructor instanceof Constructor<?> constructor) {
            noArgsConstructor = constructor;
        } else {
            final Class<?> beanClass = gbd.getBeanClass();
            if (beanClass.isInterface()) {
                throw new BeanInstantiationException(beanClass.getName(), "Specified class is an interface");
            }
            try {
                noArgsConstructor = beanClass.getDeclaredConstructor();
                gbd.setResolvedConstructor(noArgsConstructor);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return instantiateClass(noArgsConstructor);
    }

    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
        try {
            AssertUtils.notNull(ctor, "Constructor must not be null");
            ctor.setAccessible(true);


                int parameterCount = ctor.getParameterCount();
                AssertUtils.isTrue(args.length <= parameterCount, "Can't specify more arguments than constructor parameters");
                if (parameterCount == 0) {
                    return ctor.newInstance();
                }
                Class<?>[] parameterTypes = ctor.getParameterTypes();
                Object[] argsWithDefaultValues = new Object[args.length];
                for (int i = 0 ; i < args.length; i++) {
                    if (args[i] == null) {
                        Class<?> parameterType = parameterTypes[i];
                        argsWithDefaultValues[i] = (parameterType.isPrimitive() ? getDefaultValue(parameterType) : null);
                    }
                    else {
                        argsWithDefaultValues[i] = args[i];
                    }
                }
                return ctor.newInstance(argsWithDefaultValues);

        }
        catch (InstantiationException ex) {
            throw new BeanInstantiationException(ctor.getName(), "Is it an abstract class?", ex);
        }
        catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(ctor.getName(), "Is the constructor accessible?", ex);
        }
        catch (IllegalArgumentException ex) {
            throw new BeanInstantiationException(ctor.getName(), "Illegal arguments for constructor", ex);
        }
        catch (InvocationTargetException ex) {
            throw new BeanInstantiationException(ctor.getName(), "Constructor threw exception", ex.getTargetException());
        }
    }

    private static Object getDefaultValue(Class<?> type) {
        if (type.isPrimitive()) {
            if (type.equals(boolean.class)) return false;
            if (type.equals(byte.class)) return (byte) 0;
            if (type.equals(short.class)) return (short) 0;
            if (type.equals(char.class)) return '\0';
            if (type.equals(int.class)) return 0;
            if (type.equals(long.class)) return 0L;
            if (type.equals(float.class)) return 0.0f;
            if (type.equals(double.class)) return 0.0d;
        }
        return null;
    }

}
