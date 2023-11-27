package com.petros.bringframework.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Interface to be implemented in Servlet environments in order to configure the ServletContext programmatically.
 * Implementations of this SPI will be detected automatically by BringServletContainerInitializer, which itself is
 * bootstrapped automatically by any Servlet container.
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface WebAppInitializer {
    /**
     * Configure the given {@link ServletContext} with any servlets, filters, listeners
     * context-params and attributes necessary for initializing this web application.
     * @param ctx the {@code ServletContext} to initialize
     * @throws ServletException if any call against the given {@code ServletContext}
     * throws a {@code ServletException}
     */
    void onStartup(ServletContext ctx) throws ServletException;
}
