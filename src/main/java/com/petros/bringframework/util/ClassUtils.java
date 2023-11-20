package com.petros.bringframework.util;

import com.petros.bringframework.core.AssertUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;

import static com.petros.bringframework.core.AssertUtils.notBlank;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.ClassUtils.isPrimitiveWrapper;

public abstract class ClassUtils {

    private static final char PACKAGE_SEPARATOR = '.';
    public static final String CGLIB_CLASS_SEPARATOR = "$$";
    private static final char NESTED_CLASS_SEPARATOR = '$';

    private static final char PATH_SEPARATOR = '/';


    /**
     * Map with primitive wrapper type as key and corresponding primitive
     * type as value, for example: Integer.class -> int.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(9);
    /**
     * Map with primitive type as key and corresponding wrapper
     * type as value, for example: int.class -> Integer.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(9);

    {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);
        primitiveWrapperTypeMap.put(Void.class, void.class);

        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        requireNonNull(lhsType, "Left-hand side type must not be null");
        requireNonNull(rhsType, "Right-hand side type must not be null");

        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }

        if (lhsType.isPrimitive()) {
            var resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
            return (lhsType == resolvedPrimitive);
        }

        var resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
        return (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper));
    }

    public static String getShortName(String className) {
        notBlank(className, "Class name must not be empty");

        int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        int nameEndIndex = className.indexOf(CGLIB_CLASS_SEPARATOR);
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }

        return className.substring(lastDotIndex + 1, nameEndIndex)
                .replace(NESTED_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ignored) {}

        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ignored) {}
            }
        }
        return cl;
    }

    public static String classPackageAsResourcePath(@Nullable Class<?> clazz) {
        if (clazz == null) {
            return "";
        }

        var className = clazz.getName();
        int packageEndIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        if (packageEndIndex == -1) {
            return "";
        }

        return className.substring(0, packageEndIndex)
                .replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
    }

    public static String collectionToCommaDelimitedString(Collection<?> coll) {
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = coll.iterator();

        while(it.hasNext()) {
            sb.append(it.next()).append("");
            if (it.hasNext()) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    /**
     * Return a descriptive name for the given object's type: usually simply
     * the class name, but component type class name + "[]" for arrays,
     * and an appended list of implemented interfaces for JDK proxies.
     * @param value the value to introspect
     * @return the qualified name of the class
     */
    @Nullable
    public static String getDescriptiveType(@Nullable Object value) {
        if (value == null) {
            return null;
        }

        var clazz = value.getClass();
        if (Proxy.isProxyClass(clazz)) {
            var prefix = clazz.getName() + " implementing ";
            var result = new StringJoiner(",", prefix, "");
            for (var inter : clazz.getInterfaces()) {
                result.add(inter.getName());
            }
            return result.toString();
        }

        return clazz.getTypeName();
    }

    /**
     * Return the qualified name of the given class: usually simply
     * the class name, but component type class name + "[]" for arrays.
     * @param clazz the class
     * @return the qualified name of the class
     */
    public static String getQualifiedName(Class<?> clazz) {
        AssertUtils.notNull(clazz, "Class must not be null");
        return clazz.getTypeName();
    }

    /**
     * Check if the given class represents a primitive (i.e. boolean, byte,
     * char, short, int, long, float, or double), {@code void}, or a wrapper for
     * those types (i.e. Boolean, Byte, Character, Short, Integer, Long, Float,
     * Double, or Void).
     * @param clazz the class to check
     * @return {@code true} if the given class represents a primitive, void, or
     * a wrapper class
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        AssertUtils.notNull(clazz, "Class must not be null");
        return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

    /**
     * Determine if the given type is assignable from the given value,
     * assuming setting by reflection. Considers primitive wrapper classes
     * as assignable to the corresponding primitive types.
     * @param type the target type
     * @param value the value that should be assigned to the type
     * @return if the type is assignable from the value
     */
    public static boolean isAssignableValue(Class<?> type, @Nullable Object value) {
        AssertUtils.notNull(type, "Type must not be null");
        if (nonNull(value)) {
            return isAssignableValue(type, value.getClass());
        }
        return !type.isPrimitive();
    }
}