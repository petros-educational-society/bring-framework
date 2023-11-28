package com.petros.bringframework.beans.converter;

import com.petros.bringframework.beans.DefaultPropertyEditorRegistry;
import com.petros.bringframework.core.CollectionFactory;
import com.petros.bringframework.core.type.convert.ConversionFailedException;
import com.petros.bringframework.core.type.convert.TypeDescriptor;
import com.petros.bringframework.util.*;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static com.petros.bringframework.beans.PropertyAccessorUtils.PROPERTY_KEY_PREFIX;
import static com.petros.bringframework.beans.PropertyAccessorUtils.PROPERTY_KEY_SUFFIX;
import static java.util.Objects.nonNull;

/**
 * Internal helper class for converting property values to target types.
 * @author Viktor Basanets
 * @Project: bring-framework
 */

@Log4j2
public class TypeConverterDelegate {

    private final DefaultPropertyEditorRegistry propertyEditorRegistry;

    @Nullable
    private final Object targetObject;

    public TypeConverterDelegate(DefaultPropertyEditorRegistry propertyEditorRegistry) {
        this(propertyEditorRegistry, null);
    }

    public TypeConverterDelegate(DefaultPropertyEditorRegistry propertyEditorRegistry, @Nullable Object targetObject) {
        this.propertyEditorRegistry = propertyEditorRegistry;
        this.targetObject = targetObject;
    }

