package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.factory.BeanDefinitionStoreException;
import com.petros.bringframework.beans.factory.support.AnnotationAttributes;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.support.AbstractBeanDefinition;
import com.petros.bringframework.beans.support.GenericBeanDefinition;
import com.petros.bringframework.context.annotation.AnnotationConfigUtils;
import com.petros.bringframework.context.annotation.Bean;
import com.petros.bringframework.context.annotation.BeanMethod;
import com.petros.bringframework.context.annotation.ComponentScan;
import com.petros.bringframework.context.annotation.ComponentScanAnnotationParser;
import com.petros.bringframework.type.reading.MetadataReader;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Parses a Configuration class definition, populating a collection of ConfigurationClass objects (parsing a single Configuration class may result in any number of ConfigurationClass objects because one Configuration class may import another using the Import annotation).
 * This class helps separate the concern of parsing the structure of a Configuration class from the concern of registering BeanDefinition objects based on the content of that model (with the exception of @ComponentScan annotations which need to be registered immediately).
 *
 * @author "Maksym Oliinyk"
 * @author "Vasiuk Maryna"
 */
public class ConfigurationClassParser {
    private final ComponentScanAnnotationParser componentScanParser;
    private final BeanDefinitionRegistry registry;
    private final Map<ConfigurationClass, ConfigurationClass> configurationClasses = new LinkedHashMap<>();
    private final Map<String, ConfigurationClass> knownSuperclasses = new HashMap<>();

    public ConfigurationClassParser(BeanDefinitionRegistry registry) {
        this.registry = registry;
        this.componentScanParser = new ComponentScanAnnotationParser(registry);
    }

    public void parse(Set<BeanDefinitionHolder> configCandidates) {
        for (BeanDefinitionHolder holder : configCandidates) {
            BeanDefinition bd = holder.getBeanDefinition();
            try {
                if (bd instanceof AbstractBeanDefinition abd) {
                    if (null != abd.getFactoryMethodName() ) {
                        ConfigurationClassBeanDefinitionReader.ConfigurationClassBeanDefinition cbd = (ConfigurationClassBeanDefinitionReader.ConfigurationClassBeanDefinition) bd;
                        parse(cbd.getMetadata(), holder.getBeanName());
                    } else {
                        parse(abd.getBeanClass(), holder.getBeanName());
                    }
                } else {
                    throw new NotImplementedException("Not implemented, should be AbstractBeanDefinition");
                }
            } catch (BeanDefinitionStoreException ex) {
                throw ex;
            } catch (Throwable ex) {
                throw new BeanDefinitionStoreException(
                        "Failed to parse configuration class [" + bd.getBeanClassName() + "]", ex);
            }
        }
    }

