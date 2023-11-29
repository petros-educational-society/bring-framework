package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.AnnotatedBeanDefinition;
import com.petros.bringframework.beans.factory.config.AnnotationMetadata;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.core.AssertUtils;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

/**
 * @author "Maksym Oliinyk"
 */
@Log4j2
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
                if (annotationAttributes != null) {
                    log.debug(annotationAttributes);
                }
            }
        }

        return new ScopeMetadata(DEFAULT_SCOPE_NAME, DEFAULT_SCOPED_PROXY_MODE);
    }
}
