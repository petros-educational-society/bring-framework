package com.petros.bringframework.core.type.provider;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class FieldTypeProvider implements TypeProvider {

    private final String fieldName;

    private final Class<?> declaringClass;

    private transient Field field;

    public FieldTypeProvider(final Field field) {
        this.fieldName = field.getName();
        this.declaringClass = field.getDeclaringClass();
        this.field = field;
    }

    @Override
    public Type getType() {
        return this.field.getGenericType();
    }

    @Override
    public Object getSource() {
        return this.field;
    }
}