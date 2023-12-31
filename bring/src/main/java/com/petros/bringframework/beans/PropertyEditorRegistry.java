package com.petros.bringframework.beans;

import javax.annotation.Nullable;
import java.beans.PropertyEditor;


/**
 * Encapsulates methods for registering JavaBeans {@link PropertyEditor PropertyEditors}
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 *
 */
public interface PropertyEditorRegistry {

    /**
     * Register the given custom property editor for all properties of the given type
     * @param requiredType the type of the property
     * @param propertyEditor the editor to register
     */
    void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor);

    /**
     * Register the given custom property editor for the given type and
     * property, or for all properties of the given type
     * <p>If the property path denotes an array or Collection property,
     * the editor will get applied either to the array/Collection itself
     * (the {@link PropertyEditor} has to create an array or Collection value) or
     * to each element (the {@code PropertyEditor} has to create the element type),
     * depending on the specified required type.
     * <p>Note: Only one single registered custom editor per property path
     * is supported. In the case of a Collection/array, do not register an editor
     * for both the Collection/array and each element on the same property.
     * <p>For example, if you wanted to register an editor for "items[n].quantity"
     * (for all values n), you would use "items.quantity" as the value of the
     * 'propertyPath' argument to this method.
     * @param requiredType the type of the property. This may be {@code null}
     * if a property is given but should be specified in any case, in particular in
     * case of a Collection - making clear whether the editor is supposed to apply
     * to the entire Collection itself or to each of its entries. So as a general rule:
     * <b>Do not specify {@code null} here in case of a Collection/array!</b>
     * @param propertyPath the path of the property (name or nested path), or
     * {@code null} if registering an editor for all properties of the given type
     * @param propertyEditor editor to register
     */
    void registerCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath, PropertyEditor propertyEditor);

    /**
     * Find a custom property editor for the given type and property.
     * @param requiredType the type of the property (can be {@code null} if a property
     * is given but should be specified in any case for consistency checking)
     * @param propertyPath the path of the property (name or nested path), or
     * {@code null} if looking for an editor for all properties of the given type
     * @return the registered editor, or {@code null} if none
     */
    @Nullable
    PropertyEditor findCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath);

}
