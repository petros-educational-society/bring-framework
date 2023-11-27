package com.petros.bringframework.web.context.annotation;

import com.petros.bringframework.context.annotation.AnnotationConfigApplicationContext;
import com.petros.bringframework.web.context.WebAppContext;

import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class ServletAnnotationConfigApplicationContext extends AnnotationConfigApplicationContext implements WebAppContext {
    public ServletAnnotationConfigApplicationContext() {
        super();
    }

    public ServletAnnotationConfigApplicationContext(Class<?>... componentClasses) {
        super(componentClasses);
    }

    public ServletAnnotationConfigApplicationContext(String... basePackages) {
        super(basePackages);
    }

    public List<Object> findControllers() {
        return Arrays.stream(getBeanFactory().getSingletonNames()).collect(Collectors.toList());
    }

    @Nullable
    @Override
    public ServletContext getServletContext() {
        return null;
    }
}
