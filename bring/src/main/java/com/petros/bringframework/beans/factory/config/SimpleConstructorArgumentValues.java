package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.util.ClassUtils;
import lombok.Builder;
import lombok.Getter;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds information about constructor argument values, allowing indexed and generic argument values
 * to be added and retrieved.
 *
 * @author "Maksym Oliinyk"
 */
public class SimpleConstructorArgumentValues {
    @Getter
    private final Map<Integer, ValueHolder> indexedArgumentValues = new HashMap<>();
    private final List<ValueHolder> genericArgumentValues = new ArrayList<>();

    /**
     * Add an argument value for the given index in the constructor argument list.
     * Params:
     *
     * @param index – the index in the constructor argument list
     * @param type – the argument type
     * @param name - the argument name
     */
    public void addIndexedArgumentValue(int index, Type type, String name) {
        indexedArgumentValues.put(index, ValueHolder.builder().type(type).name(name).build());
    }

    /**
     * Get argument value for the given index in the constructor argument list.
     *
     * @param index        the index in the constructor argument list
     * @param requiredType the type to match (can be {@code null} to match
     *                     untyped values only)
     * @return the ValueHolder for the argument, or {@code null} if none set
     */
    public ValueHolder getIndexedArgumentValue(int index, Class<?> requiredType) {
        ValueHolder value = indexedArgumentValues.get(index);
        if (value != null && requiredType.isInstance(value)) {
            return value;
        }
        return null;
    }

    /**
     * Check whether an argument value has been registered for the given index.
     *
     * @param index the index in the constructor argument list
     */
    public boolean hasIndexedArgumentValue(int index) {
        return indexedArgumentValues.containsKey(index);
    }


    /**
     * Add a generic argument value to be matched by type.
     * <p>Note: A single generic argument value will just be used once,
     * rather than matched multiple times.
     *
     * @param value the argument value
     */
    public void addGenericArgumentValue(Object value) {
        genericArgumentValues.add(ValueHolder.builder().value(value).build());
    }

    public ValueHolder getGenericArgumentValue(Class<?> requiredType) {
        for (ValueHolder value : genericArgumentValues) {
            if (requiredType != null && value.getType() == null && value.getName() == null &&
                    !ClassUtils.isAssignable(requiredType, value.getValue().getClass())) {
                continue;
            }
            return value;
        }
        return null;
    }


    /**
     * Return the number of argument values held in this instance,
     * counting both indexed and generic argument values.
     */
    public int getArgumentCount() {
        return (this.indexedArgumentValues.size() + this.genericArgumentValues.size());
    }

    /**
     * Return if this holder does not contain any argument values,
     * neither indexed ones nor generic ones.
     */
    public boolean isEmpty() {
        return (this.indexedArgumentValues.isEmpty() && this.genericArgumentValues.isEmpty());
    }


    /**
     * Holder for a constructor argument value, with an optional type
     * attribute indicating the target type of the actual constructor argument.
     */
    @Getter
    public static class ValueHolder {

        @Nullable
        private Object value;

        @Nullable
        private Type type;

        @Nullable
        private String name;

        /**
         * Create a new ValueHolder for the given value, type and name.
         *
         * @param value the argument value
         * @param type  the type of the constructor argument
         * @param name  the name of the constructor argument
         */
        @Builder
        public ValueHolder(@Nullable Object value, @Nullable Type type, @Nullable String name) {
            this.value = value;
            this.type = type;
            this.name = name;
        }


    }

}
