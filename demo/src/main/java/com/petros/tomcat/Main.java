package com.petros;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class Main {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);

        // Add your servlet
        tomcat.addServlet("", "myServlet", new MyServlet());
        context.addServletMappingDecoded("/*", "myServlet");

        tomcat.start();
//        tomcat.getService().addConnector(connector1);
        tomcat.getServer().await();
    }
}
