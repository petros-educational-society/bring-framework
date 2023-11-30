package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.factory.support.AnnotationAttributes;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.support.GenericBeanDefinition;
import com.petros.bringframework.beans.support.ReflectionBeanDefinition;
import com.petros.bringframework.context.annotation.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.petros.bringframework.beans.factory.config.AutowireMode.AUTOWIRE_CONSTRUCTOR;
import static java.util.Objects.nonNull;


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

        for (BeanMethod beanMethod : configClass.getBeanMethods()) {
            loadBeanDefinitionsForBeanMethod(beanMethod);
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

    private void loadBeanDefinitionsForBeanMethod(BeanMethod beanMethod) {
        ConfigurationClass configClass = beanMethod.getConfigurationClass();
        MethodMetadata metadata = beanMethod.getMetadata();
        String methodName = metadata.getMethodName();

        AnnotationAttributes annotationAttributes = AnnotationConfigUtils.attributesFor(metadata, Bean.class);
        if (annotationAttributes == null) {
            throw new IllegalStateException("No @Bean annotation attributes");
        }

        List<String> names = new ArrayList<>(Arrays.asList(annotationAttributes.getStringArray("name")));
        String beanName = (!names.isEmpty() ? names.remove(0) : methodName);

        ConfigurationClassBeanDefinition configBeanDef = new ConfigurationClassBeanDefinition(configClass, metadata, beanName);
        if (metadata.isStatic()) {
            //todo throw exception not supported
            configBeanDef.setBeanClassName(configClass.getMetadata().getClassName());
        } else {
            configBeanDef.setFactoryBeanName(configClass.getBeanName());
        }
        configBeanDef.setFactoryMethodName(methodName);
        configBeanDef.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        configBeanDef.setResolvedFactoryMethod(metadata.getIntrospectedMethod());
        AnnotationConfigUtils.processCommonDefinitionAnnotations(configBeanDef, metadata);

        configBeanDef.setAutowireCandidate(annotationAttributes.getBoolean("autowireCandidate"));
        var initMethodName = annotationAttributes.getString("initMethod");
        if (!initMethodName.isBlank()) {
            configBeanDef.setInitMethodName(initMethodName);
        }
        var destroyMethodName = annotationAttributes.getString("destroyMethod");
        if (!destroyMethodName.isBlank()) {
            configBeanDef.setDestroyMethodName(destroyMethodName);
        }

        ScopedProxyMode proxyMode = ScopedProxyMode.NO;
        AnnotationAttributes scopeAttributes = AnnotationConfigUtils.attributesFor(metadata, Scope.class);
        if (nonNull(scopeAttributes)) {
            configBeanDef.setScope(scopeAttributes.getString("value"));
            proxyMode = scopeAttributes.getEnum("proxyMode");
            if (proxyMode == ScopedProxyMode.DEFAULT) {
                proxyMode = ScopedProxyMode.NO;
            }
        }

        BeanDefinition beanDefToRegister = configBeanDef;
        /*if (proxyMode != ScopedProxyMode.NO) {
            if (proxyMode != ScopedProxyMode.NO) {
                BeanDefinitionHolder proxyDef = ScopedProxyCreator.createScopedProxy(
                        new BeanDefinitionHolder(configBeanDef, beanName), this.registry,
                        proxyMode == ScopedProxyMode.TARGET_CLASS);
                beanDefToRegister = new ConfigurationClassBeanDefinition(
                        (RootBeanDefinition) proxyDef.getBeanDefinition(), configClass, metadata, beanName);
            }
        }*/

        this.registry.registerBeanDefinition(beanName, beanDefToRegister);
    }

    static class ConfigurationClassBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {
        private final AnnotationMetadata annotationMetadata;
        private final MethodMetadata factoryMethodMetadata;
        private final String derivedBeanName;

        private ConfigurationClassBeanDefinition(ConfigurationClass configClass,
                                                 MethodMetadata factoryMethodMetadata, String derivedBeanName) {
            this.annotationMetadata = configClass.getMetadata();
            this.factoryMethodMetadata = factoryMethodMetadata;
            this.derivedBeanName = derivedBeanName;
        }

        @Override
        public AnnotationMetadata getMetadata() {
            return this.annotationMetadata;
        }

        @Nullable
        @Override
        public MethodMetadata getFactoryMethodMetadata() {
            return this.factoryMethodMetadata;
        }
    }
}
