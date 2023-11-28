package com.petros.bringframework.web.servlet;


import com.petros.bringframework.web.context.WebAppContext;
import com.petros.bringframework.web.context.annotation.ServletAnnotationConfigApplicationContext;
import com.petros.bringframework.web.servlet.support.RequestHandlerRegistry;
import com.petros.bringframework.web.servlet.support.common.RequestMethod;
import com.petros.bringframework.web.servlet.support.utils.Http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//todo: finish the implementation
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

        var pathInfo = req.getPathInfo();
        var servletPath = req.getServletPath();
//        var controllerMethodHandler = requestHandlerRegistry.getHandler(RequestMethod.HEAD, servletPath);

//        controllerMethodHandler.ifPresentOrElse(
//                requestHandler -> requestHandler.invoke(req, resp),
//                () -> Http.sendBadRequest(resp)
//        );
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        var ctx = (ServletAnnotationConfigApplicationContext) webAppContext;
//        ctx.initControllers();
        var servletPath = req.getServletPath();
        var pathInfo = req.getPathInfo();
        var controllerMethodHandler = ctx.getRequestHandlerRegistry().getHandler(RequestMethod.GET, servletPath);

        controllerMethodHandler.ifPresentOrElse(
                requestHandler -> requestHandler.invoke(req, resp),
                () -> Http.sendBadRequest(resp)
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

//        var pathInfo = req.getPathInfo();
//        var controllerMethodHandler = requestHandlerRegistry.getHandler(RequestMethod.POST, pathInfo);
//
//        controllerMethodHandler.ifPresentOrElse(
//                requestHandler -> requestHandler.invoke(req, resp),
//                () -> Http.sendBadRequest(resp)
//        );
    }
}
