package com.petros.bringframework.util;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

public class ObjectUtils {
    private static final String ARRAY_START = "{";
    private static final String ARRAY_END = "}";
    private static final String EMPTY_ARRAY = ARRAY_START + ARRAY_END;
    private static final String NULL_STRING = "null";
    private static final String EMPTY_STRING = "";
    private static final String NON_EMPTY_ARRAY = ARRAY_START + "..." + ARRAY_END;
    private static final String COLLECTION = "[...]";
    private static final String MAP = NON_EMPTY_ARRAY;
    private static final int DEFAULT_TRUNCATION_THRESHOLD = 100;

    public static boolean nullSafeEquals(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            return arrayEquals(o1, o2);
        }
        return false;
    }

    private static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] objects1 && o2 instanceof Object[] objects2) {
            return Arrays.equals(objects1, objects2);
        }
        if (o1 instanceof boolean[] booleans1 && o2 instanceof boolean[] booleans2) {
            return Arrays.equals(booleans1, booleans2);
        }
        if (o1 instanceof byte[] bytes1 && o2 instanceof byte[] bytes2) {
            return Arrays.equals(bytes1, bytes2);
        }
        if (o1 instanceof char[] chars1 && o2 instanceof char[] chars2) {
            return Arrays.equals(chars1, chars2);
        }
        if (o1 instanceof double[] doubles1 && o2 instanceof double[] doubles2) {
            return Arrays.equals(doubles1, doubles2);
        }
        if (o1 instanceof float[] floats1 && o2 instanceof float[] floats2) {
            return Arrays.equals(floats1, floats2);
        }
        if (o1 instanceof int[] ints1 && o2 instanceof int[] ints2) {
            return Arrays.equals(ints1, ints2);
        }
        if (o1 instanceof long[] longs1 && o2 instanceof long[] longs2) {
            return Arrays.equals(longs1, longs2);
        }
        if (o1 instanceof short[] shorts1 && o2 instanceof short[] shorts2) {
            return Arrays.equals(shorts1, shorts2);
        }
        return false;
    }

    public static String nullSafeConciseToString(@Nullable Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof Optional<?> optional) {
            return (optional.isEmpty() ? "Optional.empty" :
                    "Optional[%s]".formatted(nullSafeConciseToString(optional.get())));
        }
        if (obj.getClass().isArray()) {
            return (Array.getLength(obj) == 0 ? EMPTY_ARRAY : NON_EMPTY_ARRAY);
        }
        if (obj instanceof Collection) {
            return COLLECTION;
        }
        if (obj instanceof Map) {
            return MAP;
        }
        if (obj instanceof Class<?> clazz) {
            return clazz.getName();
        }
        if (obj instanceof Charset charset) {
            return charset.name();
        }
        if (obj instanceof TimeZone timeZone) {
            return timeZone.getID();
        }
        if (obj instanceof ZoneId zoneId) {
            return zoneId.getId();
        }
        if (obj instanceof CharSequence charSequence) {
            return StringUtils.truncate(charSequence.toString(), DEFAULT_TRUNCATION_THRESHOLD);
        }
        Class<?> type = obj.getClass();
        if (isSimpleValueType(type)) {
            String str = obj.toString();
            if (str != null) {
                return StringUtils.truncate(str, DEFAULT_TRUNCATION_THRESHOLD);
            }
        }
        return type.getTypeName() + "@" + getIdentityHexString(obj);
    }

    public static String getIdentityHexString(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }

    private static boolean isSimpleValueType(Class<?> type) {
        return (Void.class != type && void.class != type &&
                (ClassUtils.isPrimitiveOrWrapper(type) ||
                        Enum.class.isAssignableFrom(type) ||
                        CharSequence.class.isAssignableFrom(type) ||
                        Number.class.isAssignableFrom(type) ||
                        Date.class.isAssignableFrom(type) ||
                        Temporal.class.isAssignableFrom(type) ||
                        ZoneId.class.isAssignableFrom(type) ||
                        TimeZone.class.isAssignableFrom(type) ||
                        File.class.isAssignableFrom(type) ||
                        Path.class.isAssignableFrom(type) ||
                        Charset.class.isAssignableFrom(type) ||
                        Currency.class.isAssignableFrom(type) ||
                        InetAddress.class.isAssignableFrom(type) ||
                        URI.class == type ||
                        URL.class == type ||
                        UUID.class == type ||
                        Locale.class == type ||
                        Pattern.class == type ||
                        Class.class == type));
    }

    /**
     * Determine whether the given array is empty:
     * i.e. {@code null} or of zero length.
     * @param array the array to check
     */
    public static boolean isEmpty(@Nullable Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(@Nullable Object[] array) {
        return !isEmpty(array);
    }

    /**
     * Return a String representation of the specified Object.
     * <p>Builds a String representation of the contents in case of an array.
     * Returns a {@code "null"} String if {@code obj} is {@code null}.
     * @param obj the object to build a String representation for
     * @return a String representation of {@code obj}
     * @see #nullSafeConciseToString(Object)
     */
    public static String nullSafeToString(@Nullable Object obj) {
        if (obj == null) {
            return NULL_STRING;
        }
        if (obj instanceof String string) {
            return string;
        }
        if (obj instanceof Object[] objects) {
            return nullSafeToString(objects);
        }
        if (obj instanceof boolean[] booleans) {
            return nullSafeToString(booleans);
        }
        if (obj instanceof byte[] bytes) {
            return nullSafeToString(bytes);
        }
        if (obj instanceof char[] chars) {
            return nullSafeToString(chars);
        }
        if (obj instanceof double[] doubles) {
            return nullSafeToString(doubles);
        }
        if (obj instanceof float[] floats) {
            return nullSafeToString(floats);
        }
        if (obj instanceof int[] ints) {
            return nullSafeToString(ints);
        }
        if (obj instanceof long[] longs) {
            return nullSafeToString(longs);
        }
        if (obj instanceof short[] shorts) {
            return nullSafeToString(shorts);
        }
        String str = obj.toString();
        return (str != null ? str : EMPTY_STRING);
    }

}
