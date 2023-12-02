package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.AnnotatedBeanDefinition;
import com.petros.bringframework.beans.factory.config.AnnotationMetadata;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.core.AssertUtils;
import lombok.extern.log4j.Log4j2;

/**
 * A {@link ScopeMetadataResolver} implementation that by default checks for
 * the presence of Spring's {@link Scope @Scope} annotation on the bean class.
 *
 * @see com.petros.bringframework.context.annotation.Scope
 * @author "Maksym Oliinyk"
 */
@Log4j2
public class AnnotationScopeMetadataResolver implements ScopeMetadataResolver {


    /**
     * Resolves the scope metadata for a given BeanDefinition.
     *
     * @param definition The BeanDefinition for which the scope metadata needs to be resolved
     * @return ScopeMetadata containing the scope name
     */
    @Override
    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        AssertUtils.notNull(definition, "BeanDefinition must not be null");
        ScopeMetadata metadata = new ScopeMetadata();
        if (definition instanceof AnnotatedBeanDefinition annDef) {
            final AnnotationMetadata annotationMetadata = annDef.getMetadata();
            final boolean isAnnotated = annotationMetadata.hasAnnotation(Scope.class.getName());
            if (isAnnotated) {
                AnnotationConfigUtils.processScopeMetadata(metadata, annotationMetadata);
            }
        }
        return metadata;
    }
}
