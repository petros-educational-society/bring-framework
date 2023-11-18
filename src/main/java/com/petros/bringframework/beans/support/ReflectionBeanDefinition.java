package com.petros.bringframework.beans.support;

import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import com.petros.bringframework.beans.factory.config.AnnotatedBeanDefinition;
import com.petros.bringframework.beans.factory.config.AnnotationMetadata;
import com.petros.bringframework.beans.factory.config.MethodMetadata;
import com.petros.bringframework.beans.factory.config.SimpleConstructorArgumentValues;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.type.reading.ReflectionMetadataReader;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReflectionBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

    private final AnnotationMetadata metadata;

    /**
     * Create a new ScannedGenericBeanDefinition for the class that the
     * given MetadataReader describes.
     *
     * @param metadataReader the MetadataReader for the scanned target class
     */
    public ReflectionBeanDefinition(ReflectionMetadataReader metadataReader) {
        AssertUtils.notNull(metadataReader, "MetadataReader must not be null");
        this.metadata = metadataReader.getAnnotationMetadata();
        setBeanClassName(this.metadata.getClassName());
        initConstructorArgumentValues(metadataReader.getIntrospectedClass());
//		setResource(metadataReader.getResource());
    }


    @Override
    public final AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    @Nullable
    public MethodMetadata getFactoryMethodMetadata() {
        return null;
    }

    private void initConstructorArgumentValues(final Class<?> beanClass) {
        final SimpleConstructorArgumentValues argumentValues = new SimpleConstructorArgumentValues();
        final Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        if (declaredConstructors.length == 1) {
            final Constructor<?> constructor = declaredConstructors[0];
            addConstructorParameters(constructor.getParameters(), constructor.getParameterCount(), argumentValues);
        } else {
            Arrays.stream(declaredConstructors)
                    .filter(constructor -> constructor.isAnnotationPresent(InjectPlease.class))
                    .findFirst()
                    .ifPresentOrElse(constructor -> {
                        addConstructorParameters(constructor.getParameters(), constructor.getParameterCount(), argumentValues);
                    }, () -> {
                        final Map<Integer, List<Constructor<?>>> constructorsMap
                                = Arrays.stream(declaredConstructors).collect(Collectors.groupingBy(t -> t.getParameterCount()));
                        if (!constructorsMap.containsKey(0)) {
                            Integer maxKey = Collections.max(constructorsMap.keySet(), Integer::compareTo);
                            constructorsMap.get(maxKey).stream().findFirst().ifPresent(constructor -> {
                                addConstructorParameters(constructor.getParameters(), constructor.getParameterCount(), argumentValues);
                            });
                        }
                    });
        }
        setConstructorArgumentValues(argumentValues);
    }

    private void addConstructorParameters(Parameter[] constructorParameters, int parametersCount, SimpleConstructorArgumentValues argumentValues) {
        final Parameter[] parameters = constructorParameters;
        IntStream.range(0, parametersCount)
                .forEach(i -> {
                    argumentValues.addIndexedArgumentValue(i, parameters[i]);
                    argumentValues.addGenericArgumentValue(parameters[i]);
                });
    }
}