package com.petros.bringframework.web.servlet;

import com.petros.bringframework.web.context.WebAppContext;
import com.petros.bringframework.web.context.annotation.ServletAnnotationConfigApplicationContext;
import com.petros.bringframework.web.servlet.support.common.RequestMethod;
import com.petros.bringframework.web.servlet.support.mapper.DataMapper;
import com.petros.bringframework.web.servlet.support.utils.Http;

import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** The basic abstract implementation of a HttpServlet abstract class and is used to specify the flow sequence
 * @author Viktor Basanets
 * @author Serhii Dorodko
 * @Project: bring-framework
 */
public abstract class BasicFrameworkServlet extends HttpServlet {

    protected WebAppContext webAppContext;

    /**
     * Create a new {@code FrameworkServlet} with the given web application context. This
     * constructor is useful in Servlet environments where instance-based registration
     * of servlets is possible through the {@link ServletContext#addServlet} API.
     * @param webAppContext the context to use
     */
    public BasicFrameworkServlet(@Nullable WebAppContext webAppContext) {
        this.webAppContext = webAppContext;
    }

    protected void handleRequest(HttpServletRequest req, HttpServletResponse resp, RequestMethod method){
        var ctx = (ServletAnnotationConfigApplicationContext) webAppContext;
        var servletPath = req.getServletPath();
        var handlerRegistry = ctx.getRequestHandlerRegistry();
        handlerRegistry.setMapper(ctx.getBean(DataMapper.class));
        var methodHandler = handlerRegistry.getHandler(method, servletPath);

        methodHandler.ifPresentOrElse(handler -> handler.invoke(req, resp), () -> Http.sendNotFound(resp));
    }
}
