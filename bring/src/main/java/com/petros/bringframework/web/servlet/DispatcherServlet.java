package com.petros.bringframework.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */

@WebServlet("/api/nasa/photos/*")
public class DispatcherServlet extends BasicFrameworkServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var pathInfo = req.getPathInfo();
        var parameterMap = req.getParameterMap();
        var method = req.getMethod();
        var cachedPath = method + " " + pathInfo;
        var m = methodCache.get(cachedPath);

        System.out.println();

        resp.getWriter().println();
    }
}
