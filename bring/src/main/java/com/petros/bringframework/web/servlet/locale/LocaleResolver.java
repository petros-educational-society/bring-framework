package com.petros.bringframework.web.servlet.locale;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Interface for web-based locale resolution strategies that allows for
 * both locale resolution via the request and locale modification via
 * request and response.
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface LocaleResolver {
    /**
     * Resolve the current locale via the given request.
     * Can return a default locale as fallback in any case.
     * @param request the request to resolve the locale for
     * @return the current locale (never {@code null})
     */
    Locale resolveLocale(HttpServletRequest request);

    /**
     * Set the current locale to the given one.
     * @param request the request to be used for locale modification
     * @param response the response to be used for locale modification
     * @param locale the new locale, or {@code null} to clear the locale
     */
    void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale);
}
