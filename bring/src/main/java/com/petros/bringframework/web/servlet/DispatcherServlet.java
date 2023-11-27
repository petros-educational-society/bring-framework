package com.petros.bringframework.web.servlet;


import com.petros.bringframework.web.context.WebAppContext;
import com.petros.bringframework.web.servlet.support.RequestHandlerRegistry;
import com.petros.bringframework.web.servlet.support.common.RequestMethod;
import com.petros.bringframework.web.servlet.support.utils.Http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//todo: finish the implementation
public class DispatcherServlet extends BasicFrameworkServlet {

    public final RequestHandlerRegistry requestHandlerRegistry;

    /**
     * Create a new {@code DispatcherServlet} with the given web application context. This
     * constructor is useful in Servlet environments where instance-based registration
     * of servlets is possible through the {@link ServletContext#addServlet} API.
     * @param webAppContext the context to use
     */
    public DispatcherServlet(WebAppContext webAppContext) {
        super(webAppContext);
        this.requestHandlerRegistry = new RequestHandlerRegistry();
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) {

        var pathInfo = req.getPathInfo();
        var controllerMethodHandler = requestHandlerRegistry.getHandler(RequestMethod.HEAD, pathInfo);

        controllerMethodHandler.ifPresentOrElse(
                requestHandler -> requestHandler.invoke(req, resp),
                () -> Http.sendBadRequest(resp)
        );
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        var pathInfo = req.getPathInfo();
        var controllerMethodHandler = requestHandlerRegistry.getHandler(RequestMethod.GET, pathInfo);

        controllerMethodHandler.ifPresentOrElse(
                requestHandler -> requestHandler.invoke(req, resp),
                () -> Http.sendBadRequest(resp)
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        var pathInfo = req.getPathInfo();
        var controllerMethodHandler = requestHandlerRegistry.getHandler(RequestMethod.POST, pathInfo);

        controllerMethodHandler.ifPresentOrElse(
                requestHandler -> requestHandler.invoke(req, resp),
                () -> Http.sendBadRequest(resp)
        );
    }
}
