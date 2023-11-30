package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.AnnotatedBeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanDefinitionHolder;
import com.petros.bringframework.beans.factory.config.BeanPostProcessor;
import com.petros.bringframework.beans.factory.support.AnnotationBeanNameGenerator;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.factory.support.BeanNameGenerator;
import com.petros.bringframework.beans.support.ReflectionBeanDefinition;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.type.reading.ReflectionMetadataReader;
import org.reflections.Reflections;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Scans a given set of base packages in the classpath for classes annotated with
 * {@link com.petros.bringframework.context.annotation.Component} and its subtypes, registering them
 * as bean definitions in the provided {@link BeanDefinitionRegistry}.
 *
 * @author "Viktor Basanets"
 * @author "Maksym Oliinyk"
 * @see AnnotationConfigApplicationContext#scan
 * @see Component
 */
public class SimpleClassPathBeanDefinitionScanner {

    private final BeanDefinitionRegistry registry;
    private final BeanNameGenerator nameGenerator = AnnotationBeanNameGenerator.INSTANCE;
    private final ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    public SimpleClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    /**
     * Scans the provided base packages for classes annotated with @Component and its subtypes,
     * generating and registering bean definitions for each discovered class.
     *
     * @param basePackages The base packages to scan for component classes.
     * @return The count of bean definitions registered during the scan.
     */
    public int scan(String... basePackages) {
        final Set<BeanDefinitionHolder> candidates = doScan(basePackages);
        return candidates.size();
    }

    /**
     * Scans the provided base packages for candidate components and generates BeanDefinitionHolders
     * for each discovered component class.
     *
     * @param basePackages The base packages to scan for candidate components.
     * @return A set of BeanDefinitionHolder instances for the discovered candidate components.
     */
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        AssertUtils.notEmpty(basePackages, "At least one base package must be specified");
        Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();

        var candidates = findCandidateComponents(basePackages);
        for (var beanDef : candidates) {
            final var beanName = nameGenerator.generateBeanName(beanDef, registry);
            var scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(beanDef);
            beanDef.setScope(scopeMetadata.scopeName());
            if (beanDef instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
                AnnotationConfigUtils.processCommonDefinitionAnnotations(annotatedBeanDefinition);
            }
            var definitionHolder = new BeanDefinitionHolder(beanDef, beanName);
            beanDefinitions.add(definitionHolder);
            registerBeanDefinition(definitionHolder);
        }

        return beanDefinitions;
    }

    /**
     * Finds candidate components within the specified base packages and returns them as BeanDefinition instances.
     *
     * @param basePackages The base packages to scan for candidate components.
     * @return A set of BeanDefinition instances representing the discovered candidate components.
     */
    protected Set<BeanDefinition> findCandidateComponents(String... basePackages) {
        final Set<BeanDefinition> candidates = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Reflections scanner = new Reflections(basePackage);
            final Set<Class<?>> sources = scanner.getTypesAnnotatedWith(Component.class);
            sources.addAll(scanner.getSubTypesOf(BeanPostProcessor.class));
            for (Class<?> source : sources) {
                if (source.isAnnotation() || source.isInterface()) {
                    continue;
                }
                ReflectionMetadataReader metadataReader = new ReflectionMetadataReader(source);
                ReflectionBeanDefinition sbd = new ReflectionBeanDefinition(metadataReader);
                candidates.add(sbd);
            }
        }
        return candidates;
    }

    /**
     * Registers the provided BeanDefinitionHolder in the BeanDefinitionRegistry.
     *
     * @param beanDef The BeanDefinitionHolder to be registered.
     */
    private void registerBeanDefinition(BeanDefinitionHolder beanDef) {
        String beanName = beanDef.getBeanName();
        registry.registerBeanDefinition(beanName, beanDef.getBeanDefinition());

        String[] aliases = beanDef.getAliases();
        if (aliases != null) {
            for (String alias : aliases) {
                registry.registerAlias(beanName, alias);
            }
        }
    }

}
