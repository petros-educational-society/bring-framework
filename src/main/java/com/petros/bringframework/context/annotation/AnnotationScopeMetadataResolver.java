package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.AnnotatedBeanDefinition;
import com.petros.bringframework.beans.factory.config.AnnotationMetadata;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.main.Scope;
import com.petros.bringframework.core.AssertUtils;

import java.util.Map;

/**
 * @author "Maksym Oliinyk"
 */
public class AnnotationScopeMetadataResolver implements ScopeMetadataResolver {

    private static final String DEFAULT_SCOPE_NAME = "singleton";
    private static final ScopedProxyMode DEFAULT_SCOPED_PROXY_MODE = ScopedProxyMode.NO;


    @Override
    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        AssertUtils.notNull(definition, "BeanDefinition must not be null");

        if (definition instanceof AnnotatedBeanDefinition annDef) {
            final AnnotationMetadata metadata = annDef.getMetadata();
            final String annotationName = Scope.class.getName();
            final boolean isAnnotated = metadata.hasAnnotation(annotationName);
            if (isAnnotated) {
                final Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(annotationName);
                System.out.println(annotationAttributes);
            }
        }

        return new ScopeMetadata(DEFAULT_SCOPE_NAME, DEFAULT_SCOPED_PROXY_MODE);
    }
}
