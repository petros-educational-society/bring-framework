package com.petros.bringframework.web.servlet.support;

import com.petros.bringframework.core.Conventions;
import com.petros.bringframework.web.WebAppInitializer;
import com.petros.bringframework.web.context.WebAppContext;
import com.petros.bringframework.web.context.annotation.ServletAnnotationConfigApplicationContext;
import com.petros.bringframework.web.servlet.BasicFrameworkServlet;
import com.petros.bringframework.web.servlet.SimpleDispatcherServlet;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;
import java.util.Optional;

import static com.petros.bringframework.core.AssertUtils.hasText;
import static com.petros.bringframework.core.AssertUtils.notNull;
import static com.petros.bringframework.util.ObjectUtils.isNotEmpty;
import static java.util.Objects.isNull;

/**
 * Base class for {@link WebAppInitializer}
 * implementations that register a {@link SimpleDispatcherServlet} in the servlet context.
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@Slf4j
public abstract class AbstractDispatcherServletInitializer implements WebAppInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
//        registerContextLoaderListener(servletContext);
        registerDispatcherServlet(servletContext);
    }

//    /**
//     * Register a {@link ContextLoaderListener} against the given servlet context. The
//     * {@code ContextLoaderListener} is initialized with the application context returned
//     * from the {@link #createRootApplicationContext()} template method.
//     * @param servletContext the servlet context to register the listener against
//     */
//    protected void registerContextLoaderListener(ServletContext servletContext) {
//        var rootAppContext = createRootApplicationContext();
//        if (rootAppContext != null) {
//            ContextLoaderListener listener = new ContextLoaderListener(rootAppContext);
//            listener.setContextInitializers(getRootApplicationContextInitializers());
//            servletContext.addListener(listener);
//        } else {
//            log.debug("No ContextLoaderListener registered, as " +
//                    "createRootApplicationContext() did not return an application context");
//        }
//    }

//    /**
//     * Specify application context initializers to be applied to the root application
//     */
//    @Nullable
//    protected ApplicationContextInitializer<?>[] getRootApplicationContextInitializers() {
//        //todo: refactor this
//        return null;
//    }
//
//    /**
//     * <p>This implementation creates an {@link AbstractDispatcherServletInitializer},
//     * providing it the annotated classes returned by {@link #getRootConfigClasses()}.
//     * Returns {@code null} if {@link #getRootConfigClasses()} returns {@code null}.
//     */
//    @Nullable
//    protected WebAppContext createRootApplicationContext() {
//        return Optional.ofNullable(getRootConfigClasses())
//                .map(ServletAnnotationConfigApplicationContext::new)
//                .orElse(null);
//    }

    /**
     * Specify {@code @Configuration} and/or {@code @Component} classes for the
     * {@linkplain #createServletApplicationContext()} application context.
     * @return the configuration for the root application context, or {@code null}
     * if creation and registration of a root context is not desired
     */
    @Nullable
    protected abstract Class<?>[] getRootConfigClasses();

    /**
     * <p>This implementation creates an {@link ServletAnnotationConfigApplicationContext},
     */
    protected WebAppContext createServletApplicationContext() {
        return Optional.ofNullable(getRootConfigClasses())
                .map(ServletAnnotationConfigApplicationContext::new)
                .orElse(null);
    }

    /**
     * Register a {@link SimpleDispatcherServlet} against the given servlet context.
     * <p>This method will create a {@code SimpleDispatcherServlet} with the name returned by
     * {@link #getServletName()}, initializing it with the application context returned
     * from {@link #createServletApplicationContext()}, and mapping it to the patterns
     * returned from {@link #getServletMappings()}.
     * {@link #createDispatcherServlet(WebAppContext)}.
     * @param servletContext the context to register the servlet against
     */
    protected void registerDispatcherServlet(ServletContext servletContext) {
        var servletName = getServletName();
        hasText(servletName, "getServletName() must not return null or empty");

        var servletAppContext = createServletApplicationContext();
        notNull(servletAppContext, "createServletAppContext() must not return null");

        var dispatcherServlet = createDispatcherServlet(servletAppContext);
        notNull(dispatcherServlet, "createDispatcherServlet(WebAppContext) must not return null");

        var registration = servletContext.addServlet(servletName, dispatcherServlet);
        if (isNull(registration)) {
            throw new IllegalStateException("Failed to register servlet with name '" + servletName + "'. " +
                    "Check if there is another servlet registered under the same name.");
        }

        registration.setLoadOnStartup(1);
        registration.addMapping(getServletMappings());
        registration.setAsyncSupported(isAsyncSupported());

        var filters = getServletFilters();
        if (isNotEmpty(filters)) {
            for (var filter : filters) {
                registerServletFilter(servletContext, filter);
            }
        }
    }

    /**
     * Return the name under which the {@link SimpleDispatcherServlet} will be registered.
     */
    protected String getServletName() {
        return "dispatcher";
    }

    /**
     * Create a {@link SimpleDispatcherServlet} (or other kind of {@link BasicFrameworkServlet}-derived
     * dispatcher) with the specified {@link WebAppContext}.
     */
    protected BasicFrameworkServlet createDispatcherServlet(WebAppContext servletAppContext) {
        return new SimpleDispatcherServlet(servletAppContext);
    }

    /**
     * Specify the servlet mapping(s) for the {@code SimpleDispatcherServlet}
     * for example {@code "/"}, {@code "/app"}, etc.
     * @see #registerDispatcherServlet(ServletContext)
     */
    protected abstract String[] getServletMappings();

    /**
     * Specify filters to add and map to the {@code SimpleDispatcherServlet}.
     * @return an array of filters or {@code null}
     * @see #registerServletFilter(ServletContext, Filter)
     */
    @Nullable
    protected Filter[] getServletFilters() {
        return null;
    }

    /**
     * Add the given filter to the ServletContext and map it to the
     * {@code SimpleDispatcherServlet}
     * @param servletContext the servlet context to register filters with
     * @param filter the filter to be registered
     * @return the filter registration
     */
    protected FilterRegistration.Dynamic registerServletFilter(ServletContext servletContext, Filter filter) {
        var filterName = Conventions.getVariableName(filter);
        var registration = servletContext.addFilter(filterName, filter);

        if (registration == null) {
            int counter = 0;
            while (registration == null) {
                if (counter == 100) {
                    throw new IllegalStateException("Failed to register filter with name '" + filterName + "'. " +
                            "Check if there is another filter registered under the same name.");
                }
                registration = servletContext.addFilter(filterName + "#" + counter, filter);
                counter++;
            }
        }

        registration.setAsyncSupported(isAsyncSupported());
        registration.addMappingForServletNames(getDispatcherTypes(), false, getServletName());
        return registration;
    }

    private EnumSet<DispatcherType> getDispatcherTypes() {
        return isAsyncSupported() ?
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC) :
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
    }

    protected boolean isAsyncSupported() {
        return false;
    }
}
