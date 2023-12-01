package com.petros.bringframework.beans.factory;

import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.core.type.ResolvableType;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * The root interface for accessing a Bring container.
 * e.g. in case of incomplete or contradictory bean metadata.
 *
 * @author "Viktor Basanets"
 * @author "Vadym Vovk"
 * @author "Oleksii Skachkov"
 */
public interface BeanFactory {

    String FACTORY_BEAN_PREFIX = "&";

    Object getBean(String name);

    <T> T getBean(Class<T> requiredType);

    boolean containsBean(String name);

    boolean isSingleton(String name);

    boolean isPrototype(String name);

    boolean isTypeMatch(String name, BeanDefinition beanDefinition, ResolvableType typeToMatchh);

    Class<?> getType(String name);

    void destroyBeans();

    void postProcessBeforeDistraction();

    String[] getAliases(String name);

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> type);

    BeanDefinitionRegistry getBeanDefinitionRegistry();
}
