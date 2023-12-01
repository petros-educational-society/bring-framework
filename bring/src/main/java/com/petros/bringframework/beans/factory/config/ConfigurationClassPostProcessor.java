package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.factory.BeanAware;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.support.AbstractBeanDefinition;
import com.petros.bringframework.context.annotation.Configuration;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import javax.annotation.Nullable;
import java.util.*;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * BeanFactoryPostProcessor used for bootstrapping processing of @Configuration classes.
 *
 * @author "Vasiuk Maryna"
 * @author "Maksym Oliinyk"
 */
@Log4j2
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private final Set<Integer> registriesPostProcessed = new HashSet<>();

    private final Set<Integer> factoriesPostProcessed = new HashSet<>();

    public ConfigurationClassPostProcessor() {
    }

    @Nullable
    private ConfigurationClassBeanDefinitionReader reader;

    /**
     * Build and validate a configuration model based on the registry of
     * {@link Configuration} classes.
     */
    public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
        List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
        String[] candidateNames = registry.getBeanDefinitionNames();

        for (String beanName : candidateNames) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (hasConfigurationAnnotation(beanDef)) {
                configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
            }
        }

        if (configCandidates.isEmpty()) {
            return;
        }

        // Parse each @Configuration class
        ConfigurationClassParser parser = new ConfigurationClassParser(registry);

        Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
        Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());
        do {
            parser.parse(candidates);

            Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
            configClasses.removeAll(alreadyParsed);

            // Read the model and create bean definitions based on its content
            if (this.reader == null) {
                this.reader = new ConfigurationClassBeanDefinitionReader(registry);
            }
            this.reader.loadBeanDefinitions(configClasses);
            alreadyParsed.addAll(configClasses);

            candidates.clear();
            if (registry.getBeanDefinitionCount() > candidateNames.length) {
                String[] newCandidateNames = registry.getBeanDefinitionNames();
                Set<String> oldCandidateNames = new HashSet<>(Arrays.asList(candidateNames));
                Set<String> alreadyParsedClasses = new HashSet<>();
                for (ConfigurationClass configurationClass : alreadyParsed) {
                    alreadyParsedClasses.add(configurationClass.getMetadata().getClassName());
                }
                for (String candidateName : newCandidateNames) {
                    if (!oldCandidateNames.contains(candidateName)) {
                        BeanDefinition bd = registry.getBeanDefinition(candidateName);
                        if (!alreadyParsedClasses.contains(bd.getBeanClassName())) {
                            candidates.add(new BeanDefinitionHolder(bd, candidateName));
                        }
                    }
                }
                candidateNames = newCandidateNames;
            }
        }
        while (!candidates.isEmpty());
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        int registryId = System.identityHashCode(registry);
        if (this.registriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException(
                    "postProcessBeanDefinitionRegistry already called on this post-processor against " + registry);
        }
        if (this.factoriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException(
                    "postProcessBeanFactory already called on this post-processor against " + registry);
        }
        this.registriesPostProcessed.add(registryId);

        processConfigBeanDefinitions(registry);
    }

    @Override
    public void postProcessBeanFactory(BeanFactory beanFactory) {
        postProcessBeanDefinitionRegistry(beanFactory.getBeanDefinitionRegistry());
        enhanceConfigurationClasses(beanFactory);
    }

    private boolean hasConfigurationAnnotation(BeanDefinition beanDef) {
        if (beanDef instanceof AnnotatedBeanDefinition) {
            AnnotationMetadata metadata = ((AnnotatedBeanDefinition) beanDef).getMetadata();
            return metadata.isAnnotated(Configuration.class.getName());
        }
        return false;
    }

    private void enhanceConfigurationClasses(BeanFactory beanFactory) {
        Map<String, AbstractBeanDefinition> configBeanDefs = new LinkedHashMap<>();
        BeanDefinitionRegistry registry = beanFactory.getBeanDefinitionRegistry();
        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition bd = registry.getBeanDefinition(beanName);
            AnnotationMetadata annotationMetadata = null;
            MethodMetadata methodMetadata = null;
            if (bd instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
                annotationMetadata = annotatedBeanDefinition.getMetadata();
                methodMetadata = annotatedBeanDefinition.getFactoryMethodMetadata();
            }
            if (annotationMetadata.isAnnotated(Configuration.class.getName()) && methodMetadata == null) {
                if (bd instanceof AbstractBeanDefinition abd) {
                    configBeanDefs.put(beanName, abd);
                }
            }
        }
        if (configBeanDefs.isEmpty()) {
            return;
        }

        for (Map.Entry<String, AbstractBeanDefinition> entry : configBeanDefs.entrySet()) {
            AbstractBeanDefinition beanDef = entry.getValue();
            Class<?> configClass = beanDef.getBeanClass();


            Class<?> dynamicType = createProxy(configClass);
            beanDef.setBeanClassName(dynamicType.getName());
        }
    }


    /**
     * Creates a dynamic proxy for a given configuration class. This method leverages the ByteBuddy library
     * to dynamically subclass the specified class and intercept its method calls.
     * Always register more specific method matchers last. Otherwise, any less specific method matcher that
     * is registered afterwards might prevent rules that you defined before from being applied.
     * <p>
     * The proxy creation involves several key steps:
     * - Subclassing the given configClass using ByteBuddy.
     * - Interception of public methods not declared by the BeanAware interface.
     * - Redirecting the intercepted method calls to a BeanMethodInterceptor.
     * - Defining a private field 'beanFactory' of type BeanFactory in the subclass.
     * - Implementing the BeanAware interface to allow the proxy to be aware of bean lifecycle events.
     * - Intercepting the BeanAware interface's methods to delegate to the 'beanFactory' field.
     * - Loading the created subclass into the same class loader as the configClass, using the default injection strategy.
     *
     * @param configClass The class to be proxied. This class is expected to be a configuration class.
     * @return A dynamic proxy of the given class, enhanced with additional behavior as defined.
     */
    private Class<?> createProxy(Class<?> configClass) {
        return new ByteBuddy()
                .subclass(configClass)
                .method(
                        ElementMatchers.isPublic()
                                .and(not(isDeclaredBy(BeanAware.class))
                                        .and(not(isDeclaredBy(Object.class))))
                ).intercept(MethodDelegation.to(BeanMethodInterceptor.class))
                .defineField("beanFactory", BeanFactory.class, Visibility.PRIVATE)
                .implement(BeanAware.class).intercept(FieldAccessor.ofBeanProperty())
                .make()
                .load(configClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
    }
}
