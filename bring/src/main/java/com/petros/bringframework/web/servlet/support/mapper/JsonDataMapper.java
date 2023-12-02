package com.petros.bringframework.web.servlet.support.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bringframework.context.annotation.Component;

/**
 * Default data mapper
 *
 * @author Serhii Dorodko
 */
@Component
public class JsonDataMapper implements DataMapper {
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException {
        return jsonMapper.readValue(content, valueType);
    }

    @Override
    public String writeValueAsString(Object value) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(value);
    }
}