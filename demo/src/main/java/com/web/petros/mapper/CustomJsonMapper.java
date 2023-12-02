package com.web.petros.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.web.servlet.support.mapper.DataMapper;

@Component
public class CustomJsonMapper implements DataMapper {
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException {
        System.out.println("CustomJsonMapper read");
        return jsonMapper.readValue(content, valueType);
    }

    @Override
    public String writeValueAsString(Object value) throws JsonProcessingException {
        System.out.println("CustomJsonMapper write");
        return jsonMapper.writeValueAsString(value);
    }
}
