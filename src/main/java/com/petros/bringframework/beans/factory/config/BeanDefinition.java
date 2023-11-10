package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.support.PropertyValue;

import javax.annotation.Nullable;
import java.util.List;

public interface BeanDefinition {

    /**
     * Scope identifier for the standard singleton scope: {@value}.
     */
    String SCOPE_SINGLETON = "singleton";

    /**
     * Scope identifier for the standard prototype scope: {@value}.
     */
    String SCOPE_PROTOTYPE = "prototype";

    /**
     * Set the name of the parent definition of this bean definition, if any.
     */
    void setParentName(@Nullable String parentName);

    /**
     * Return the name of the parent definition of this bean definition, if any.
     */
    @Nullable
    String getParentName();

    /**
     * Specify the bean class name of this bean definition.
     * <p>The class name can be modified during bean factory post-processing,
     * typically replacing the original class name with a parsed variant of it.
     *
     * @see #setParentName
     * @see #setFactoryBeanName
     * @see #setFactoryMethodName
     */
    void setBeanClassName(@Nullable String beanClassName);

    /**
     * Return the current bean class name of this bean definition.
     * <p>Note that this does not have to be the actual class name used at runtime, in
     * case of a child definition overriding/inheriting the class name from its parent.
     * Also, this may just be the class that a factory method is called on, or it may
     * even be empty in case of a factory bean reference that a method is called on.
     * Hence, do <i>not</i> consider this to be the definitive bean type at runtime but
     * rather only use it for parsing purposes at the individual bean definition level.
     *
     * @see #getParentName()
     * @see #getFactoryBeanName()
     * @see #getFactoryMethodName()
     */
    @Nullable
    String getBeanClassName();

    /**
     * Override the target scope of this bean, specifying a new scope name.
     *
     * @see #SCOPE_SINGLETON
     * @see #SCOPE_PROTOTYPE
     */
    void setScope(@Nullable String scope);

    /**
     * Return the name of the current target scope for this bean,
     * or {@code null} if not known yet.
     */
    @Nullable
    String getScope();

    /**
     * Set whether this bean should be lazily initialized.
     * <p>If {@code false}, the bean will get instantiated on startup by bean
     * factories that perform eager initialization of singletons.
     */
    void setLazyInit(boolean lazyInit);

    /**
     * Return whether this bean should be lazily initialized, i.e. not
     * eagerly instantiated on startup. Only applicable to a singleton bean.
     */
    boolean isLazyInit();

    /**
     * Set the names of the beans that this bean depends on being initialized.
     * The bean factory will guarantee that these beans get initialized first.
     */
    void setDependsOn(@Nullable String... dependsOn);

    /**
     * Return the bean names that this bean depends on.
     */
    @Nullable
    String[] getDependsOn();

    /**
     * Set whether this bean is a candidate for getting autowired into some other bean.
     * <p>Note that this flag is designed to only affect type-based autowiring.
     * It does not affect explicit references by name, which will get resolved even
     * if the specified bean is not marked as an autowire candidate. As a consequence,
     * autowiring by name will nevertheless inject a bean if the name matches.
     */
    void setAutowireCandidate(boolean autowireCandidate);

    /**
     * Return whether this bean is a candidate for getting autowired into some other bean.
     */
    boolean isAutowireCandidate();

    /**
     * Set whether this bean is a primary autowire candidate.
     * <p>If this value is {@code true} for exactly one bean among multiple
     * matching candidates, it will serve as a tie-breaker.
     */
    void setPrimary(boolean primary);

    /**
     * Return whether this bean is a primary autowire candidate.
     */
    boolean isPrimary();

    /**
     * Specify the factory bean to use, if any.
     * This the name of the bean to call the specified factory method on.
     *
     * @see #setFactoryMethodName
     */
    void setFactoryBeanName(@Nullable String factoryBeanName);

    /**
     * Return the factory bean name, if any.
     */
    @Nullable
    String getFactoryBeanName();

