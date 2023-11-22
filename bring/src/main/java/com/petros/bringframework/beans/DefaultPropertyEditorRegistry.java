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

    public void setConversionService(@Nullable ConversionService conversionService) {
        this.conversionService = conversionService;
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

    public void useConfigValueEditors() {
        this.configValueEditorsActive = true;
    }

    protected void registerDefaultEditors() {
        this.defaultEditorsActive = true;
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
     * Retrieve the default editor for the given property type, if any.
     * <p>Lazily registers the default editors, if they are active.
     * @param requiredType type of the property
     * @return the default editor, or {@code null} if none found
     * @see #registerDefaultEditors
     */
    @Nullable
    public PropertyEditor getDefaultEditor(Class<?> requiredType) {
        if (!this.defaultEditorsActive) {
            return null;
        }
        if (this.overriddenDefaultEditors != null) {
            PropertyEditor editor = this.overriddenDefaultEditors.get(requiredType);
            if (editor != null) {
                return editor;
            }
        }
        if (this.defaultEditors == null) {
            createDefaultEditors();
        }
        return this.defaultEditors.get(requiredType);
    }

    /**
     * Actually register the default editors for this registry instance.
     */
    private void createDefaultEditors() {
        this.defaultEditors = new HashMap<>(64);

        // Simple editors, without parameterization capabilities.
        // The JDK does not contain a default editor for any of these target types.
//        this.defaultEditors.put(Charset.class, new CharsetEditor());
//        this.defaultEditors.put(Class.class, new ClassEditor());
//        this.defaultEditors.put(Class[].class, new ClassArrayEditor());
//        this.defaultEditors.put(Currency.class, new CurrencyEditor());
//        this.defaultEditors.put(File.class, new FileEditor());
//        this.defaultEditors.put(InputStream.class, new InputStreamEditor());
//        this.defaultEditors.put(InputSource.class, new InputSourceEditor());
//        this.defaultEditors.put(Locale.class, new LocaleEditor());
//        this.defaultEditors.put(Path.class, new PathEditor());
//        this.defaultEditors.put(Pattern.class, new PatternEditor());
//        this.defaultEditors.put(Properties.class, new PropertiesEditor());
//        this.defaultEditors.put(Reader.class, new ReaderEditor());
//        this.defaultEditors.put(Resource[].class, new ResourceArrayPropertyEditor());
//        this.defaultEditors.put(TimeZone.class, new TimeZoneEditor());
//        this.defaultEditors.put(URI.class, new URIEditor());
//        this.defaultEditors.put(URL.class, new URLEditor());
//        this.defaultEditors.put(UUID.class, new UUIDEditor());
//        this.defaultEditors.put(ZoneId.class, new ZoneIdEditor());
//
//        // Default instances of collection editors.
//        // Can be overridden by registering custom instances of those as custom editors.
//        this.defaultEditors.put(Collection.class, new CustomCollectionEditor(Collection.class));
//        this.defaultEditors.put(Set.class, new CustomCollectionEditor(Set.class));
//        this.defaultEditors.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
//        this.defaultEditors.put(List.class, new CustomCollectionEditor(List.class));
//        this.defaultEditors.put(SortedMap.class, new CustomMapEditor(SortedMap.class));
//
//        // Default editors for primitive arrays.
//        this.defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
//        this.defaultEditors.put(char[].class, new CharArrayPropertyEditor());
//
//        // The JDK does not contain a default editor for char!
//        this.defaultEditors.put(char.class, new CharacterEditor(false));
//        this.defaultEditors.put(Character.class, new CharacterEditor(true));
//
//        // Spring's CustomBooleanEditor accepts more flag values than the JDK's default editor.
//        this.defaultEditors.put(boolean.class, new CustomBooleanEditor(false));
//        this.defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));
//
//        // The JDK does not contain default editors for number wrapper types!
//        // Override JDK primitive number editors with our own CustomNumberEditor.
//        this.defaultEditors.put(byte.class, new CustomNumberEditor(Byte.class, false));
//        this.defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
//        this.defaultEditors.put(short.class, new CustomNumberEditor(Short.class, false));
//        this.defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));
//        this.defaultEditors.put(int.class, new CustomNumberEditor(Integer.class, false));
//        this.defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
//        this.defaultEditors.put(long.class, new CustomNumberEditor(Long.class, false));
//        this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
//        this.defaultEditors.put(float.class, new CustomNumberEditor(Float.class, false));
//        this.defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
//        this.defaultEditors.put(double.class, new CustomNumberEditor(Double.class, false));
//        this.defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
//        this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
//        this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));
//
//        // Only register config value editors if explicitly requested.
//        if (this.configValueEditorsActive) {
//            StringArrayPropertyEditor sae = new StringArrayPropertyEditor();
//            this.defaultEditors.put(String[].class, sae);
//            this.defaultEditors.put(short[].class, sae);
//            this.defaultEditors.put(int[].class, sae);
//            this.defaultEditors.put(long[].class, sae);
//        }
    }

    /**
     * Determine whether this registry contains a custom editor
     * for the specified array/collection element.
     * @param elementType the target type of the element
     * (can be {@code null} if not known)
     * @param propertyPath the property path (typically of the array/collection;
     * can be {@code null} if not known)
     * @return whether a matching custom editor has been found
     */
    public boolean hasCustomEditorForElement(@Nullable Class<?> elementType, @Nullable String propertyPath) {
        if (propertyPath != null && this.customEditorsForPath != null) {
            for (Map.Entry<String, CustomEditorHolder> entry : this.customEditorsForPath.entrySet()) {
                if (PropertyAccessorUtils.matchesProperty(entry.getKey(), propertyPath) &&
                        entry.getValue().getPropertyEditor(elementType) != null) {
                    return true;
                }
            }
        }
        // No property-specific editor -> check type-specific editor.
        return (elementType != null && this.customEditors != null && this.customEditors.containsKey(elementType));
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
