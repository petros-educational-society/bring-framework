package com.petros.bringframework.core.type.convert;


/**
 * Base class for exceptions thrown by the conversion system.
 *
 * @author Viktor Basanets
 */

public abstract class ConversionException extends RuntimeException {
    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
