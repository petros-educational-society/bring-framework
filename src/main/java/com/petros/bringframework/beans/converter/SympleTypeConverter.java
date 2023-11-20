package com.petros.bringframework.beans.converter;

import com.petros.bringframework.beans.TypeConverter;
import com.petros.bringframework.beans.exception.TypeMismatchException;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.core.type.convert.TypeDescriptor;
import com.petros.bringframework.core.type.convert.support.ConversionException;

import javax.annotation.Nullable;

public class SympleTypeConverter implements TypeConverter {

    private final TypeConverterDelegate typeConverterDelegate;

    public SympleTypeConverter(TypeConverterDelegate typeConverterDelegate) {
        this.typeConverterDelegate = typeConverterDelegate;
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
        }
        catch (ConverterNotFoundException | IllegalStateException ex) {
            throw new ConversionNotSupportedException(value, requiredType, ex);
        }
        catch (ConversionException | IllegalArgumentException ex) {
            throw new TypeMismatchException(value, requiredType, ex);
        }
    }


}
