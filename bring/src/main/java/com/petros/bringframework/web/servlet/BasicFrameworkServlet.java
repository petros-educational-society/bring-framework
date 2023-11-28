package com.petros.bringframework.web.servlet;

import com.petros.bringframework.web.context.WebAppContext;

import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public abstract class BasicFrameworkServlet extends HttpServlet {

    protected static final String BRING_CONTEXT_ATTRIBUTE_NAME = "BRING_CONTEXT";

    protected WebAppContext webAppContext;

    /**
     * Create a new {@code FrameworkServlet} with the given web application context. This
     * constructor is useful in Servlet 3.0+ environments where instance-based registration
     * of servlets is possible through the {@link ServletContext#addServlet} API.
     * @param webAppContext the context to use
     */
    public BasicFrameworkServlet(@Nullable WebAppContext webAppContext) {
        this.webAppContext = webAppContext;
    }
}
