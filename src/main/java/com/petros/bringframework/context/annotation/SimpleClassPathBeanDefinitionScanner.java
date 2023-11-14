package com.petros.bringframework.context.annotation;


import com.petros.bringframework.beans.factory.config.AnnotatedBeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanDefinitionHolder;
import com.petros.bringframework.beans.factory.support.AnnotationBeanNameGenerator;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.factory.support.BeanNameGenerator;
import com.petros.bringframework.beans.support.ReflectionScannedGenericBeanDefinition;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.type.reading.ReflectionMetadataReader;
import org.reflections.Reflections;

import java.util.LinkedHashSet;
import java.util.Set;

public class SimpleClassPathBeanDefinitionScanner {

    private final BeanDefinitionRegistry registry;
    private final BeanNameGenerator nameGenerator = AnnotationBeanNameGenerator.INSTANCE;
    private final ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    public SimpleClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public int scan(String... basePackages) {
        final Set<BeanDefinitionHolder> candidates = doScan(basePackages);
        return candidates.size();
    }

    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        AssertUtils.notEmpty(basePackages, "At least one base package must be specified");
        Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = findCandidateComponents(basePackages);
            for (BeanDefinition beanDef : candidates) {
                final String beanName = nameGenerator.generateBeanName(beanDef, registry);
                ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(beanDef);
                beanDef.setScope(scopeMetadata.scopeName());
                if (beanDef instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
                    AnnotationConfigUtils.processCommonDefinitionAnnotations(annotatedBeanDefinition);
                }
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDef, beanName);
                beanDefinitions.add(definitionHolder);
                registerBeanDefinition(definitionHolder);
            }
        }
        return beanDefinitions;

    }

    protected Set<BeanDefinition> findCandidateComponents(String... basePackages) {
        final Set<BeanDefinition> candidates = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Reflections scanner = new Reflections(basePackage);
            final Set<Class<?>> sources = scanner.getTypesAnnotatedWith(Component.class);
            for (Class<?> source : sources) {
                ReflectionMetadataReader metadataReader = new ReflectionMetadataReader(source);
                ReflectionScannedGenericBeanDefinition sbd = new ReflectionScannedGenericBeanDefinition(metadataReader);
                candidates.add(sbd);
            }
        }
        return candidates;
    }

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
