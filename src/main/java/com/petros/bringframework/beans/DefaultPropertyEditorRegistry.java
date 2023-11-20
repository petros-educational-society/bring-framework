package com.petros.bringframework.beans;

import com.petros.bringframework.core.type.convert.ConversionService;
import com.petros.bringframework.util.ClassUtils;

import javax.annotation.Nullable;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class DefaultPropertyEditorRegistry implements PropertyEditorRegistry {

    @Nullable
    private ConversionService conversionService;

    private boolean defaultEditorsActive = false;

    private boolean configValueEditorsActive = false;

    @Nullable
    private Map<Class<?>, PropertyEditor> defaultEditors;

    @Nullable
    private Map<Class<?>, PropertyEditor> overriddenDefaultEditors;

    @Nullable
    private Map<Class<?>, PropertyEditor> customEditors;

    @Nullable
    private Map<String, CustomEditorHolder> customEditorsForPath;

    @Nullable
    private Map<Class<?>, PropertyEditor> customEditorCache;

    @Override
    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        registerCustomEditor(requiredType, null, propertyEditor);
    }

    @Nullable
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    @Override
    public void registerCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath, PropertyEditor propertyEditor) {
        if (requiredType == null && propertyPath == null) {
            throw new IllegalArgumentException("Either requiredType or propertyPath is required");
        }
        if (propertyPath != null) {
            if (this.customEditorsForPath == null) {
                this.customEditorsForPath = new LinkedHashMap<>(16);
            }
            this.customEditorsForPath.put(propertyPath, new CustomEditorHolder(propertyEditor, requiredType));
        } else {
            if (this.customEditors == null) {
                this.customEditors = new LinkedHashMap<>(16);
            }
            this.customEditors.put(requiredType, propertyEditor);
            this.customEditorCache = null;
        }
    }

    @Override
    @Nullable
    public PropertyEditor findCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath) {
        var requiredTypeToUse = requiredType;
        if (nonNull(propertyPath)) {
            if (nonNull(customEditorsForPath)) {
                var editor = getCustomEditor(propertyPath, requiredType);
                if (isNull(editor)) {
                    List<String> strippedPaths = new ArrayList<>();
                    addStrippedPropertyPaths(strippedPaths, "", propertyPath);

                    var it = strippedPaths.iterator();
                    while (it.hasNext() && isNull(editor)) {
                        editor = getCustomEditor(it.next(), requiredType);
                    }
                }
                if (editor != null) {
                    return editor;
                }
            }
            if (requiredType == null) {
                requiredTypeToUse = getPropertyType(propertyPath);
            }
        }
        return getCustomEditor(requiredTypeToUse);
    }

    /**
     * The default implementation always returns {@code null}.
     * Need to override this with the standard {@code getPropertyType}
     * method in specific wrapper.
     *
     * @param propertyPath the property path to determine the type for
     */

    @Nullable
    protected Class<?> getPropertyType(String propertyPath) {
        return null;
    }

    @Nullable
    private PropertyEditor getCustomEditor(String propertyName, @Nullable Class<?> requiredType) {
        CustomEditorHolder holder = null;
        if (nonNull(customEditorsForPath)) {
            holder = customEditorsForPath.get(propertyName);
        }

        if (nonNull(holder)) {
            return holder.getPropertyEditor(requiredType);
        }

        return null;
    }

    @Nullable
    private PropertyEditor getCustomEditor(@Nullable Class<?> requiredType) {
        if (isNull(requiredType) || isNull(customEditors)) {
            return null;
        }

        var editor = customEditors.get(requiredType);
        if (isNull(editor)) {
            if (nonNull(customEditorCache)) {
                editor = this.customEditorCache.get(requiredType);
            }
            if (isNull(editor)) {
                for (var entry : customEditors.entrySet()) {
                    var key = entry.getKey();
                    if (key.isAssignableFrom(requiredType)) {
                        editor = entry.getValue();
                        if (isNull(customEditorCache)) {
                            customEditorCache = new HashMap<>();
                        }
                        customEditorCache.put(requiredType, editor);
                        if (nonNull(editor)) {
                            break;
                        }
                    }
                }
            }
        }
        return editor;
    }

    /**
     * Add property paths with all variations of stripped keys and/or indexes.
     * Invokes itself recursively with nested paths.
     * @param strippedPaths the result list to add to
     * @param nestedPath the current nested path
     * @param propertyPath the property path to check for keys/indexes to strip
     */
    private void addStrippedPropertyPaths(List<String> strippedPaths, String nestedPath, String propertyPath) {
        int startIndex = propertyPath.indexOf('[');
        if (startIndex != -1) {
            int endIndex = propertyPath.indexOf(']');
            if (endIndex != -1) {
                var prefix = propertyPath.substring(0, startIndex);
                var key = propertyPath.substring(startIndex, endIndex + 1);
                var suffix = propertyPath.substring(endIndex + 1);
                strippedPaths.add(nestedPath + prefix + suffix);
                addStrippedPropertyPaths(strippedPaths, nestedPath + prefix, suffix);
                addStrippedPropertyPaths(strippedPaths, nestedPath + prefix + key, suffix);
            }
        }
    }


    /**
     * Holder for a registered custom editor with property name.
     * Keeps the PropertyEditor itself plus the type it was registered for.
     */
    private record CustomEditorHolder(PropertyEditor propertyEditor, @Nullable Class<?> registeredType) {

        @Nullable
        private PropertyEditor getPropertyEditor(@Nullable Class<?> requiredType) {
            if (this.registeredType == null ||
                    (requiredType != null && (ClassUtils.isAssignable(this.registeredType, requiredType) ||
                            ClassUtils.isAssignable(requiredType, this.registeredType))) ||
                    (requiredType == null && (!Collection.class.isAssignableFrom(this.registeredType) && !this.registeredType.isArray()))) {

                return this.propertyEditor;
            }

            return null;
        }
    }
}
