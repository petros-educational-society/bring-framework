package com.petros.bringframework.web;

import com.petros.bringframework.util.ReflectionUtils;

import javax.annotation.Nullable;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import static java.util.Objects.nonNull;

/**
 * Servlet {@link ServletContainerInitializer} designed to support code-based
 * configuration of the servlet container using Brings {@link WebAppInitializer} SPI.
 * This class will be loaded and instantiated and have its {@link #onStartup}
 * method invoked by any Servlet container during container startup assuming
 * that the {@code web} module JAR is present on the classpath. This occurs through
 * the JAR Services API {@link ServiceLoader#load(Class)} method detecting the
 * {@code web} module's {@code META-INF/services/javax.servlet.ServletContainerInitializer}
 * service provider configuration file.
 * @author Viktor Basanets
 * @Project: bring-framework
 * @see #onStartup(Set, ServletContext)
 * @see WebAppInitializer
 */
@HandlesTypes(WebAppInitializer.class)
public class BringServletContainerInitializer implements ServletContainerInitializer {
    /**
     * Delegate the {@code ServletContext} to any {@link WebAppInitializer}
     * implementations present on the application classpath.
     * <p>Because this class declares @{@code HandlesTypes(WebAppInitializer.class)},
     * Servlet containers will automatically scan the classpath for implementations
     * of Brings {@code WebAppInitializer} interface and provide the set of all
     * such types to the {@code initializerClasses} parameter of this method.
     * <p>If no {@code WebAppInitializer} implementations are found on the classpath,
     * this method is effectively a no-op. An INFO-level log message will be issued notifying
     * the user that the {@code ServletContainerInitializer} has indeed been invoked but that
     * no {@code WebApplicationInitializer} implementations were found.
     * @param initializerClasses all implementations of
     * {@link WebAppInitializer} found on the application classpath
     * @param ctx the servlet context to be initialized
     */
    @Override
    public void onStartup(@Nullable Set<Class<?>> initializerClasses, ServletContext ctx) throws ServletException {
        List<WebAppInitializer> initializers = Collections.emptyList();
        if (nonNull(initializerClasses)) {
            initializers = new ArrayList<>(initializerClasses.size());
            for (var initializerClass : initializerClasses) {
                if (isThisWebAppInitializerImplementation(initializerClass)) {
                    try {
                        initializers.add((WebAppInitializer) ReflectionUtils.accessibleConstructor(initializerClass).newInstance());
                    }
                    catch (Throwable ex) {
                        throw new ServletException("Failed to instantiate WebAppInitializer class", ex);
                    }
                }
            }
        }

        if (initializers.isEmpty()) {
            ctx.log("No Bring WebAppInitializer types detected on classpath");
            return;
        }

        ctx.log(initializers.size() + " Bring WebAppInitializers detected on classpath");
        for (var initializer : initializers) {
            initializer.onStartup(ctx);
        }
    }

    private boolean isThisWebAppInitializerImplementation(Class<?> initializerClass) {
        if (initializerClass.isInterface()) {
            return false;
        }

        if (Modifier.isAbstract(initializerClass.getModifiers())) {
            return false;
        }

        return WebAppInitializer.class.isAssignableFrom(initializerClass);
    }
}
