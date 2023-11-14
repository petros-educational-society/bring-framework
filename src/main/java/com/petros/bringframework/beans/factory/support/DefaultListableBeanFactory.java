package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.factory.BeanDefinitionStoreException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.core.AssertUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

@Slf4j
@RequiredArgsConstructor
public class DefaultListableBeanFactory implements BeanFactory {

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private final BeanDefinitionRegistry registry;

    @Override
    public Object getBean(String name) {
        return new Object();
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return getBean(requiredType, (Object[]) null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType, @Nullable Object... args) throws BeansException {
        AssertUtils.notNull(requiredType, "Required type must not be null");
        Object resolved = resolveBean(ResolvableType.forRawClass(requiredType), args, false);
        if (resolved == null) {
            throw new NoSuchBeanDefinitionException(requiredType);
        }
        return (T) resolved;
    }
    @Override
    public boolean containsBean(String name) {
        return false;
    }

    @Override
    public boolean isSingleton(String name) {
        return true;
    }

    @Override
    public boolean isPrototype(String name) {
        return false;
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) {
        return false;
    }

    @Override
    public Class<?> getType(String name) {
        return null;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws BeanDefinitionStoreException {

        AssertUtils.notBlank(beanName, "Bean name must not be empty");
        requireNonNull(beanDefinition, "BeanDefinition must not be null");

        var registeredBean = beanDefinitionMap.put(beanName, beanDefinition);
        if (registeredBean != null) {
            log.info("BeanDefinition with name {} was registered", beanName);
        }
    }


    @Override
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        AssertUtils.notBlank(beanName, "'beanName' must not be empty");

        var bd = this.beanDefinitionMap.remove(beanName);
        if (bd == null) {
            if (log.isTraceEnabled()) {
                log.trace("No bean named '{}' found in {}", beanName, this);
            }
            throw new NoSuchBeanDefinitionException(beanName);
        }
    }

    @Override
    public String[] getAliases(String name) {
        return new String[0];
    }
}
