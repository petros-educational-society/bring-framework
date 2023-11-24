package com.petros.bringframework.web.context.annotation;

import com.petros.bringframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Viktor Basanets
 * @Project: bring-framework
 */
public class ServletAnnotationConfigApplicationContext extends AnnotationConfigApplicationContext {
    public ServletAnnotationConfigApplicationContext(Class<?>... componentClasses) {
        super(componentClasses);
    }

    public List<Object> findControllers() {
        return Arrays.stream(getBeanFactory().getSingletonNames()).collect(Collectors.toList());
    }
}
