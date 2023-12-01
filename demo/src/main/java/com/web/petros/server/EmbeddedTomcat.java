package com.web.petros.server;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

/**
 * Implementation of {@link ServletContainer} based on the built-in Tomcat
 * @author Viktor Basanets
 * @Project: bring-framework
 */
class EmbeddedTomcat implements ServletContainer {

    private static final String BASE_DIR_NAME = "embedded_tomcat";

    static final ServletContainer CONTAINER = new EmbeddedTomcat();

    @Override
    public void start() throws LifecycleException, URISyntaxException, IOException {
        var tomcat = createTomcat();
        var docBase = createDocBase();

        processResources(createContext(tomcat, docBase), docBase);
        tomcat.start();
        tomcat.getServer().await();
    }

    private File createDocBase() throws URISyntaxException {
        var jarPath = EmbeddedTomcat.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath()
                .replaceAll("\\\\", "/");
        return new File(jarPath.substring(0, jarPath.lastIndexOf("/target/")));
    }

    private Tomcat createTomcat() throws IOException {
        var tomcat = new Tomcat();
        tomcat.setBaseDir(Files.createTempDirectory(BASE_DIR_NAME).toString());
        tomcat.getConnector();
        return tomcat;
    }

    private Context createContext(Tomcat tomcat, File docBase) {
        var rootContextPath = "";
        var ctx = tomcat.addWebapp(rootContextPath, docBase.getAbsolutePath());
        ctx.setParentClassLoader(EmbeddedTomcat.class.getClassLoader());
        return ctx;
    }

    private void processResources(Context context, File docBase) {
        var resources = new StandardRoot(context);
        var additionResource = new File(docBase.getAbsolutePath(), "target/classes");
        var resourceSet = new DirResourceSet(resources, "/WEB-INF/classes", additionResource.getAbsolutePath(), "/");
        resources.addPreResources(resourceSet);
        context.setResources(resources);
    }
}
