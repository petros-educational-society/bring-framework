package com.petros.bringframework.web.context.request;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Generic interface for a web request. Mainly intended for generic web
 * request interceptors, giving them access to general request metadata,
 * not for actual handling of the request.
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface WebRequest extends RequestAttributes {
    /**
     * Return the request header of the given name, or {@code null} if none.
     */
    @Nullable
    String getHeader(String headerName);

    /**
     * Return the request header values for the given header name,
     * or {@code null} if none.
     */
    @Nullable
    String[] getHeaderValues(String headerName);

    /**
     * Return a Iterator over request header names.
     */
    Iterator<String> getHeaderNames();

    /**
     * Return the request parameter of the given name, or {@code null} if none.
     */
    @Nullable
    String getParameter(String paramName);

    /**
     * Return the request parameter values for the given parameter name,
     * or {@code null} if none.
     */
    @Nullable
    String[] getParameterValues(String paramName);

    /**
     * Return a Iterator over request parameter names.
     */
    Iterator<String> getParameterNames();

    /**
     * Return a immutable Map of the request parameters, with parameter names as map keys
     * and parameter values as map values. The map values will be of type String array.
     */
    Map<String, String[]> getParameterMap();

    /**
     * Return the primary Locale for this request.
     */
    Locale getLocale();

    /**
     * Return the context path for this request
     * (usually the base path that the current web application is mapped to).
     */
    String getContextPath();

    /**
     * Get a short description of this request,
     * typically containing request URI and session id.
     * @param includeClientInfo whether to include client-specific
     * information such as session id and username
     * @return the requested description as String
     */
    String getDescription(boolean includeClientInfo);

}
