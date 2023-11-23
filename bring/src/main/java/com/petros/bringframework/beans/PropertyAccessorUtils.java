package com.petros.bringframework.beans;

/**
 * Utility methods for classes that perform bean property access
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 *
 */

public abstract class PropertyAccessorUtils {
    public final static String PROPERTY_KEY_PREFIX = "[";

    public final static char PROPERTY_KEY_PREFIX_CHAR = '[';

    public final static String PROPERTY_KEY_SUFFIX = "]";

    public final static char PROPERTY_KEY_SUFFIX_CHAR = ']';

    /**
     * Determine whether the given registered path matches the given property path,
     * either indicating the property itself or an indexed element of the property.
     * @param propertyPath the property path (typically without index)
     * @param registeredPath the registered path (potentially with index)
     * @return whether the paths match
     */
    public static boolean matchesProperty(String registeredPath, String propertyPath) {
        if (!registeredPath.startsWith(propertyPath)) {
            return false;
        }
        if (registeredPath.length() == propertyPath.length()) {
            return true;
        }
        if (registeredPath.charAt(propertyPath.length()) != PROPERTY_KEY_PREFIX_CHAR) {
            return false;
        }
        return registeredPath.indexOf(PROPERTY_KEY_SUFFIX_CHAR, propertyPath.length() + 1) ==
                registeredPath.length() - 1;
    }
}
