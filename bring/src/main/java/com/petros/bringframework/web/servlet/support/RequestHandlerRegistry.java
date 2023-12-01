package com.petros.bringframework.web.servlet.support;

import com.petros.bringframework.web.servlet.support.common.RequestMethod;
import com.petros.bringframework.web.servlet.support.mapper.DataMapper;
import com.petros.bringframework.web.servlet.support.mapper.JsonDataMapper;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Registry of all RequestHandlerFactories each per requestMapping
 * Finds needed handler for request method and path
 *
 * @author Serhii Dorodko
 */
public class RequestHandlerRegistry {
    private final Set<RequestHandlerFactory> factorySet = new HashSet<>();
    private DataMapper mapper;

    public void setMapper(DataMapper mapper) {
        if (mapper != null)
            this.mapper = mapper;
    }

    public void registerHandlerList(List<Method> methodList, Object controllerBean) {
        for (Method method : methodList) {
            factorySet.add(new RequestHandlerFactory(method, controllerBean));
        }
    }

    public Optional<RequestResponseHandler> getHandler(RequestMethod requestMethod, String path) {
        return factorySet.stream()
                .filter(factory -> factory.isMatching(requestMethod, path))
                .findFirst()
                .map(handler -> handler.getHandler(path, mapper));
    }
}
