package com.petros.bringframework.web.context;

import com.petros.bringframework.context.ApplicationContext;

import javax.annotation.Nullable;
import javax.servlet.ServletContext;


/**
 * Interface to provide configuration for a web application.
 * <p>This interface adds a {@code getServletContext()} method to the generic
 * ApplicationContext interface, and defines application attribute name.
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public interface WebAppContext extends ApplicationContext {

    /**
     * Context attribute to bind root WebApplicationContext to on successful startup.
     * <p>Note: If the startup of the root context fails, this attribute can contain
     * an exception or error as value.
     */
    String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebAppContext.class.getName() + ".ROOT";

    /**
     * Scope identifier for request scope: "request".
     */
    String SCOPE_REQUEST = "request";

    /**
     * Scope identifier for session scope: "session".
     */
    String SCOPE_SESSION = "session";

    /**
     * Scope identifier for the global web application scope: "application".
     */
    String SCOPE_APPLICATION = "application";

    /**
     * Name of the ServletContext environment bean in the factory.
     */
    String SERVLET_CONTEXT_BEAN_NAME = "servletContext";

    /**
     * Name of the ServletContext init-params environment bean in the factory.
     */
    String CONTEXT_PARAMETERS_BEAN_NAME = "contextParameters";

    /**
     * Name of the ServletContext attributes environment bean in the factory.
     */
    String CONTEXT_ATTRIBUTES_BEAN_NAME = "contextAttributes";


    /**
     * Return the standard Servlet API ServletContext for this application.
     */
    @Nullable
    ServletContext getServletContext();

}
