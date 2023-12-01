package com.petros.bringframework.web.servlet;

import com.petros.bringframework.web.context.WebAppContext;
import com.petros.bringframework.web.servlet.support.common.RequestMethod;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple implementation of a BasicFrameworkServlet and is used to override particular Http methods
 * @author Viktor Basanets
 * @author Serhii Dorodko
 * @Project: bring-framework
 */

public class SimpleDispatcherServlet extends BasicFrameworkServlet {

    /**
     * Create a new {@code SimpleDispatcherServlet} with the given web application context. This
     * constructor is useful in Servlet environments where instance-based registration
     * of servlets is possible through the {@link ServletContext#addServlet} API.
     * @param webAppContext the context to use
     */
    public SimpleDispatcherServlet(WebAppContext webAppContext) {
        super(webAppContext);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) {
        handleRequest(req, resp, RequestMethod.HEAD);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        handleRequest(req, resp, RequestMethod.GET);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        handleRequest(req, resp, RequestMethod.POST);
    }
}
