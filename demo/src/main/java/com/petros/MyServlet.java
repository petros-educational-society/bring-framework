package com.petros;

import com.petros.bringframework.context.ApplicationContext;
import com.petros.bringframework.context.annotation.AnnotationConfigApplicationContext;
import com.petros.services.UserController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyServlet extends HttpServlet {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var controller = context.getBean(UserController.class);
        var pathInfo = req.getPathInfo();

//        mapa.get()
        resp.getWriter().write("Hello from MyServlet");
    }
}
