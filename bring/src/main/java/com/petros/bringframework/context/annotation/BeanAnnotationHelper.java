package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.support.AnnotationAttributes;
import com.petros.bringframework.core.annotation.AnnotatedElementUtils;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author "Maksym Oliinyk"
 */
@Log4j2
public abstract class BeanAnnotationHelper {
    private static final Map<Method, String> beanNameCache = new ConcurrentHashMap<>();

    private static final Map<Method, Boolean> scopedProxyCache = new ConcurrentHashMap<>();

    public static String determineBeanNameFor(Method beanMethod) {
        String beanName = beanNameCache.get(beanMethod);
        if (beanName == null) {
            // By default, the bean name is the name of the @Bean-annotated method
            beanName = beanMethod.getName();
            // Check to see if the user has explicitly set a custom bean name...
            AnnotationAttributes bean = AnnotatedElementUtils.getAnnotationAttributes(beanMethod, Bean.class);
            if (bean != null) {
                String[] names = bean.getStringArray("name");
                if (names.length > 0) {
                    beanName = names[0];
                }
            }
            beanNameCache.put(beanMethod, beanName);
        }
        return beanName;
    }

    public static boolean isScopedProxy(Method beanMethod) {
        Boolean scopedProxy = scopedProxyCache.get(beanMethod);
        if (scopedProxy == null) {
            AnnotationAttributes scope = AnnotatedElementUtils.getAnnotationAttributes(beanMethod, Scope.class);
            scopedProxy = (scope != null && scope.getEnum("proxyMode") != ScopedProxyMode.NO);
            scopedProxyCache.put(beanMethod, scopedProxy);
        }
        return scopedProxy;
    }


}
