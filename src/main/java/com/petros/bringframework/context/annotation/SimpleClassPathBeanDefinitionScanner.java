package com.petros.bringframework.context.annotation;


import com.petros.bringframework.beans.factory.config.AnnotatedBeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanDefinitionHolder;
import com.petros.bringframework.beans.factory.support.AnnotationBeanNameGenerator;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.factory.support.BeanNameGenerator;
import com.petros.bringframework.beans.support.ScannedGenericBeanDefinition;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.type.reading.MetadataReader;
import com.petros.bringframework.type.reading.ReflectionMetadataReader;
import org.reflections.Reflections;

import java.util.LinkedHashSet;
import java.util.Set;

public class SimpleClassPathBeanDefinitionScanner {

    private final BeanDefinitionRegistry registry;
    private final BeanNameGenerator nameGenerator = AnnotationBeanNameGenerator.INSTANCE;

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
                //todo: implement scope and scope resolver. The ScopeMetadataResolver plays a key role in processing @Scope annotations to determine the appropriate scope for each bean, if the scope is not settled, should be used to default: SINGELTON_SCOPE
//                ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
//                beanDef.setScope(scopeMetadata.getScopeName());
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

    //todo: verify method,  finish implementation
    protected Set<BeanDefinition> findCandidateComponents(String... basePackages) {
        final Set<BeanDefinition> candidates = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Reflections scanner = new Reflections(basePackage);
            final Set<Class<?>> sources = scanner.getTypesAnnotatedWith(Component.class);
            for (Class<?> source : sources) {
                MetadataReader metadataReader = new ReflectionMetadataReader(source);
                ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
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
