package com.petros.bringframework.web.context;

import com.petros.bringframework.context.ApplicationContextException;
import com.petros.bringframework.context.ApplicationContextInitializer;
import com.petros.bringframework.context.ConfigurableApplicationContext;
import com.petros.bringframework.core.io.ClassPathResource;
import com.petros.bringframework.core.io.Resource;
import com.petros.bringframework.util.BeanUtils;
import com.petros.bringframework.util.ClassUtils;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static com.petros.bringframework.web.context.WebAppContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import static java.util.Objects.nonNull;

/**
 * Bootstrap listener to start up and shut down Brings root {@link WebAppContext}.
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */
//todo: remove this class on the next review
@Log4j2
public class ContextLoaderListener implements ServletContextListener {

    /**
     * Config param for the root WebAppContext implementation class to use: {@value}.
     */
    public static final String CONTEXT_CLASS_PARAM = "contextClass";

    /**
     * Name of the class path resource
     * that defines ContextLoaderListener's default strategy names.
     */
    private static final String DEFAULT_STRATEGIES_PATH = "ContextLoader.properties";


    private static final Properties defaultStrategies;

    static {
        try {
            var resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, ContextLoaderListener.class);
            defaultStrategies = loadProperties(resource);
        }
        catch (IOException ex) {
            log.debug("Could not load 'ContextLoader.properties': {}", ex.getMessage());
            throw new IllegalStateException("Could not load 'ContextLoader.properties': " + ex.getMessage());
        }
    }

    @Nullable
    private WebAppContext context;

    private static final Map<ClassLoader, WebAppContext> currentContextPerThread = new ConcurrentHashMap<>(1);

    @Nullable
    private static volatile WebAppContext currentContext;

    private final List<ApplicationContextInitializer<ConfigurableApplicationContext>> contextInitializers = new ArrayList<>();

    /**
     * Load properties from the given resource.
     * @param resource the resource to load from
     * @return the populated Properties instance
     * @throws IOException if loading failed
     */
    public static Properties loadProperties(Resource resource) throws IOException {
        var props = new Properties();
        try (InputStream is = resource.getInputStream()) {
            props.load(is);
        }
        return props;
    }

    public ContextLoaderListener() {
    }

    /**
     * Create a new {@code ContextLoaderListener} with the given application context. This
     * constructor is useful in Servlet environments where instance-based
     * registration of listeners is possible through the {@link javax.servlet.ServletContext#addListener}
     * API.
     * @param context the application context to manage
     */
    public ContextLoaderListener(@Nullable WebAppContext context) {
        this.context = context;
    }

    /**
     * Initialize the root web application context.
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        initWebApplicationContext(event.getServletContext());
    }

    /**
     * Close the root web application context.
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        //todo: investigate how correct to close the context
    }

    /**
     * Specify which {@link ApplicationContextInitializer} instances should be used
     * to initialize the application context used by this {@code ContextLoaderListener}.
     */
    @SuppressWarnings("unchecked")
    public void setContextInitializers(@Nullable ApplicationContextInitializer<?>... initializers) {
        if (nonNull(initializers)) {
            for (var initializer : initializers) {
                contextInitializers.add((ApplicationContextInitializer<ConfigurableApplicationContext>) initializer);
            }
        }
    }

    /**
     * Initialize Brings application context for the given servlet context,
     * using the application context provided at construction time, or creating a new one
     */
    public void initWebApplicationContext(ServletContext ctx) {
        if (nonNull(ctx.getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))) {
            throw new IllegalStateException("Cannot initialize context because there is already a root app context present!");
        }

        ctx.log("Initializing Bring root WebAppContext");
        if (log.isInfoEnabled()) {
            log.info("Root WebAppContext: initialization started");
        }

        long startTime = System.currentTimeMillis();
        try {
            if (this.context == null) {
                this.context = createWebApplicationContext(ctx);
            }

            ctx.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);

            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            if (ccl == this.getClass().getClassLoader()) {
                currentContext = this.context;
            }
            else if (ccl != null) {
                currentContextPerThread.put(ccl, this.context);
            }

            if (log.isInfoEnabled()) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                log.info("Root WebApplicationContext initialized in " + elapsedTime + " ms");
            }

        }
        catch (RuntimeException | Error ex) {
            log.error("Context initialization failed", ex);
            ctx.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ex);
            throw ex;
        }
    }

    /**
     * Instantiate the root WebAppContext for this loader, either the
     * default context class or a custom context class if specified.
     * @param sc current servlet context
     * @return the root WebAppContext
     */
    protected WebAppContext createWebApplicationContext(ServletContext sc) {
        return (WebAppContext) BeanUtils.instantiateClass(determineContextClass(sc));
    }

    /**
     * Return the WebAppContext implementation class to use.
     * @return the WebAppContext implementation class to use
     */
    protected Class<?> determineContextClass(ServletContext servletContext) {
        var contextClassName = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);
        if (nonNull(contextClassName)) {
            try {
                return ClassUtils.forName(contextClassName, ClassUtils.getDefaultClassLoader());
            }
            catch (ClassNotFoundException ex) {
                throw new ApplicationContextException(
                        "Failed to load custom context class [" + contextClassName + "]", ex);
            }
        }
        else {
            contextClassName = defaultStrategies.getProperty(WebAppContext.class.getName());
            try {
                return ClassUtils.forName(contextClassName, this.getClass().getClassLoader());
            }
            catch (ClassNotFoundException ex) {
                log.debug("Failed to load default context class [{}]", contextClassName, ex);
                throw new ApplicationContextException(
                        "Failed to load default context class [" + contextClassName + "]", ex);
            }
        }
    }
}
