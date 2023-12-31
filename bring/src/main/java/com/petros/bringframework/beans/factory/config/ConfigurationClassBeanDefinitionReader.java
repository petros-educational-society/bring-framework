package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.factory.support.AnnotationAttributes;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.support.GenericBeanDefinition;
import com.petros.bringframework.beans.support.ReflectionBeanDefinition;
import com.petros.bringframework.context.annotation.*;
import com.petros.bringframework.core.type.ResolvableType;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.petros.bringframework.beans.factory.config.AutowireMode.AUTOWIRE_CONSTRUCTOR;
import static java.util.Objects.nonNull;

/**
 * A class responsible for reading and processing configuration class bean definitions.
 * This class provides methods to load and register bean definitions based on configuration classes.
 * It uses the provided {@link BeanDefinitionRegistry} to register the bean definitions.
 *
 * @author "Maksym Oliinyk"
 * @author "Vadym Vovk"
 */
@Log4j2
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
        final Class<?> targetType = predictBeanTargetType(metadata);


        AnnotationAttributes annotationAttributes = AnnotationConfigUtils.attributesFor(metadata, Bean.class);
        if (annotationAttributes == null) {
            throw new IllegalStateException("No @Bean annotation attributes");
        }

        List<String> names = new ArrayList<>(Arrays.asList(annotationAttributes.getStringArray("name")));
        String beanName = (!names.isEmpty() ? names.remove(0) : methodName);

        ConfigurationClassBeanDefinition configBeanDef = new ConfigurationClassBeanDefinition(configClass, metadata, beanName);
        if (metadata.isStatic()) {
            log.error("Static @Bean method {} for class {} not supported", methodName, configClass.getMetadata().getClassName());
            throw new UnsupportedOperationException("Static @Bean method not supported");
        } else {
            configBeanDef.setFactoryBeanName(configClass.getBeanName());
        }
        configBeanDef.setFactoryMethodName(methodName);
        configBeanDef.setTargetType(targetType);
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

    private Class<?> predictBeanTargetType(MethodMetadata metadata) {
        return Optional.ofNullable(metadata.getIntrospectedMethod()).map(Method::getReturnType).orElse(null);
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
