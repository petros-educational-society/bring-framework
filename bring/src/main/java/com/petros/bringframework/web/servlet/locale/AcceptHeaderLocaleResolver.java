package com.petros.bringframework.web.servlet.locale;

import com.petros.bringframework.util.StringUtils;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * {@link LocaleResolver} implementation that simply uses the primary locale
 * specified in the "accept-language" header of the HTTP request
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class AcceptHeaderLocaleResolver implements LocaleResolver {
    private final List<Locale> supportedLocales = new ArrayList<>(4);

    @Nullable
    private Locale defaultLocale;


    /**
     * Configure supported locales to check against the requested locales
     * determined.
     */
    public void setSupportedLocales(List<Locale> locales) {
        this.supportedLocales.clear();
        this.supportedLocales.addAll(locales);
    }

    /**
     * Return the configured list of supported locales.
     */
    public List<Locale> getSupportedLocales() {
        return this.supportedLocales;
    }

    /**
     * Configure a fixed default locale to fall back on if the request does not
     * have an "Accept-Language" header.
     */
    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * The configured default locale, if any.
     */
    @Nullable
    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale defaultLocale = getDefaultLocale();
        if (defaultLocale != null && request.getHeader("Accept-Language") == null) {
            return defaultLocale;
        }
        Locale requestLocale = request.getLocale();
        List<Locale> supportedLocales = getSupportedLocales();
        if (supportedLocales.isEmpty() || supportedLocales.contains(requestLocale)) {
            return requestLocale;
        }
        Locale supportedLocale = findSupportedLocale(request, supportedLocales);
        if (supportedLocale != null) {
            return supportedLocale;
        }
        return (defaultLocale != null ? defaultLocale : requestLocale);
    }

    @Nullable
    private Locale findSupportedLocale(HttpServletRequest request, List<Locale> supportedLocales) {
        var requestLocales = request.getLocales();
        Locale languageMatch = null;
        while (requestLocales.hasMoreElements()) {
            Locale locale = requestLocales.nextElement();
            if (supportedLocales.contains(locale)) {
                if (languageMatch == null || languageMatch.getLanguage().equals(locale.getLanguage())) {
                    return locale;
                }
            } else if (languageMatch == null) {
                for (var cand : supportedLocales) {
                    if (!StringUtils.hasLength(cand.getCountry()) && cand.getLanguage().equals(locale.getLanguage())) {
                        languageMatch = cand;
                        break;
                    }
                }
            }
        }
        return languageMatch;
    }

    @Override
    public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
        throw new UnsupportedOperationException(
                "Cannot change HTTP accept header - use a different locale resolution strategy");
    }
}
