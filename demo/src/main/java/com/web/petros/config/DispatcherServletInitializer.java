package com.web.petros.config;

import com.petros.bringframework.web.servlet.support.AbstractDispatcherServletInitializer;

import javax.annotation.Nullable;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class DispatcherServletInitializer extends AbstractDispatcherServletInitializer {
    @Nullable
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] {DefaultAppConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }
}
