package com.petros.bringframework.core;

import com.petros.bringframework.util.ClassUtils;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;

import static java.util.Objects.isNull;

/**
 * Provides method(s) to support various naming and other conventions used throughout the framework.
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class Conventions {

    /**
     * Suffix added to names when using arrays.
     */
    private static final String PLURAL_SUFFIX = "List";

    /**
     * Determine the conventional variable name for the supplied {@code Object}based on its concrete type.
     * @param value the value to generate a variable name for
     * @return the generated variable name
     */
    public static String getVariableName(Object value) {
        AssertUtils.notNull(value, "Value must not be null");
        Class<?> valueClass;
        boolean pluralize = false;

        if (value.getClass().isArray()) {
            valueClass = value.getClass().getComponentType();
            pluralize = true;
        }
        else if (value instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                throw new IllegalArgumentException(
                        "Cannot generate variable name for an empty Collection");
            }
            valueClass = getClassForValue(
                    peekAhead(collection)
            );
            pluralize = true;
        }
        else {
            valueClass = getClassForValue(value);
        }

        var name = ClassUtils.getShortNameAsProperty(valueClass);
        return pluralize ? pluralize(name) : name;
    }

    /**
     * Retrieve the {@code Class} of an element in the {@code Collection}.
     * The exact element for which the {@code Class} is retrieved will depend
     * on the concrete {@code Collection} implementation.
     */
    private static <E> E peekAhead(Collection<E> collection) {
        var it = collection.iterator();
        if (!it.hasNext()) {
            throw new IllegalStateException("Unable to peek ahead in non-empty collection - no element found");
        }
        E value = it.next();
        if (isNull(value)) {
            throw new IllegalStateException("Unable to peek ahead in non-empty collection - only null element found");
        }
        return value;
    }

    /**
     * Determine the class to use for naming a variable containing the given value.
     * @param value the value to check
     * @return the class to use for naming a variable
     */
    private static Class<?> getClassForValue(Object value) {
        Class<?> valueClass = value.getClass();
        if (Proxy.isProxyClass(valueClass)) {
            Class<?>[] interfaces = valueClass.getInterfaces();
            for (Class<?> ifc : interfaces) {
                if (!ClassUtils.isJavaLanguageInterface(ifc)) {
                    return ifc;
                }
            }
        } else if (valueClass.getName().lastIndexOf('$') != -1 && valueClass.getDeclaringClass() == null) {
            valueClass = valueClass.getSuperclass();
        }
        return valueClass;
    }

    /**
     * Pluralize the given name.
     */
    private static String pluralize(String name) {
        return name + PLURAL_SUFFIX;
    }

}
