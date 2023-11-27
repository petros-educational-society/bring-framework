package com.petros.bringframework.web.servlet.support;

import com.petros.bringframework.web.servlet.support.common.RequestMethod;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RequestHandlerRegistry {
    private final Set<RequestHandlerFactory> factorySet = new HashSet<>();

    public Boolean registerHandler(Method method){
        return factorySet.add(new RequestHandlerFactory(method));
    }

    public Optional<RequestHandler> getHandler(RequestMethod requestMethod, String path){
        return factorySet.stream()
                .filter(factory -> factory.isMatching(requestMethod, path))
                .findFirst().map(handler -> handler.getHandler(path));
    }
}
