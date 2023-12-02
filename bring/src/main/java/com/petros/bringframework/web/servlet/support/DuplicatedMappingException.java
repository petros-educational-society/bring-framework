package com.petros.bringframework.web.servlet.support;

/**
 * @author Serhii Dorodko
 */
public class DuplicatedMappingException extends RuntimeException {
    public DuplicatedMappingException() {
        super("Request mapping must be unique.");
    }
}
