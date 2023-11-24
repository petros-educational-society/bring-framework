package com.petros.bringframework.web.method.support;

import com.petros.bringframework.core.MethodParameter;
import com.petros.bringframework.web.context.request.NativeWebRequest;
import com.petros.bringframework.web.method.HandlerMethodArgumentResolver;

import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Resolves servlet backed response-related method arguments. Supports values of the
 * following types:
 * <ul>
 * <li>{@link ServletResponse}
 * <li>{@link OutputStream}
 * <li>{@link Writer}
 * </ul>
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class ServletResponseMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return (ServletResponse.class.isAssignableFrom(paramType) ||
                OutputStream.class.isAssignableFrom(paramType) ||
                Writer.class.isAssignableFrom(paramType));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, NativeWebRequest webRequest) throws Exception {
        Class<?> paramType = parameter.getParameterType();
        if (ServletResponse.class.isAssignableFrom(paramType)) {
            return resolveNativeResponse(webRequest, paramType);
        }

        return resolveArgument(paramType, resolveNativeResponse(webRequest, ServletResponse.class));
    }

    private <T> T resolveNativeResponse(NativeWebRequest webRequest, Class<T> requiredType) {
        T nativeResponse = webRequest.getNativeResponse(requiredType);
        if (nativeResponse == null) {
            throw new IllegalStateException(
                    "Current response is not of type [" + requiredType.getName() + "]: " + webRequest);
        }
        return nativeResponse;
    }

    private Object resolveArgument(Class<?> paramType, ServletResponse response) throws IOException {
        if (OutputStream.class.isAssignableFrom(paramType)) {
            return response.getOutputStream();
        }

        if (Writer.class.isAssignableFrom(paramType)) {
            return response.getWriter();
        }

        throw new UnsupportedOperationException("Unknown parameter type: " + paramType);
    }

}
