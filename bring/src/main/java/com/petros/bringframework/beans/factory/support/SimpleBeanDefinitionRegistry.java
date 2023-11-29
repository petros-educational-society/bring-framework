package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.factory.BeanDefinitionStoreException;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

@Log4j2
public class SimpleBeanDefinitionRegistry implements BeanDefinitionRegistry {

    private final Map<String, BeanDefinition> beanDefinitions;

    public SimpleBeanDefinitionRegistry() {
        beanDefinitions = new ConcurrentHashMap<>();
    }

    public Map<String, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        requireNonNull(beanName, "'beanName' is required");
        requireNonNull(beanDefinition, "'beanDefinition' is required");

        if (nonNull(beanDefinitions.put(beanName, beanDefinition))) {
            log.info("BeanDefinition with name {} was registered", beanName);
        }
    }

    @Override
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        if (isNull(beanDefinitions.remove(beanName))) {
            if (log.isTraceEnabled()) {
                log.trace("No bean named '{}' found in {}", beanName, this);
            }
            throw new NoSuchBeanDefinitionException(beanName);
        }
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        requireNonNull(beanName, "'beanName' is required");
        var founded = beanDefinitions.get(beanName);
        if (isNull(founded)) {
            throw new NoSuchBeanDefinitionException(beanName);
        }
        return founded;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        requireNonNull(beanName, "'beanName' is required");
        return beanDefinitions.containsKey(beanName);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitions.keySet().toArray(new String[0]);
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitions.size();
    }

    @Override
    public boolean isBeanNameInUse(String beanName) {
        requireNonNull(beanName, "'beanName' is required");
        return containsBeanDefinition(beanName);
    }

    @Override
    public void registerAlias(String beanName, String alias) {
        throw new RuntimeException("Method not implemented yet");
    }
}
