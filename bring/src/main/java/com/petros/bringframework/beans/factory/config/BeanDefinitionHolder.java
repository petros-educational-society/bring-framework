package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.core.AssertUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nullable;

/**
 * Holder for a BeanDefinition with name and aliases.
 * Can be registered as a placeholder for an inner bean.
 *
 * <p>Can also be used for programmatic registration of inner bean
 * definitions.
 *
 * @see BeanDefinition
 * @author @author "Maksym Oliinyk"
 */
@EqualsAndHashCode
@ToString
@Getter
public class BeanDefinitionHolder {
    private final BeanDefinition beanDefinition;

    private final String beanName;

    @Nullable
    private final String[] aliases;

    /**
     * Create a new BeanDefinitionHolder.
     *
     * @param beanDefinition the BeanDefinition to wrap
     * @param beanName       the name of the bean, as specified for the bean definition
     */
    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
        this(beanDefinition, beanName, null);
    }

    /**
     * Create a new BeanDefinitionHolder.
     *
     * @param beanDefinition the BeanDefinition to wrap
     * @param beanName       the name of the bean, as specified for the bean definition
     * @param aliases        alias names for the bean, or {@code null} if none
     */
    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName, @Nullable String[] aliases) {
        AssertUtils.notNull(beanDefinition, "BeanDefinition must not be null");
        AssertUtils.notNull(beanName, "Bean name must not be null");
        this.beanDefinition = beanDefinition;
        this.beanName = beanName;
        this.aliases = aliases;
    }

}
