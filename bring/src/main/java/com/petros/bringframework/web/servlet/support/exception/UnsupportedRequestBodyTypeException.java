package com.petros.bringframework.web.servlet.support.exception;

/**
 * @author Serhii Dorodko
 */
public class UnsupportedRequestBodyTypeException extends RuntimeException{
    public UnsupportedRequestBodyTypeException() {
        super("Primitive types are not supported for the @RequestBody parameter.");
    }
}
