package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.AnnotatedGenericBeanDefinition;
import com.petros.bringframework.beans.factory.support.AnnotationBeanNameGenerator;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.factory.support.BeanNameGenerator;
import com.petros.bringframework.type.reading.ReflectionMetadataReader;
import lombok.Getter;

/**
 * @author "Maksym Oliinyk"
 */
public class AnnotatedBeanDefinitionReader {

    @Getter
    private final BeanDefinitionRegistry registry;

    private BeanNameGenerator nameGenerator = AnnotationBeanNameGenerator.INSTANCE;
    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    /**
     * Register one or more component classes to be processed.
     * Calls to register are idempotent; adding the same component class more than once has no additional effect.
     *
     * @param componentClasses one or more component classes, e.g. @Configuration classes
     */
    public void register(Class<?>[] componentClasses) {
        for (Class<?> componentClass : componentClasses) {
            registerBean(componentClass);
        }
    }

    /**
     * Register a bean from the given bean class, deriving its metadata from
     * class-declared annotations.
     *
     * @param beanClass the class of the bean
     */
    public void registerBean(Class<?> beanClass) {
        ReflectionMetadataReader metadataReader = new ReflectionMetadataReader(beanClass);
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(metadataReader.getAnnotationMetadata());
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());
        final String beanName = this.nameGenerator.generateBeanName(abd, this.registry);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
        registry.registerBeanDefinition(beanName, abd);
    }


}
