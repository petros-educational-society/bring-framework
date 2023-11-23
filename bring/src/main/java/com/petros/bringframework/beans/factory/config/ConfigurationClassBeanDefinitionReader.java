package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.support.ReflectionBeanDefinition;
import com.petros.bringframework.context.annotation.AnnotationConfigUtils;
import com.petros.bringframework.context.annotation.AnnotationScopeMetadataResolver;
import com.petros.bringframework.context.annotation.ScopeMetadata;
import com.petros.bringframework.context.annotation.ScopeMetadataResolver;

import java.util.Set;

public class ConfigurationClassBeanDefinitionReader {

    private static final ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
    private final BeanDefinitionRegistry registry;

    public ConfigurationClassBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void loadBeanDefinitions(Set<ConfigurationClass> configurationModel) {
        for (ConfigurationClass configClass : configurationModel) {
            loadBeanDefinitionsForConfigurationClass(configClass);
        }
    }

    private void loadBeanDefinitionsForConfigurationClass(ConfigurationClass configClass) {
        if (configClass.isImported()) {
            //todo remove imported functionality or add it
            registerBeanDefinitionForImportedConfigurationClass(configClass);
        }
    }

    private void registerBeanDefinitionForImportedConfigurationClass(ConfigurationClass configClass) {
        AnnotationMetadata metadata = configClass.getMetadata();
        ReflectionBeanDefinition configBeanDef = new ReflectionBeanDefinition(metadata);

        ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(configBeanDef);
        configBeanDef.setScope(scopeMetadata.getScopeName());
        String configBeanName = configBeanDef.getBeanClassName();
        AnnotationConfigUtils.processCommonDefinitionAnnotations(configBeanDef, metadata);

        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(configBeanDef, configBeanName);
        //definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
        this.registry.registerBeanDefinition(definitionHolder.getBeanName(), definitionHolder.getBeanDefinition());
        configClass.setBeanName(configBeanName);
    }
}
