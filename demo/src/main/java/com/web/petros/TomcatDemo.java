package com.web.petros;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

public class TomcatDemo {
    public static void main(String[] args) throws LifecycleException, URISyntaxException, IOException {
        var jarPath = TomcatDemo.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath()
                .replaceAll("\\\\", "/");

        var docBase = new File(jarPath.substring(0, jarPath.lastIndexOf("/target/")));

        var tomcat = new Tomcat();
        tomcat.setBaseDir(Files.createTempDirectory("embedded_tomcat").toString());
        tomcat.getConnector();

        var ctx = tomcat.addWebapp("", docBase.getAbsolutePath());
        ctx.setParentClassLoader(TomcatDemo.class.getClassLoader());

        var additionResource = new File(docBase.getAbsolutePath(), "target/classes");
        var resources = new StandardRoot(ctx);

        var resourceSet = new DirResourceSet(resources, "/WEB-INF/classes", additionResource.getAbsolutePath(), "/");
        resources.addPreResources(resourceSet);
        ctx.setResources(resources);

        tomcat.start();
        tomcat.getServer().await();
    }
}
