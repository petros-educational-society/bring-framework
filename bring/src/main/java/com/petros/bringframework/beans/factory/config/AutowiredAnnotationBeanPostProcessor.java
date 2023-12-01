package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.factory.BeanAware;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import com.petros.bringframework.beans.factory.support.NoSuchBeanDefinitionException;
import com.petros.bringframework.beans.factory.support.NoUniqueBeanDefinitionException;
import com.petros.bringframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * {@link com.petros.bringframework.beans.factory.config.BeanPostProcessor BeanPostProcessor}
 * implementation that enables autowiring capabilities and property injection through annotations:
 * {@link com.petros.bringframework.beans.factory.annotation.InjectPlease @InjectPlease}
 * and {@link Value @Value}.
 *
 * @see AnnotationBeanPostProcessor
 * @see BeanAware
 * @author "Vasiuk Maryna"
 */
@Log4j2
public class AutowiredAnnotationBeanPostProcessor implements AnnotationBeanPostProcessor, BeanAware {

    private BeanFactory beanFactory;
    private Map<String, String> propertiesMap;

    public AutowiredAnnotationBeanPostProcessor() {}

    /**
     * Reads properties from the {@code application.properties} file and injects them into fields
     * annotated with {@code @Value}.
     * <p>
     * This method handles {@code FileNotFoundException} if the properties file is not found.
     *
     * @return a {@code Map} containing the properties read from the file
     * @throws IllegalStateException if the properties file cannot be accessed or read
     */
    private Map<String, String> readProperties() {
        String path = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource("application.properties")).getPath();
        Stream<String> lines = null;
        try {
            lines = new BufferedReader(new FileReader(path)).lines();
        } catch (FileNotFoundException e) {
            log.debug("File not found: {}", path, e);
        }
        propertiesMap = lines.map(line -> line.split("=")).collect(toMap(arr -> arr[0], arr -> arr[1]));
        return propertiesMap;
    }

    /**
     * Handles autowiring of fields annotated with {@code @InjectPlease} and injects
     * autowired candidates into these fields.
     * <p>
     * Additionally, handles property injection based on the {@code @Value} annotation,
     * injecting properties retrieved from the {@code application.properties} file.
     *
     * @param bean the bean instance to be processed
     * @return the processed bean instance with autowired fields and properties injected
     */
    private Object postProcessPropertyValues(Object bean) {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(InjectPlease.class)) {
                field.setAccessible(true);
                Object object = findAutowireCandidate(field.getType());
                try {
                    field.set(bean, object);
                } catch (IllegalAccessException e) {
                    log.debug("IllegalAccessException occurred while setting field: {}", field.getName(), e);
                }
            }
            else if (field.isAnnotationPresent(Value.class)) {
                Value annotation = field.getAnnotation(Value.class);
                propertiesMap = readProperties();
                String value = propertiesMap.get(annotation.value());
                field.setAccessible(true);
                try {
                    field.set(bean, value);
                } catch (IllegalAccessException e) {
                    log.debug("IllegalAccessException occurred while setting field: {}", field.getName(), e);
                }
            }
        }
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(InjectPlease.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                for (Class<?> parameterType: parameterTypes) {
                    Object beanOfParameterType = findAutowireCandidate(parameterType);
                    method.setAccessible(true);
                    try {
                        method.invoke(bean, beanOfParameterType);
                    } catch (IllegalAccessException e) {
                        log.debug("IllegalAccessException occurred while invoking method: {}", method.getName(), e);
                    } catch (InvocationTargetException e) {
                        log.debug("InvocationTargetException occurred while invoking method: {}", method.getName(), e);
                    }
                }
            }
        }

        return bean;
    }

    /**
     * Performs post-processing before bean initialization, handling autowiring
     * and property injection based on annotations like {@code @InjectPlease} and {@code @Value}.
     *
     * @param bean     the bean instance to be processed
     * @param beanName the name of the bean
     * @return the processed bean instance with autowired fields and properties injected
     */
    @Nullable
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return postProcessPropertyValues(bean);
    }

    /**
     * Finds an autowire candidate of the specified type within the bean factory.
     * <p>
     * This method retrieves candidates from the bean factory and handles scenarios
     * where no candidate or multiple candidates are found.
     *
     * @param type the type of the autowire candidate
     * @return an instance of the autowire candidate
     * @throws NoSuchBeanDefinitionException  if no candidate is found
     * @throws NoUniqueBeanDefinitionException if multiple candidates are found
     */
    protected <T> T findAutowireCandidate(Class<T> type) throws BeansException {
        Map<String, T> candidates = new LinkedHashMap<>(4);
        candidates.putAll(beanFactory.getBeansOfType(type));

        if (candidates.isEmpty()) {
            throw new NoSuchBeanDefinitionException(type.getName());
        } else if (candidates.size() > 1) {
            throw new NoUniqueBeanDefinitionException(candidates.keySet());
        }

        return candidates.values().iterator().next();
    }

    /**
     * Sets the bean factory for this post-processor, allowing access to the bean factory
     *
     * @param beanFactory the bean factory associated with this post-processor
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
