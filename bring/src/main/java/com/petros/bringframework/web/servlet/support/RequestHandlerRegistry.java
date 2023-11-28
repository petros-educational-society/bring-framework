package com.petros.bringframework.web.servlet.support;

import com.petros.bringframework.web.servlet.support.common.RequestMethod;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Registry of all RequestHandlerFactories each per requestMapping
 * Finds needed handler for request method and path
 * @author Serhii Dorodko
 */
public class RequestHandlerRegistry {
    private final Set<RequestHandlerFactory> factorySet = new HashSet<>();

    public Boolean registerHandler(Method method, Object controllerBean){
        return factorySet.add(new RequestHandlerFactory(method, controllerBean));
    }

    public void registerHandlerList(List<Method> methodList, Object controllerBean){
        for (Method method : methodList) {
            factorySet.add(new RequestHandlerFactory(method, controllerBean));
        }
    }

    public Optional<RequestHandler> getHandler(RequestMethod requestMethod, String path){
        return factorySet.stream()
                .filter(factory -> factory.isMatching(requestMethod, path))
                .findFirst()
                .map(handler -> handler.getHandler(path));
    }
}
