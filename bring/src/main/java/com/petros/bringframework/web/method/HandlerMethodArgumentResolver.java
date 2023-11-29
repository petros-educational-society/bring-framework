package com.petros.bringframework.web.method;

import com.petros.bringframework.core.MethodParameter;
import com.petros.bringframework.web.context.request.NativeWebRequest;

import javax.annotation.Nullable;

//todo: remove this interface on the next review if it wasn't use
/**
 * Interface for resolving method parameters into argument values in
 * the context of a given request
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface HandlerMethodArgumentResolver {

    /**
     * Whether the given {@linkplain MethodParameter method parameter} is
     * supported by this resolver.
     * @param parameter the method parameter to check
     * @return {@code true} if this resolver supports the supplied parameter;
     * {@code false} otherwise
     */
    boolean supportsParameter(MethodParameter parameter);

    /**
     * Resolves a method parameter into an argument value from a given request.
     * @return the resolved argument value, or {@code null} if not resolvable
     * @throws Exception in case of errors with the preparation of argument values
     */
    @Nullable
    Object resolveArgument(MethodParameter parameter, NativeWebRequest webRequest) throws Exception;
}
