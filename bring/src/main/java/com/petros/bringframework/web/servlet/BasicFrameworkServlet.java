package com.petros.bringframework.web.servlet;

import com.petros.bringframework.web.context.annotation.RequestMapping;
import com.petros.bringframework.web.context.annotation.ServletAnnotationConfigApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public abstract class BasicFrameworkServlet extends HttpServlet {

    protected static final String BRING_CONTEXT_ATTRIBUTE_NAME = "BRING_CONTEXT";

    //todo: remove and use servletContext instead
    protected final Map<String, Method> methodCache = new ConcurrentHashMap<>();
    protected final Map<Class<?>, Object> controllerCache = new ConcurrentHashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        var servletContext = config.getServletContext();
        var ctx = new ServletAnnotationConfigApplicationContext("com.petros");

        List<Object> controllers = ctx.findControllers();
        controllerCache.putAll(controllers.stream().collect(Collectors.toMap(Object::getClass, b -> b)));

        var methodMap = controllers.stream()
                .flatMap(c -> Arrays.stream(c.getClass().getDeclaredMethods()))
                .filter(m -> m.isAnnotationPresent(RequestMapping.class))
                .collect(Collectors.toMap(m -> format("%s %s", m.getAnnotation(RequestMapping.class).method(), m.getAnnotation(RequestMapping.class).path()), m -> m));
        methodCache.putAll(methodMap);

//        servletContext.setAttribute(BRING_CONTEXT_ATTRIBUTE_NAME, new AnnotationConfigApplicationContext(BaseServletConfig.class));
    }
}
