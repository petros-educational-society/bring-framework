package com.petros.bringframework.core;

import javax.annotation.Nullable;

import static java.util.Objects.isNull;

/**
 * @author "Maksym Oliinyk"
 */
public abstract class AssertUtils {

    /**
     * Assert that an array contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * <pre class="code">Assert.notEmpty(array, "The array must contain elements");</pre>
     *
     * @param array   the array to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object array is {@code null} or contains no elements
     */
    public static void notEmpty(@Nullable Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an object is not {@code null}.
     * <pre class="code">AssertUtils.notNull(clazz, "The class must not be null");</pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static void notNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert a boolean expression, throwing an {@code IllegalArgumentException}
     * if the expression evaluates to {@code false}.
     * <pre class="code">Assert.isTrue(i &gt; 0, "The value must be greater than zero");</pre>
     *
     * @param expression a boolean expression
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalArgumentException if {@code expression} is {@code false}
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notBlank(String str, String msg) {
        if (isBlank(str)) {
            throw new NullPointerException(msg);
        }
    }

    public static void hasText(@Nullable String str, String message) {
        if (!(str != null && !str.isEmpty() && containsText(str))) {
            throw new IllegalArgumentException(message);
        }
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean notBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isBlank(String str) {
        return isNull(str) || str.isBlank();
    }

    public static String uncapitalizeAsProperty(String str) {
        if (!notBlank(str) || (str.length() > 1 && Character.isUpperCase(str.charAt(0)) &&
                Character.isUpperCase(str.charAt(1)))) {
            return str;
        }
        return changeFirstCharacterCase(str, false);
    }

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (!notBlank(str)) {
            return str;
        }

        char baseChar = str.charAt(0);
        char updatedChar;
        if (capitalize) {
            updatedChar = Character.toUpperCase(baseChar);
        }
        else {
            updatedChar = Character.toLowerCase(baseChar);
        }
        if (baseChar == updatedChar) {
            return str;
        }

        char[] chars = str.toCharArray();
        chars[0] = updatedChar;
        return new String(chars);
    }

    public static void state(boolean ex, String msg) {
        if (!ex) {
            throw new IllegalStateException(msg);
        }
    }
}
