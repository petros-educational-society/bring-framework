package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.factory.support.BeanNameGenerator;
import com.petros.bringframework.context.annotation.AnnotationConfigUtils;
import com.petros.bringframework.context.annotation.AnnotationScopeMetadataResolver;
import com.petros.bringframework.context.annotation.ScopeMetadata;
import com.petros.bringframework.context.annotation.ScopeMetadataResolver;

import java.util.Set;

public class ConfigurationClassBeanDefinitionReader {
    private final BeanDefinitionRegistry registry;
    private final BeanNameGenerator importBeanNameGenerator;
    private static final ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    public ConfigurationClassBeanDefinitionReader(BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        this.registry = registry;
        this.importBeanNameGenerator = importBeanNameGenerator;
    }

    public void loadBeanDefinitions(Set<ConfigurationClass> configurationModel) {
        for (ConfigurationClass configClass : configurationModel) {
            loadBeanDefinitionsForConfigurationClass(configClass);
        }
    }

    private void loadBeanDefinitionsForConfigurationClass(ConfigurationClass configClass) {
        if (configClass.isImported()) {
            registerBeanDefinitionForImportedConfigurationClass(configClass);
        }
    }

    private void registerBeanDefinitionForImportedConfigurationClass(ConfigurationClass configClass) {
        AnnotationMetadata metadata = configClass.getMetadata();
        AnnotatedGenericBeanDefinition configBeanDef = new AnnotatedGenericBeanDefinition(metadata);

        ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(configBeanDef);
        configBeanDef.setScope(scopeMetadata.getScopeName());
        String configBeanName = this.importBeanNameGenerator.generateBeanName(configBeanDef, this.registry);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(configBeanDef, metadata);

        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(configBeanDef, configBeanName);
        //definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
        this.registry.registerBeanDefinition(definitionHolder.getBeanName(), definitionHolder.getBeanDefinition());
        configClass.setBeanName(configBeanName);
    }
}
