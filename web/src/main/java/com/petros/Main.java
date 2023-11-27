package com.petros;

import com.petros.controller.ControllerDummy;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.lang.reflect.Method;

public class Main {

    public final static ControllerDummy controllerBeanDummy = new ControllerDummy();
    public static RequestHandlerRegistry requestHandlerRegistry = new RequestHandlerRegistry();

    public static void main(String[] args) throws Exception {

        // Fill the registry
        var controllerMethods = controllerBeanDummy.getClass().getDeclaredMethods();
        for (Method controllerMethod : controllerMethods) {
            var isRegistered = requestHandlerRegistry.registerHandler(controllerMethod);
        }


        // Embedded TomCat config and start
        String tempDirLocation = "src/main/webapp/temp";
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(tempDirLocation);
        tomcat.setPort(8080);

        String contextPath = "/";
        String docBase = new File("src/main/webapp/").getAbsolutePath();
        System.out.println("docBase: " + docBase);
        Context context = tomcat.addContext(contextPath, docBase);

        tomcat.addServlet("/", "DispatcherServlet", new DispatcherServlet());
        context.addServletMappingDecoded("/*", "DispatcherServlet");

        tomcat.start();
        tomcat.getServer().await();
    }
}