    protected final void parse(Class<?> clazz, String beanName) throws IOException {
        processConfigurationClass(new ConfigurationClass(clazz, beanName));
    }
    protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
        processConfigurationClass(new ConfigurationClass(metadata, beanName));
    }

    protected void processConfigurationClass(ConfigurationClass configClass) throws IOException {
        ConfigurationClass existingClass = this.configurationClasses.get(configClass);
        if (existingClass != null) {
            if (configClass.isImported()) {
                if (existingClass.isImported()) {
                    existingClass.mergeImportedBy(configClass);
                }
                return;
            } else {
                this.configurationClasses.remove(configClass);
                this.knownSuperclasses.values().removeIf(configClass::equals);
            }
        }

        SourceClass sourceClass = asSourceClass(configClass);
        do {
            sourceClass = doProcessConfigurationClass(configClass, sourceClass);
        }
        while (sourceClass != null);

        this.configurationClasses.put(configClass, configClass);
    }

    @Nullable
    protected final SourceClass doProcessConfigurationClass(
            ConfigurationClass configClass, SourceClass sourceClass)
            throws IOException {
        Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
                sourceClass.getMetadata(), ComponentScan.class.getName());

        if (!componentScans.isEmpty()) {
            for (AnnotationAttributes componentScan : componentScans) {
                Set<BeanDefinitionHolder> scannedBeanDefinitions =
                        this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());
                //todo delete this ???
//                for (BeanDefinitionHolder holder : scannedBeanDefinitions) {
//                    BeanDefinition bdCand = holder.getBeanDefinition().getOriginatingBeanDefinition();
//                    if (bdCand == null) {
//                        bdCand = holder.getBeanDefinition();
//                    }
//                    parse(bdCand.getBeanClassName(), holder.getBeanName());
//                }
            }
        }

        //Process @Bean methods
        var beanMethods = retrieveBeanMethodMetadata(sourceClass);
        beanMethods.forEach(methodMetadata -> configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass)));

        //Do we need this logic
        if (sourceClass.getMetadata().hasSuperClass()) {
            String superclass = sourceClass.getMetadata().getSuperClassName();
            if (superclass != null && !superclass.startsWith("java") &&
                    !this.knownSuperclasses.containsKey(superclass)) {
                this.knownSuperclasses.put(superclass, configClass);
                return sourceClass.getSuperClass();
            }
        }

        // No superclass -> processing is complete
        return null;
    }

    private Set<MethodMetadata> retrieveBeanMethodMetadata(SourceClass sourceClass) {
        var metadata = sourceClass.getMetadata();
        return metadata.getAnnotatedMethods(Bean.class.getName());
    }

    public Set<ConfigurationClass> getConfigurationClasses() {
        return this.configurationClasses.keySet();
    }

    /**
     * Simple wrapper that allows annotated source classes to be dealt with
     * in a uniform manner, regardless of how they are loaded.
     */
    private class SourceClass {

        private final Object source;  // Class or MetadataReader

        private final AnnotationMetadata metadata;

        public SourceClass(Object source) {
            this.source = source;
            if (source instanceof Class) {
                this.metadata = new ReflectionAnnotationMetadata((Class<?>) source);
            } else {
                this.metadata = ((MetadataReader) source).getAnnotationMetadata();
            }
        }

        public final AnnotationMetadata getMetadata() {
            return this.metadata;
        }

        public SourceClass getSuperClass() throws IOException {
            return asSourceClass(((Class<?>) this.source).getSuperclass());
        }

        public Set<SourceClass> getAnnotations() {
            Set<SourceClass> result = new LinkedHashSet<>();
            Class<?> sourceClass = (Class<?>) this.source;
            for (Annotation ann : sourceClass.getDeclaredAnnotations()) {
                Class<?> annType = ann.annotationType();
                if (!annType.getName().startsWith("java")) {
                    try {
                        result.add(asSourceClass(annType));
                    } catch (Throwable ex) {
                        // An annotation not present on the classpath is being ignored by the JVM's class loading -> ignore here as well.
                    }
                }
            }
            return result;
        }

        @Override
        public boolean equals(@Nullable Object other) {
            return (this == other || (other instanceof SourceClass &&
                    this.metadata.getClassName().equals(((SourceClass) other).metadata.getClassName())));
        }

        @Override
        public int hashCode() {
            return this.metadata.getClassName().hashCode();
        }

        @Override
        public String toString() {
            return this.metadata.getClassName();
        }
    }

    private SourceClass asSourceClass(ConfigurationClass configurationClass) throws IOException {
        AnnotationMetadata metadata = configurationClass.getMetadata();
        return asSourceClass(((ReflectionAnnotationMetadata) metadata).getIntrospectedClass());
    }

    /**
     * Factory method to obtain a {@link SourceClass} from a {@link Class}.
     */
    SourceClass asSourceClass(Class<?> classType) throws IOException {
        try {
            for (Annotation ann : classType.getDeclaredAnnotations()) {
                AnnotationConfigUtils.validateAnnotation(ann);
            }
            return new SourceClass(classType);
        } catch (Throwable ex) {
//            return asSourceClass(classType.getName());
            return null;
        }
    }

    /**
     * Factory method to obtain a {@link SourceClass} from a class name.
     */
//    SourceClass asSourceClass(String className) throws IOException {
//        if (className.startsWith("java")) {
//            try {
//                return new SourceClass(ClassUtils.forName(className, ClassLoader.getSystemClassLoader()));
//            } catch (ClassNotFoundException ex) {
//                throw new IOException("Failed to load class [" + className + "]", ex);
//            }
//        }
//        return new SourceClass(reader);
//    }
}
