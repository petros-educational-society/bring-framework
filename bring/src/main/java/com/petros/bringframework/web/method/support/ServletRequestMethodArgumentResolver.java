package com.petros.bringframework.web.method.support;

import com.petros.bringframework.core.MethodParameter;
import com.petros.bringframework.util.ClassUtils;
import com.petros.bringframework.web.context.request.NativeWebRequest;
import com.petros.bringframework.web.context.request.WebRequest;
import com.petros.bringframework.web.method.HandlerMethodArgumentResolver;
import com.petros.bringframework.web.servlet.support.RequestContextUtils;

import javax.annotation.Nullable;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.PushBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.Principal;
import java.util.Locale;

//todo: remove this impl on the next review if it wasn't use
/**
 * Resolves servlet backed request-related method arguments.
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class ServletRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Nullable
    private static Class<?> pushBuilder;

    static {
        try {
            pushBuilder = ClassUtils.forName("javax.servlet.http.PushBuilder",
                    ServletRequestMethodArgumentResolver.class.getClassLoader());
        }
        catch (ClassNotFoundException ex) {
            // Servlet 4.0 PushBuilder not found - not supported for injection
            pushBuilder = null;
        }
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return WebRequest.class.isAssignableFrom(paramType) ||
                ServletRequest.class.isAssignableFrom(paramType) ||
                HttpSession.class.isAssignableFrom(paramType) ||
                pushBuilder != null && pushBuilder.isAssignableFrom(paramType) ||
                Principal.class.isAssignableFrom(paramType) && !parameter.hasParameterAnnotations() ||
                InputStream.class.isAssignableFrom(paramType) ||
                Reader.class.isAssignableFrom(paramType) ||
                Locale.class == paramType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, NativeWebRequest webRequest) throws Exception {
        Class<?> paramType = parameter.getParameterType();
        if (WebRequest.class.isAssignableFrom(paramType)) {
            if (!paramType.isInstance(webRequest)) {
                throw new IllegalStateException(
                        "Current request is not of type [" + paramType.getName() + "]: " + webRequest);
            }
            return webRequest;
        }

        if (ServletRequest.class.isAssignableFrom(paramType)) {
            return resolveNativeRequest(webRequest, paramType);
        }

        return resolveArgument(paramType, resolveNativeRequest(webRequest, HttpServletRequest.class));
    }

    private <T> T resolveNativeRequest(NativeWebRequest webRequest, Class<T> requiredType) {
        T nativeRequest = webRequest.getNativeRequest(requiredType);
        if (nativeRequest == null) {
            throw new IllegalStateException(
                    "Current request is not of type [" + requiredType.getName() + "]: " + webRequest);
        }
        return nativeRequest;
    }

    @Nullable
    private Object resolveArgument(Class<?> paramType, HttpServletRequest request) throws IOException {
        if (HttpSession.class.isAssignableFrom(paramType)) {
            HttpSession session = request.getSession();
            if (session != null && !paramType.isInstance(session)) {
                throw new IllegalStateException(
                        "Current session is not of type [" + paramType.getName() + "]: " + session);
            }
            return session;
        }

        if (pushBuilder != null && pushBuilder.isAssignableFrom(paramType)) {
            return resolvePushBuilder(request, paramType);
        }

        if (InputStream.class.isAssignableFrom(paramType)) {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null && !paramType.isInstance(inputStream)) {
                throw new IllegalStateException(
                        "Request input stream is not of type [" + paramType.getName() + "]: " + inputStream);
            }
            return inputStream;
        }

        if (Reader.class.isAssignableFrom(paramType)) {
            Reader reader = request.getReader();
            if (reader != null && !paramType.isInstance(reader)) {
                throw new IllegalStateException(
                        "Request body reader is not of type [" + paramType.getName() + "]: " + reader);
            }
            return reader;
        }

        if (Principal.class.isAssignableFrom(paramType)) {
            Principal userPrincipal = request.getUserPrincipal();
            if (userPrincipal != null && !paramType.isInstance(userPrincipal)) {
                throw new IllegalStateException(
                        "Current user principal is not of type [" + paramType.getName() + "]: " + userPrincipal);
            }
            return userPrincipal;
        }

        if (Locale.class == paramType) {
            return RequestContextUtils.getLocale(request);
        }

        throw new UnsupportedOperationException("Unknown parameter type: " + paramType.getName());
    }

    @Nullable
    public static Object resolvePushBuilder(HttpServletRequest request, Class<?> paramType) {
        PushBuilder pushBuilder = request.newPushBuilder();
        if (pushBuilder != null && !paramType.isInstance(pushBuilder)) {
            throw new IllegalStateException(
                    "Current push builder is not of type [" + paramType.getName() + "]: " + pushBuilder);
        }
        return pushBuilder;

    }
}
