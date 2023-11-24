package com.petros.bringframework.web.context.request;

import javax.annotation.Nullable;

/**
 * Extension of the {@link WebRequest} interface, exposing the
 * native request and response objects in a generic fashion.
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface NativeWebRequest extends WebRequest  {

    /**
     * Return the underlying native request object.
     */
    Object getNativeRequest();

    /**
     * Return the underlying native response object, if any.
     */
    @Nullable
    Object getNativeResponse();

    /**
     * Return the underlying native request object, if available.
     * @param requiredType the desired type of request object
     * @return the matching request object, or {@code null} if none
     * of that type is available
     */
    @Nullable
    <T> T getNativeRequest(@Nullable Class<T> requiredType);

    /**
     * Return the underlying native response object, if available.
     * @param requiredType the desired type of response object
     * @return the matching response object, or {@code null} if none
     * of that type is available
     */
    @Nullable
    <T> T getNativeResponse(@Nullable Class<T> requiredType);
}
