package com.petros.bringframework.web.servlet.support.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Implement this interface to configure data mapper for custom typed request/response body within controller
 *
 * @author Serhii Dorodko
 */
public interface DataMapper {
    <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException;
    String writeValueAsString(Object value) throws JsonProcessingException;
}
