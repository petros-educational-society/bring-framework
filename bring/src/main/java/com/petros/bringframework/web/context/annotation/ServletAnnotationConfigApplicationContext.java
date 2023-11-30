package com.petros.bringframework.web.context.annotation;

import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.context.annotation.AnnotationConfigApplicationContext;
import com.petros.bringframework.web.context.WebAppContext;
import com.petros.bringframework.web.servlet.support.RequestHandlerRegistry;

import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.petros.bringframework.util.StringUtils.hasLength;

/**
 * Extension of AnnotationConfigApplicationContext customized for Servlet-based environments.
 * Manages the initialization of controller mappings and request handling.
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class ServletAnnotationConfigApplicationContext extends AnnotationConfigApplicationContext implements WebAppContext {

    private Map<Class<?>, Object> controllerMap;
    private RequestHandlerRegistry requestHandlerRegistry;

    /**
     * Constructs a ServletAnnotationConfigApplicationContext without any initial components.
     */
    public ServletAnnotationConfigApplicationContext() {
        super();
        doInit();
    }

    /**
     * Constructs a ServletAnnotationConfigApplicationContext with the provided component classes.
     *
     * @param componentClasses Component classes for context initialization
     */
    public ServletAnnotationConfigApplicationContext(Class<?>... componentClasses) {
        super(componentClasses);
        doInit();
    }

    /**
     * Constructs a ServletAnnotationConfigApplicationContext with the provided base packages.
     *
     * @param basePackages Base packages for component scanning
     */
    public ServletAnnotationConfigApplicationContext(String... basePackages) {
        super(basePackages);
        doInit();
    }

    /**
     * Retrieves the ServletContext associated with this application context.
     *
     * @return The ServletContext associated with this context
     */
    @Nullable
    @Override
    public ServletContext getServletContext() {
        return null;
    }

    /**
     * Retrieves the RequestHandlerRegistry associated with this context.
     *
     * @return The RequestHandlerRegistry for managing request handlers
     */
    public RequestHandlerRegistry getRequestHandlerRegistry() {
        return requestHandlerRegistry;
    }

    /**
     * Initializes the context by setting up controllers and the request handler registry.
     */
    protected void doInit() {
        initControllers();
        initRequestHandlerRegistry();
    }

    /**
     * Initializes the controller map by collecting beans annotated as RestController.
     */
    private void initControllers() {
        this.controllerMap = getBeanFactory().getBeanDefinitionRegistry()
                .getBeanDefinitions().values()
                .stream()
                .filter(this::isRestController)
                .map(this::toBeanClass)
                .collect(Collectors.toMap(c -> c, c -> getBeanFactory().getBean(c)));
    }

    /**
     * Initializes the request handler registry by registering methods annotated with RequestMapping within controllers.
     */
    private void initRequestHandlerRegistry() {
        requestHandlerRegistry = new RequestHandlerRegistry();
        for (Map.Entry<Class<?>, Object> entry : controllerMap.entrySet()) {
            var methods = Arrays.stream(entry.getKey().getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                    .toList();
            requestHandlerRegistry.registerHandlerList(methods, entry.getValue());
        }
    }

    /**
     * Checks if a given BeanDefinition is annotated as RestController.
     *
     * @param bd The BeanDefinition to check
     * @return True if the BeanDefinition is annotated as RestController
     */
    private boolean isRestController(BeanDefinition bd) {
        try {
            var beanClassName = bd.getBeanClassName();
            if (!hasLength(beanClassName)) {
                return false;
            }
            return Arrays.stream(Class.forName(bd.getBeanClassName()).getAnnotations())
                    .anyMatch(a -> a.annotationType().isAssignableFrom(RestController.class));
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (Throwable th) {
            throw new RuntimeException(th.getMessage());
        }
    }

    /**
     * Retrieves the Class from a given BeanDefinition.
     *
     * @param bd The BeanDefinition
     * @return The corresponding Class for the BeanDefinition
     */
    private Class<?> toBeanClass(BeanDefinition bd) {
        try {
            return Class.forName(bd.getBeanClassName());
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}
