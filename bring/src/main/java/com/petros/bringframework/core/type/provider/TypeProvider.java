package com.petros.bringframework.core.type.provider;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * A {@link Serializable} interface providing access to a {@link Type}.
 *
 * @author "Maksym Oliinyk"
 */
@SuppressWarnings("serial")
public interface TypeProvider extends Serializable {

    /**
     * Return the (possibly non {@link Serializable}) {@link Type}.
     */
    @Nullable
    Type getType();

    /**
     * Return the source of the type, or {@code null} if not known.
     * <p>The default implementations returns {@code null}.
     */
    @Nullable
    default Object getSource() {
        return null;
    }
}