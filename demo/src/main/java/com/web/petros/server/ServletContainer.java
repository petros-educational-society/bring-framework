package com.web.petros.server;

import org.apache.catalina.LifecycleException;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.web.petros.server.EmbeddedTomcat.CONTAINER;

/**
 * A simple interface whose implementation can run a specific servlet container
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface ServletContainer {
    void start() throws LifecycleException, URISyntaxException, IOException;

    static ServletContainer getContainer() {
        return CONTAINER;
    }
}