    /**
     * Specify a factory method, if any. This method will be invoked with
     * constructor arguments, or with no arguments if none are specified.
     * The method will be invoked on the specified factory bean, if any,
     * or otherwise as a static method on the local bean class.
     *
     * @see #setFactoryBeanName
     * @see #setBeanClassName
     */
    void setFactoryMethodName(@Nullable String factoryMethodName);

    /**
     * Return a factory method, if any.
     */
    @Nullable
    String getFactoryMethodName();

    /**
     * Return the constructor argument values for this bean.
     * <p>The returned instance can be modified during bean factory post-processing.
     *
     * @return the ConstructorArgumentValues object (never {@code null})
     */
    SimpleConstructorArgumentValues getConstructorArgumentValues();

    /**
     * Return if there are constructor argument values defined for this bean.
     *
     * @since 5.0.2
     */
    default boolean hasConstructorArgumentValues() {
        return !getConstructorArgumentValues().isEmpty();
    }

    /**
     * Return the property values to be applied to a new instance of the bean.
     * <p>The returned instance can be modified during bean factory post-processing.
     *
     * @return the List<PropertyValue> object (never {@code null})
     */
     List<PropertyValue> getPropertyValues();

    /**
     * Return if there are property values defined for this bean.
     *
     */
    default boolean hasPropertyValues() {
        return !getPropertyValues().isEmpty();
    }

    /**
     * Set the name of the initializer method.
     *
     * @since 5.1
     */
    void setInitMethodName(@Nullable String initMethodName);

    /**
     * Return the name of the initializer method.
     */
    @Nullable
    String getInitMethodName();

    /**
     * Set the name of the destroy method.
     *
     * @since 5.1
     */
    void setDestroyMethodName(@Nullable String destroyMethodName);

    /**
     * Return the name of the destroy method.
     *
     * @since 5.1
     */
    @Nullable
    String getDestroyMethodName();

    /**
     * Set the role hint for this {@code BeanDefinition}. The role hint
     * provides the frameworks as well as tools an indication of
     * the role and importance of a particular {@code BeanDefinition}.
     *
     * @see #ROLE_APPLICATION
     * @see #ROLE_SUPPORT
     * @see #ROLE_INFRASTRUCTURE
     * @since 5.1
     */
    void setRole(int role);

    /**
     * Get the role hint for this {@code BeanDefinition}. The role hint
     * provides the frameworks as well as tools an indication of
     * the role and importance of a particular {@code BeanDefinition}.
     *
     * @see #ROLE_APPLICATION
     * @see #ROLE_SUPPORT
     * @see #ROLE_INFRASTRUCTURE
     */
    int getRole();

    /**
     * Set a human-readable description of this bean definition.
     */
    void setDescription(@Nullable String description);

    /**
     * Return a human-readable description of this bean definition.
     */
    @Nullable
    String getDescription();


    // Read-only attributes

    /**
     * Return whether this a <b>Singleton</b>, with a single, shared instance
     * returned on all calls.
     *
     * @see #SCOPE_SINGLETON
     */
    boolean isSingleton();

    /**
     * Return whether this a <b>Prototype</b>, with an independent instance
     * returned for each call.
     *
     * @see #SCOPE_PROTOTYPE
     */
    boolean isPrototype();

    /**
     * Return whether this bean is "abstract", that is, not meant to be instantiated.
     */
    boolean isAbstract();

    /**
     * Return a description of the resource that this bean definition
     * came from (for the purpose of showing context in case of errors).
     */
    @Nullable
    String getResourceDescription();

    /**
     * Return the originating BeanDefinition, or {@code null} if none.
     * <p>Allows for retrieving the decorated bean definition, if any.
     * <p>Note that this method returns the immediate originator. Iterate through the
     * originator chain to find the original BeanDefinition as defined by the user.
     */
    @Nullable
    BeanDefinition getOriginatingBeanDefinition();
}
