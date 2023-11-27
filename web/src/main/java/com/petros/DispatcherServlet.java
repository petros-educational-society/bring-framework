package com.petros;


import com.petros.common.RequestMethod;
import com.petros.utils.Http;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DispatcherServlet extends HttpServlet {

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) {

        var pathInfo = req.getPathInfo();
        var controllerMethodHandler = Main.requestHandlerRegistry.getHandler(RequestMethod.HEAD, pathInfo);

        controllerMethodHandler.ifPresentOrElse(
                requestHandler -> requestHandler.invoke(req, resp),
                () -> Http.sendBadRequest(resp)
        );
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        var pathInfo = req.getPathInfo();
        var controllerMethodHandler = Main.requestHandlerRegistry.getHandler(RequestMethod.GET, pathInfo);

        controllerMethodHandler.ifPresentOrElse(
                requestHandler -> requestHandler.invoke(req, resp),
                () -> Http.sendBadRequest(resp)
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        var pathInfo = req.getPathInfo();
        var controllerMethodHandler = Main.requestHandlerRegistry.getHandler(RequestMethod.POST, pathInfo);

        controllerMethodHandler.ifPresentOrElse(
                requestHandler -> requestHandler.invoke(req, resp),
                () -> Http.sendBadRequest(resp)
        );
    }
}
