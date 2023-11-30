package com.petros.bringframework.beans.converter;

import com.petros.bringframework.beans.DefaultPropertyEditorRegistry;
import com.petros.bringframework.beans.TypeConverter;
import com.petros.bringframework.beans.exception.ConversionNotSupportedException;
import com.petros.bringframework.beans.exception.TypeMismatchException;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.core.type.convert.ConverterNotFoundException;
import com.petros.bringframework.core.type.convert.TypeDescriptor;
import com.petros.bringframework.core.type.convert.ConversionException;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;

import static java.util.Objects.nonNull;

/**
 * Simple implementation of the {@link TypeConverter} interface that does not operate on
 * a specific target object. This is an alternative to using a full-blown BeanWrapperImpl
 * instance for arbitrary type conversion needs, while using the very same conversion
 * algorithm (including delegation to {@link java.beans.PropertyEditor}
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */

@Log4j2
public class SympleTypeConverter extends DefaultPropertyEditorRegistry implements TypeConverter {

    private final TypeConverterDelegate typeConverterDelegate;

    public SympleTypeConverter() {
        typeConverterDelegate = new TypeConverterDelegate(this);
        registerDefaultEditors();
    }

    @Nullable
    @Override
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> type) throws TypeMismatchException {
        return convertIfNecessary(value, type, TypeDescriptor.valueOf(type));
    }

    @Override
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> type, @Nullable TypeDescriptor descriptor)
            throws TypeMismatchException {
        AssertUtils.state(nonNull(typeConverterDelegate), "No TypeConverterDelegate");
        try {
            return typeConverterDelegate.convertIfNecessary(null, null, value, type, descriptor);
        } catch (ConverterNotFoundException | IllegalStateException ex) {
            log.debug("Conversion not supported: Value={}, Type={}", value, type, ex);
            throw new ConversionNotSupportedException(value, type, ex);
        } catch (ConversionException | IllegalArgumentException ex) {
            log.debug("Type mismatch occurred: Value={}, Type={}", value, type, ex);
            throw new TypeMismatchException(value, type, ex);
        }
    }
}
