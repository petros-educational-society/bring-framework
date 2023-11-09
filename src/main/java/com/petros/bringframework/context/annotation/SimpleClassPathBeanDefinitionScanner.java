package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanDefinitionHolder;
import com.petros.bringframework.beans.support.BeanDefinitionRegistry;
import com.petros.bringframework.core.AssertUtils;
import org.reflections.Reflections;

import java.util.LinkedHashSet;
import java.util.Set;

public class SimpleClassPathBeanDefinitionScanner {

    private BeanDefinitionRegistry registry;
//    private BeanNameGenerator nameGenerator;

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
                final String beanName = "beanName"; //todo nameGenerator.generateBeanName(beanDef, registry);
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDef, beanName);
                beanDefinitions.add(definitionHolder);
                registerBeanDefinition(definitionHolder);
            }
        }
        return beanDefinitions;

    }

    protected Set<BeanDefinition> findCandidateComponents(String... basePackages) {
        Reflections scanner = new Reflections(basePackages);
        final Set<Class<?>> sources = scanner.getTypesAnnotatedWith(Component.class);

        return Set.of();
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
