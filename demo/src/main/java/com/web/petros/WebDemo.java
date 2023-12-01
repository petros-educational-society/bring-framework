package com.web.petros;

import com.web.petros.server.ServletContainer;
import org.apache.catalina.LifecycleException;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class WebDemo {
    public static void main(String[] args) throws LifecycleException, URISyntaxException, IOException {
        var servletContainer = ServletContainer.getContainer();
        servletContainer.start();
    }
}
