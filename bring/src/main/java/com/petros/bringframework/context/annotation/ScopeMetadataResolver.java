package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.BeanDefinition;

/**
 * Strategy interface for resolving the scope of bean definitions.
 *
 * @author Maksym Oliinyk
 */
@FunctionalInterface
public interface ScopeMetadataResolver {

    /**
     * Resolve the {@link ScopeMetadata} appropriate to the supplied
     * bean {@code definition}.
     *
     * @param definition the target bean definition
     * @return the relevant scope metadata; never {@code null}
     */
    ScopeMetadata resolveScopeMetadata(BeanDefinition definition);

}