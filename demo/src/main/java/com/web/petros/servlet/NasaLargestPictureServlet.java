package com.web.petros.servlet;

import com.petros.bringframework.context.annotation.AnnotationConfigApplicationContext;
import com.petros.bringframework.web.context.annotation.Controller;
import com.web.petros.controller.NasaController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */

@WebServlet(name = "nasaLargestPictureServlet",
        urlPatterns = "/api/nasa/photos/*")
public class NasaLargestPictureServlet extends BasicInitializationContextServlet {
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
