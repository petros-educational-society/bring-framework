package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.BeanException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import com.petros.bringframework.beans.factory.support.NoSuchBeanDefinitionException;
import com.petros.bringframework.beans.factory.support.NoUniqueBeanDefinitionException;
import com.petros.bringframework.context.annotation.Value;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * {@link com.petros.bringframework.beans.factory.config.BeanPostProcessor BeanPostProcessor}
 * implementation that autowires annotated fields, setter methods, and arbitrary
 * config methods. Such members to be injected are detected through annotations:
 * {@link com.petros.bringframework.beans.factory.annotation.InjectPlease @InjectPlease}
 * and {@link com.petros.bringframework.context.annotation.Value @Value}.
 *
 * @author "Vasiuk Maryna"
 */
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final BeanFactory beanFactory;
    private Map<String, String> propertiesMap;

    public AutowiredAnnotationBeanPostProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @SneakyThrows
    private Map<String, String> readProperties() {
        String path = ClassLoader.getSystemClassLoader().getResource("application.properties").getPath();
        Stream<String> lines = new BufferedReader(new FileReader(path)).lines();
        propertiesMap = lines.map(line -> line.split("=")).collect(toMap(arr -> arr[0], arr -> arr[1]));
        return propertiesMap;
    }

    @SneakyThrows
    private Object postProcessPropertyValues(Object bean) {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(InjectPlease.class)) {
                field.setAccessible(true);
                Object object = findAutowireCandidate(field.getType());
                field.set(bean, object);
            }
            else if (field.getAnnotation(Value.class) != null) {
                Value annotation = field.getAnnotation(Value.class);
                propertiesMap = readProperties();
                String value = annotation.value().isEmpty() ? propertiesMap.get(field.getName()) : propertiesMap.get(annotation.value());
                field.setAccessible(true);
                field.set(bean, value);
            }
        }
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(InjectPlease.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                for (Class<?> parameterType: parameterTypes) {
                    Object beanOfParameterType = findAutowireCandidate(parameterType);
                    method.setAccessible(true);
                    method.invoke(bean, beanOfParameterType);
                }
            }
        }

        return bean;
    }

    @Nullable
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return postProcessPropertyValues(bean);
    }

    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    protected <T> T findAutowireCandidate(Class<T> type) throws BeanException {
        Map<String, T> candidates = new LinkedHashMap<>(4);
        candidates.putAll(beanFactory.getBeansOfType(type));

        if (candidates.isEmpty()) {
            throw new NoSuchBeanDefinitionException(type.getName());
        } else if (candidates.size() > 1) {
            throw new NoUniqueBeanDefinitionException(candidates.keySet());
        }

        return candidates.values().iterator().next();
    }

}
