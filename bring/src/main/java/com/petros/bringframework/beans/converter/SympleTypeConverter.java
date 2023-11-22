package com.petros.bringframework.beans.converter;

import com.petros.bringframework.beans.DefaultPropertyEditorRegistry;
import com.petros.bringframework.beans.TypeConverter;
import com.petros.bringframework.beans.exception.ConversionNotSupportedException;
import com.petros.bringframework.beans.exception.TypeMismatchException;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.core.type.convert.ConverterNotFoundException;
import com.petros.bringframework.core.type.convert.TypeDescriptor;
import com.petros.bringframework.core.type.convert.ConversionException;

import javax.annotation.Nullable;
public class SympleTypeConverter extends DefaultPropertyEditorRegistry implements TypeConverter {

    private final TypeConverterDelegate typeConverterDelegate;
    public SympleTypeConverter() {
        this.typeConverterDelegate = new TypeConverterDelegate(this);
        registerDefaultEditors();
    }
    @Nullable
    @Override
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType) throws TypeMismatchException {
        return convertIfNecessary(value, requiredType, TypeDescriptor.valueOf(requiredType));
    }
    @Override
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType,
                                    @Nullable TypeDescriptor typeDescriptor) throws TypeMismatchException {

        AssertUtils.state(this.typeConverterDelegate != null, "No TypeConverterDelegate");
        try {
            return this.typeConverterDelegate.convertIfNecessary(null, null, value, requiredType, typeDescriptor);
        } catch (ConverterNotFoundException | IllegalStateException ex) {
            throw new ConversionNotSupportedException(value, requiredType, ex);
        } catch (ConversionException | IllegalArgumentException ex) {
            throw new TypeMismatchException(value, requiredType, ex);
        }
    }
}
