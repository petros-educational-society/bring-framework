package com.petros.bringframework.util;

import com.petros.bringframework.core.AssertUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

public abstract class NumberUtils {

    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);

    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    public static final Set<Class<?>> STANDARD_NUMBER_TYPES = Set.of(Byte.class, Short.class, Integer.class, Long.class,
            BigInteger.class, Float.class, Double.class, BigDecimal.class);

    /**
     * Convert the given number into an instance of the given target class.
     *
     * @param number      the number to convert
     * @param targetClass the target class to convert to
     * @return the converted number
     * @throws IllegalArgumentException if the target class is not supported
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T convertNumberToTargetClass(Number number, Class<T> targetClass)
            throws IllegalArgumentException {

        AssertUtils.notNull(number, "Number must not be null");
        AssertUtils.notNull(targetClass, "Target class must not be null");

        if (targetClass.isInstance(number)) {
            return (T) number;
        }

        if (Byte.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) Byte.valueOf(number.byteValue());
        }

        if (Short.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) Short.valueOf(number.shortValue());
        }

        if (Integer.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) Integer.valueOf(number.intValue());
        }

        if (Long.class == targetClass) {
            long value = checkedLongValue(number, targetClass);
            return (T) Long.valueOf(value);
        }

        if (BigInteger.class == targetClass) {
            return (T) (number instanceof BigDecimal bigDecimal ?
                    bigDecimal.toBigInteger() : BigInteger.valueOf(number.longValue()));
        }

        if (Float.class == targetClass) {
            return (T) Float.valueOf(number.floatValue());
        }

        if (Double.class == targetClass) {
            return (T) Double.valueOf(number.doubleValue());
        }

        if (BigDecimal.class == targetClass) {
            return (T) new BigDecimal(number.toString());
        }

        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" +
                number.getClass().getName() + "] to unsupported target class [" + targetClass.getName() + "]");
    }

    /**
     * Check for a {@code BigInteger}/{@code BigDecimal} long overflow
     * before returning the given number as a long value.
     * @param number the number to convert
     * @param targetClass the target class to convert to
     * @return the long value, if convertible without overflow
     * @throws IllegalArgumentException if there is an overflow
     */
    private static long checkedLongValue(Number number, Class<? extends Number> targetClass) {
        BigInteger bigInt = null;
        if (number instanceof BigInteger bigInteger) {
            bigInt = bigInteger;
        } else if (number instanceof BigDecimal bigDecimal) {
            bigInt = bigDecimal.toBigInteger();
        }

        if (bigInt != null && (bigInt.compareTo(LONG_MIN) < 0 || bigInt.compareTo(LONG_MAX) > 0)) {
            raiseOverflowException(number, targetClass);
        }

        return number.longValue();
    }

    /**
     * Raise an <em>overflow</em> exception for the given number and target class.
     * @param number the number we tried to convert
     * @param targetClass the target class we tried to convert to
     * @throws IllegalArgumentException if there is an overflow
     */
    private static void raiseOverflowException(Number number, Class<?> targetClass) {
        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" +
                number.getClass().getName() + "] to target class [" + targetClass.getName() + "]: overflow");
    }
}
