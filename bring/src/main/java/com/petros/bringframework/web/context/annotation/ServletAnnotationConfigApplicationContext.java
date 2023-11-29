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

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class ServletAnnotationConfigApplicationContext extends AnnotationConfigApplicationContext implements WebAppContext {

    private Map<Class<?>, Object> controllerMap;
    private RequestHandlerRegistry requestHandlerRegistry;

    public ServletAnnotationConfigApplicationContext() {
        super();
        doInit();
    }

    public ServletAnnotationConfigApplicationContext(Class<?>... componentClasses) {
        super(componentClasses);
        doInit();
    }

    public ServletAnnotationConfigApplicationContext(String... basePackages) {
        super(basePackages);
        doInit();
    }

    @Nullable
    @Override
    public ServletContext getServletContext() {
        return null;
    }

    public RequestHandlerRegistry getRequestHandlerRegistry() {
        return requestHandlerRegistry;
    }

    protected void doInit() {
        initControllers();
        initRequestHandlerRegistry();
    }
    private void initControllers() {
        this.controllerMap = getBeanFactory().getBeanDefinitionRegistry()
                .getBeanDefinitions().values()
                .stream()
                .filter(this::isRestController)
                .map(this::toBeanClass)
                .collect(Collectors.toMap(c -> c, c -> getBeanFactory().getBean(c)));
    }

    private void initRequestHandlerRegistry() {
        requestHandlerRegistry = new RequestHandlerRegistry();
        for (Map.Entry<Class<?>, Object> entry : controllerMap.entrySet()) {
            var methods = Arrays.stream(entry.getKey().getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                    .toList();
            requestHandlerRegistry.registerHandlerList(methods, entry.getValue());
        }
    }

    private boolean isRestController(BeanDefinition bd) {
        try {
            return Arrays.stream(Class.forName(bd.getBeanClassName()).getAnnotations())
                    .anyMatch(a -> a.annotationType().isAssignableFrom(RestController.class));
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Class<?> toBeanClass(BeanDefinition bd) {
        try {
            return Class.forName(bd.getBeanClassName());
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}
