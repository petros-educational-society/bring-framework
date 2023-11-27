package com.petros.bringframework.web.servlet.support;

import com.petros.bringframework.web.servlet.locale.LocaleResolver;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Utility class for easy access to request-specific state
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class RequestContextUtils {
    public static final String LOCALE_RESOLVER_ATTRIBUTE = RequestContextUtils.class.getName() + ".LOCALE_RESOLVER";

    /**
     * Return the LocaleResolver that has been bound to the request by the
     * .........
     * @param request current HTTP request
     * @return the current LocaleResolver, or {@code null} if not found
     */
    @Nullable
    public static LocaleResolver getLocaleResolver(HttpServletRequest request) {
        return (LocaleResolver) request.getAttribute(LOCALE_RESOLVER_ATTRIBUTE);
    }

    /**
     * Retrieve the current locale from the given request, using the
     * LocaleResolver
     * @param request current HTTP request
     * @return the current locale for the given request, either from the
     * LocaleResolver or from the plain request itself
     */
    public static Locale getLocale(HttpServletRequest request) {
        var localeResolver = getLocaleResolver(request);
        return localeResolver != null ? localeResolver.resolveLocale(request) : request.getLocale();
    }
}
