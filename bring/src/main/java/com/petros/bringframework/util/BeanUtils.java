package com.petros.bringframework.util;

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
}