    /**
     * Convert the value to the required type for the specified property.
     * @param propertyName name of the property
     * @param oldValue the previous value, if available (may be {@code null})
     * @param newValue the proposed new value
     * @param requiredType the type we must convert to
     * (or {@code null} if not known, for example in case of a collection element)
     * @return the new value, possibly the result of type conversion
     * @throws IllegalArgumentException if type conversion failed
     */
    @Nullable
    public <T> T convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue,
                                    Object newValue, @Nullable Class<T> requiredType) throws IllegalArgumentException {

        return convertIfNecessary(propertyName, oldValue, newValue, requiredType, TypeDescriptor.valueOf(requiredType));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, @Nullable Object newValue,
                                    @Nullable Class<T> requiredType, @Nullable TypeDescriptor typeDescriptor)
            throws IllegalArgumentException {

        ConversionFailedException conversionAttemptEx = null;
        var editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);
        var conversionService = this.propertyEditorRegistry.getConversionService();
        if (editor == null && conversionService != null && newValue != null && typeDescriptor != null) {
            TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
            if (conversionService.canConvert(sourceTypeDesc, typeDescriptor)) {
                try {
                    return (T) conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);
                }
                catch (ConversionFailedException ex) {
                    conversionAttemptEx = ex;
                }
            }
        }

        var convertedValue = newValue;
        if (editor != null || (requiredType != null && !ClassUtils.isAssignableValue(requiredType, convertedValue))) {
            if (typeDescriptor != null && requiredType != null && Collection.class.isAssignableFrom(requiredType) &&
                    convertedValue instanceof String text) {
                var elementTypeDesc = typeDescriptor.getElementTypeDescriptor();
                if (elementTypeDesc != null) {
                    Class<?> elementType = elementTypeDesc.getType();
                    if (Class.class == elementType || Enum.class.isAssignableFrom(elementType)) {
                        convertedValue = StringUtils.commaDelimitedListToStringArray(text);
                    }
                }
            }
            if (editor == null) {
                editor = findDefaultEditor(requiredType);
            }
            convertedValue = doConvertValue(oldValue, convertedValue, requiredType, editor);
        }

        boolean standardConversion = false;
        if (requiredType != null) {
            if (convertedValue != null) {
                if (Object.class == requiredType) {
                    return (T) convertedValue;
                } else if (requiredType.isArray()) {
                    if (convertedValue instanceof String text && Enum.class.isAssignableFrom(requiredType.getComponentType())) {
                        convertedValue = StringUtils.commaDelimitedListToStringArray(text);
                    }
                    return (T) convertToTypedArray(convertedValue, propertyName, requiredType.getComponentType());
                } else if (convertedValue instanceof Collection<?> coll) {
                    convertedValue = convertToTypedCollection(coll, propertyName, requiredType, typeDescriptor);
                    standardConversion = true;
                } else if (convertedValue instanceof Map<?, ?> map) {
                    convertedValue = convertToTypedMap(map, propertyName, requiredType, typeDescriptor);
                    standardConversion = true;
                }
                if (convertedValue.getClass().isArray() && Array.getLength(convertedValue) == 1) {
                    convertedValue = Array.get(convertedValue, 0);
                    standardConversion = true;
                }
                if (String.class == requiredType && ClassUtils.isPrimitiveOrWrapper(convertedValue.getClass())) {
                    return (T) convertedValue.toString();
                }
                else if (convertedValue instanceof String text && !requiredType.isInstance(convertedValue)) {
                    if (conversionAttemptEx == null && !requiredType.isInterface() && !requiredType.isEnum()) {
                        try {
                            Constructor<T> strCtor = requiredType.getConstructor(String.class);
                            return BeanUtils.instantiateClass(strCtor, convertedValue);
                        }
                        catch (NoSuchMethodException ex) {
                            if (log.isTraceEnabled()) {
                                log.trace("No String constructor found on type [" + requiredType.getName() + "]", ex);
                            }
                        }
                        catch (Exception ex) {
                            if (log.isDebugEnabled()) {
                                log.debug("Construction via String failed for type [" + requiredType.getName() + "]", ex);
                            }
                        }
                    }
                    var trimmedValue = text.trim();
                    if (requiredType.isEnum() && trimmedValue.isEmpty()) {
                        return null;
                    }
                    convertedValue = attemptToConvertStringToEnum(requiredType, trimmedValue, convertedValue);
                    standardConversion = true;
                }
                else if (convertedValue instanceof Number num && Number.class.isAssignableFrom(requiredType)) {
                    convertedValue = NumberUtils.convertNumberToTargetClass(num, (Class<Number>) requiredType);
                    standardConversion = true;
                }
            }
            else {
                if (requiredType == Optional.class) {
                    convertedValue = Optional.empty();
                }
            }

            if (!ClassUtils.isAssignableValue(requiredType, convertedValue)) {
                if (conversionAttemptEx != null) {
                    throw conversionAttemptEx;
                } else if (conversionService != null && typeDescriptor != null) {
                    var sourceTypeDesc = TypeDescriptor.forObject(newValue);
                    if (conversionService.canConvert(sourceTypeDesc, typeDescriptor)) {
                        return (T) conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);
                    }
                }

                var msg = new StringBuilder();
                msg.append("Cannot convert value of type '").append(ClassUtils.getDescriptiveType(newValue));
                msg.append("' to required type '").append(ClassUtils.getQualifiedName(requiredType)).append('\'');
                if (propertyName != null) {
                    msg.append(" for property '").append(propertyName).append('\'');
                }
                if (editor != null) {
                    msg.append(": PropertyEditor [").append(editor.getClass().getName()).append(
                            "] returned inappropriate value of type '").append(
                            ClassUtils.getDescriptiveType(convertedValue)).append('\'');
                    throw new IllegalArgumentException(msg.toString());
                } else {
                    msg.append(": no matching editors or conversion strategy found");
                    throw new IllegalStateException(msg.toString());
                }
            }
        }

        if (conversionAttemptEx != null) {
            if (editor == null && !standardConversion && requiredType != null && Object.class != requiredType) {
                throw conversionAttemptEx;
            }
            log.debug("Original ConversionService attempt failed - ignored since " +
                    "PropertyEditor based conversion eventually succeeded", conversionAttemptEx);
        }

        return (T) convertedValue;
    }

    private Object attemptToConvertStringToEnum(Class<?> requiredType, String trimmedValue, Object currentConvertedValue) {
        Object convertedValue = currentConvertedValue;

        if (Enum.class == requiredType && this.targetObject != null) {
            int index = trimmedValue.lastIndexOf('.');
            if (index > - 1) {
                String enumType = trimmedValue.substring(0, index);
                String fieldName = trimmedValue.substring(index + 1);
                ClassLoader cl = this.targetObject.getClass().getClassLoader();
                try {
                    Class<?> enumValueType = ClassUtils.forName(enumType, cl);
                    Field enumField = enumValueType.getField(fieldName);
                    convertedValue = enumField.get(null);
                }
                catch (ClassNotFoundException ex) {
                    if (log.isTraceEnabled()) {
                        log.trace("Enum class [" + enumType + "] cannot be loaded", ex);
                    }
                }
                catch (Throwable ex) {
                    if (log.isTraceEnabled()) {
                        log.trace("Field [" + fieldName + "] isn't an enum value for type [" + enumType + "]", ex);
                    }
                }
            }
        }

        if (convertedValue == currentConvertedValue) {
            try {
                Field enumField = requiredType.getField(trimmedValue);
                ReflectionUtils.makeAccessible(enumField);
                convertedValue = enumField.get(null);
            }
            catch (Throwable ex) {
                if (log.isTraceEnabled()) {
                    log.trace("Field [" + convertedValue + "] isn't an enum value", ex);
                }
            }
        }

        return convertedValue;
    }



    @SuppressWarnings("unchecked")
    private Collection<?> convertToTypedCollection(Collection<?> original, @Nullable String propertyName,
                                                   Class<?> requiredType, @Nullable TypeDescriptor typeDescriptor) {

        if (!Collection.class.isAssignableFrom(requiredType)) {
            return original;
        }

        boolean approximable = CollectionFactory.isApproximableCollectionType(requiredType);
        if (!approximable && !canCreateCopy(requiredType)) {
            if (log.isDebugEnabled()) {
                log.debug("Custom Collection type [" + original.getClass().getName() +
                        "] does not allow for creating a copy - injecting original Collection as-is");
            }
            return original;
        }

        boolean originalAllowed = requiredType.isInstance(original);
        TypeDescriptor elementType = (typeDescriptor != null ? typeDescriptor.getElementTypeDescriptor() : null);
        if (elementType == null && originalAllowed &&
                !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }

        Iterator<?> it;
        try {
            it = original.iterator();
        }
        catch (Throwable ex) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot access Collection of type [" + original.getClass().getName() +
                        "] - injecting original Collection as-is: " + ex);
            }
            return original;
        }

        Collection<Object> convertedCopy;
        try {
            if (approximable) {
                convertedCopy = CollectionFactory.createApproximateCollection(original, original.size());
            }
            else {
                convertedCopy = (Collection<Object>) ReflectionUtils.accessibleConstructor(requiredType).newInstance();
            }
        }
        catch (Throwable ex) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot create copy of Collection type [" + original.getClass().getName() +
                        "] - injecting original Collection as-is: " + ex);
            }
            return original;
        }

        for (int i = 0; it.hasNext(); i++) {
            Object element = it.next();
            String indexedPropertyName = buildIndexedPropertyName(propertyName, i);
            Object convertedElement = convertIfNecessary(indexedPropertyName, null, element,
                    (elementType != null ? elementType.getType() : null) , elementType);
            try {
                convertedCopy.add(convertedElement);
            }
            catch (Throwable ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Collection type [" + original.getClass().getName() +
                            "] seems to be read-only - injecting original Collection as-is: " + ex);
                }
                return original;
            }
            originalAllowed = originalAllowed && (element == convertedElement);
        }
        return (originalAllowed ? original : convertedCopy);
    }

    private boolean canCreateCopy(Class<?> requiredType) {
        return (!requiredType.isInterface() && !Modifier.isAbstract(requiredType.getModifiers()) &&
                Modifier.isPublic(requiredType.getModifiers()) && ClassUtils.hasConstructor(requiredType));
    }

    @SuppressWarnings("unchecked")
    private Map<?, ?> convertToTypedMap(Map<?, ?> original, @Nullable String propertyName,
                                        Class<?> requiredType, @Nullable TypeDescriptor typeDescriptor) {

        if (!Map.class.isAssignableFrom(requiredType)) {
            return original;
        }

        boolean approximable = CollectionFactory.isApproximableMapType(requiredType);
        if (!approximable && !canCreateCopy(requiredType)) {
            if (log.isDebugEnabled()) {
                log.debug("Custom Map type [" + original.getClass().getName() +
                        "] does not allow for creating a copy - injecting original Map as-is");
            }
            return original;
        }

        boolean originalAllowed = requiredType.isInstance(original);
        TypeDescriptor keyType = null;
        if (typeDescriptor != null) {
            keyType = typeDescriptor.getMapKeyTypeDescriptor();
        }
        TypeDescriptor valueType = null;
        if (typeDescriptor != null) {
            valueType = typeDescriptor.getMapValueTypeDescriptor();
        }
        if (keyType == null && valueType == null && originalAllowed &&
                !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }

        Iterator<?> it;
        try {
            it = original.entrySet().iterator();
        }
        catch (Throwable ex) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot access Map of type [" + original.getClass().getName() +
                        "] - injecting original Map as-is: " + ex);
            }
            return original;
        }

        Map<Object, Object> convertedCopy;
        try {
            if (approximable) {
                convertedCopy = CollectionFactory.createApproximateMap(original, original.size());
            }
            else {
                convertedCopy = (Map<Object, Object>)
                        ReflectionUtils.accessibleConstructor(requiredType).newInstance();
            }
        }
        catch (Throwable ex) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot create copy of Map type [" + original.getClass().getName() +
                        "] - injecting original Map as-is: " + ex);
            }
            return original;
        }

        while (it.hasNext()) {
            var entry = (Map.Entry<?, ?>) it.next();
            var key = entry.getKey();
            var value = entry.getValue();
            var keyedPropertyName = buildKeyedPropertyName(propertyName, key);
            var convertedKey = convertIfNecessary(keyedPropertyName, null, key,
                    nonNull(keyType) ? keyType.getType() : null, keyType);
            var convertedValue = convertIfNecessary(keyedPropertyName, null, value,
                    nonNull(valueType) ? valueType.getType() : null, valueType);
            try {
                convertedCopy.put(convertedKey, convertedValue);
            }
            catch (Throwable ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Map type [" + original.getClass().getName() +
                            "] seems to be read-only - injecting original Map as-is: " + ex);
                }
                return original;
            }
            originalAllowed = originalAllowed && (key == convertedKey) && (value == convertedValue);
        }
        return originalAllowed ? original : convertedCopy;
    }

    private Object convertToTypedArray(Object input, @Nullable String propertyName, Class<?> componentType) {
        if (input instanceof Collection<?> coll) {
            var result = Array.newInstance(componentType, coll.size());
            int i = 0;
            for (Iterator<?> it = coll.iterator(); it.hasNext(); i++) {
                var value = convertIfNecessary(buildIndexedPropertyName(propertyName, i), null, it.next(), componentType);
                Array.set(result, i, value);
            }
            return result;
        }
        else if (input.getClass().isArray()) {
            if (componentType.equals(input.getClass().getComponentType()) && !this.propertyEditorRegistry.hasCustomEditorForElement(componentType, propertyName)) {
                return input;
            }
            int arrayLength = Array.getLength(input);
            var result = Array.newInstance(componentType, arrayLength);
            for (int i = 0; i < arrayLength; i++) {
                var value = convertIfNecessary(buildIndexedPropertyName(propertyName, i), null, Array.get(input, i), componentType);
                Array.set(result, i, value);
            }
            return result;
        }
        else {
            var result = Array.newInstance(componentType, 1);
            var value = convertIfNecessary(buildIndexedPropertyName(propertyName, 0), null, input, componentType);
            Array.set(result, 0, value);
            return result;
        }
    }

    @Nullable
    private String buildIndexedPropertyName(@Nullable String propertyName, int index) {
        String name = null;
        if (nonNull(propertyName)) {
            name = propertyName + PROPERTY_KEY_PREFIX + index + PROPERTY_KEY_SUFFIX;
        }
        return name;
    }

    @Nullable
    private String buildKeyedPropertyName(@Nullable String propertyName, Object key) {
        String name = null;
        if (nonNull(propertyName)) {
            name = propertyName + PROPERTY_KEY_PREFIX + key + PROPERTY_KEY_SUFFIX;
        }
        return name;
    }


    /**
     * Convert the value to the required type (if necessary from a String),
     * using the given property editor.
     * @param oldValue the previous value, if available (may be {@code null})
     * @param newValue the proposed new value
     * @param requiredType the type we must convert to
     * (or {@code null} if not known, for example in case of a collection element)
     * @param editor the PropertyEditor to use
     * @return the new value, possibly the result of type conversion
     * @throws IllegalArgumentException if type conversion failed
     */
    @Nullable
    private Object doConvertValue(@Nullable Object oldValue, @Nullable Object newValue,
                                  @Nullable Class<?> requiredType, @Nullable PropertyEditor editor) {

        var convertedValue = newValue;
        if (editor != null && !(convertedValue instanceof String)) {
            try {
                editor.setValue(convertedValue);
                var newConvertedValue = editor.getValue();
                if (newConvertedValue != convertedValue) {
                    convertedValue = newConvertedValue;
                    editor = null;
                }
            }
            catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
                }
            }
        }

        var returnValue = convertedValue;
        if (requiredType != null && !requiredType.isArray() && convertedValue instanceof String[] array) {
            if (log.isTraceEnabled()) {
                log.trace("Converting String array to comma-delimited String [" + convertedValue + "]");
            }
            convertedValue = StringUtils.arrayToCommaDelimitedString(array);
        }

        if (convertedValue instanceof String newTextValue) {
            if (editor != null) {
                if (log.isTraceEnabled()) {
                    log.trace("Converting String to [" + requiredType + "] using property editor [" + editor + "]");
                }
                return doConvertTextValue(oldValue, newTextValue, editor);
            }
            if (String.class == requiredType) {
                returnValue = convertedValue;
            }
        }
        return returnValue;
    }

    /**
     * Convert the given text value using the given property editor.
     * @param oldValue the previous value, if available (may be {@code null})
     * @param newTextValue the proposed text value
     * @param editor the PropertyEditor to use
     * @return the converted value
     */
    private Object doConvertTextValue(@Nullable Object oldValue, String newTextValue, PropertyEditor editor) {
        try {
            editor.setValue(oldValue);
        }
        catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
            }
        }
        editor.setAsText(newTextValue);
        return editor.getValue();
    }

    /**
     * Find a default editor for the given type.
     * @param requiredType the type to find an editor for
     * @return the corresponding editor, or {@code null} if none
     */
    @Nullable
    private PropertyEditor findDefaultEditor(@Nullable Class<?> requiredType) {
        PropertyEditor editor = null;
        if (requiredType != null) {
            editor = this.propertyEditorRegistry.getDefaultEditor(requiredType);
        }
        return editor;
    }

}
