package com.petros.bringframework.web.context.annotation;

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
    }

    public ServletAnnotationConfigApplicationContext(Class<?>... componentClasses) {
        super(componentClasses);
        initControllers();
        initRequestHandlerRegistry();
    }

    public ServletAnnotationConfigApplicationContext(String... basePackages) {
        super(basePackages);
    }

    protected void initControllers() {
        this.controllerMap = getBeanFactory().getBeanDefinitionRegistry()
                .getBeanDefinitions().values()
                .stream()
                .filter(beanDefinition -> {
                    try {
                        return Arrays.stream(Class.forName(beanDefinition.getBeanClassName()).getAnnotations())
                                .anyMatch(a -> a.annotationType().isAssignableFrom(RestController.class));
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .map(beanDefinition -> {
                    try {
                        return Class.forName(beanDefinition.getBeanClassName());
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .collect(Collectors.toMap(c -> c, c -> getBeanFactory().getBean(c)));
    }

    protected void initRequestHandlerRegistry() {
        requestHandlerRegistry = new RequestHandlerRegistry();
        for (Map.Entry<Class<?>, Object> entry : controllerMap.entrySet()) {
            var methods = Arrays.stream(entry.getKey().getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                    .toList();
            requestHandlerRegistry.registerHandlerList(methods, entry.getValue());
        }

    }

    @Nullable
    @Override
    public ServletContext getServletContext() {
        return null;
    }

    public RequestHandlerRegistry getRequestHandlerRegistry() {
        return requestHandlerRegistry;
    }
}
