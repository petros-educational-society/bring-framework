package com.petros.bringframework.beans.support;

import com.petros.bringframework.beans.factory.config.AutowireMode;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanDefinitionRole;
import com.petros.bringframework.beans.factory.config.SimpleConstructorArgumentValues;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.petros.bringframework.beans.factory.config.AutowireMode.*;

/**
 * @author "Maksym Oliinyk"
 */
public abstract class AbstractBeanDefinition implements BeanDefinition {

    private static final String SCOPE_DEFAULT = "";

    private Object beanClass;
    @Nullable
    private String scope = SCOPE_DEFAULT;

    private boolean abstractFlag = false;

    private AutowireMode autowireMode = AutowireMode.AUTOWIRE_NO;
    private boolean autowireCandidate = true;

    private boolean primary = false;

    @Nullable
    private Boolean lazyInit;

    @Nullable
    private String[] dependsOn;

    @Nullable
    @Setter
    @Getter
    private String factoryBeanName;

    @Nullable
    @Setter
    @Getter
    private String factoryMethodName;

    @Nullable
    private SimpleConstructorArgumentValues constructorArgumentValues;
    @Nullable
    private List<PropertyValue> propertyValues;

    @Nullable
    private String[] initMethodNames;

    @Nullable
    private String[] destroyMethodNames;

    @Nullable
    @Setter
    @Getter
    private String description;

    private int role = BeanDefinitionRole.ROLE_APPLICATION.getRole();

    private volatile Method factoryMethodToIntrospect;

    //todo implement qualifiers
    //private final Map<String, AutowireCandidateQualifier> qualifiers = new LinkedHashMap<>();

    @Override
    public void setBeanClassName(@Nullable String beanClassName) {
        this.beanClass = beanClassName;
        resolveBeanClass();
    }

    @Nullable
    @Override
    public String getBeanClassName() {
        Object beanClassObject = this.beanClass;
        return (beanClassObject instanceof Class<?> clazz ? clazz.getName() : (String) beanClassObject);
    }

    @Override
    public void setScope(@Nullable String scope) {
        this.scope = scope;
    }

    @Nullable
    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    @Override
    public boolean isLazyInit() {
        return this.lazyInit != null && this.lazyInit.booleanValue();
    }

    @Override
    public void setDependsOn(@Nullable String... dependsOn) {
        this.dependsOn = dependsOn;
    }

    @Nullable
    @Override
    public String[] getDependsOn() {
        return dependsOn;
    }

    @Override
    public void setAutowireCandidate(boolean autowireCandidate) {
        this.autowireCandidate = autowireCandidate;
    }

    @Override
    public boolean isAutowireCandidate() {
        return autowireCandidate;
    }

    @Override
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public boolean isPrimary() {
        return primary;
    }

    /**
     * Specify constructor argument values for this bean.
     */
    public void setConstructorArgumentValues(SimpleConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues;
    }

    @Override
    public SimpleConstructorArgumentValues getConstructorArgumentValues() {
        if (this.constructorArgumentValues == null) {
            this.constructorArgumentValues = new SimpleConstructorArgumentValues();
        }
        return this.constructorArgumentValues;
    }

    /**
     * Specify property values for this bean, if any.
     */
    public void setPropertyValues(List<PropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }

    /**
     * Return property values for this bean (never {@code null}).
     */
    @Override
    public List<PropertyValue> getPropertyValues() {
        if (this.propertyValues == null) {
            this.propertyValues = new ArrayList<>();
        }
        return this.propertyValues;
    }

    @Override
    public void setInitMethodName(@Nullable String initMethodName) {
        this.initMethodNames = (initMethodName != null ? new String[]{initMethodName} : null);
    }

    @Nullable
    @Override
    public String getInitMethodName() {
        return (this.initMethodNames != null && this.initMethodNames.length > 0 ? this.initMethodNames[0] : null);
    }

    @Override
    public void setDestroyMethodName(@Nullable String destroyMethodName) {
        this.destroyMethodNames = (destroyMethodNames != null ? new String[]{destroyMethodName} : null);
    }

    @Nullable
    @Override
    public String getDestroyMethodName() {
        return (this.destroyMethodNames != null && this.destroyMethodNames.length > 0 ? this.destroyMethodNames[0] : null);
    }

    @Override
    public void setRole(int role) {
        this.role = BeanDefinitionRole.valueOf(role).getRole();
    }

    @Override
    public int getRole() {
        return role;
    }


    @Override
    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(this.scope) || SCOPE_DEFAULT.equals(this.scope);
    }

    @Override
    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(this.scope);
    }

    /**
     * Set if this bean is "abstract", i.e. not meant to be instantiated itself but
     * rather just serving as parent for concrete child bean definitions.
     * <p>Default is "false". Specify true to tell the bean factory to not try to
     * instantiate that particular bean in any case.
     */
    public void setAbstract(boolean abstractFlag) {
        this.abstractFlag = abstractFlag;
    }

    /**
     * Return whether this bean is "abstract", i.e. not meant to be instantiated
     * itself but rather just serving as parent for concrete child bean definitions.
     */
    @Override
    public boolean isAbstract() {
        return this.abstractFlag;
    }

    @Nullable
    @Override
    //todo implement
    public String getResourceDescription() {
        return null;
    }

    @Nullable
    @Override
    //todo implement
    public BeanDefinition getOriginatingBeanDefinition() {
        return null;
    }

    /**
     * Set the autowire mode. This determines whether any automagical detection
     * and setting of bean references will happen. Default is AUTOWIRE_NO
     * which means there won't be convention-based autowiring by name or type
     * (however, there may still be explicit annotation-driven autowiring).
     *
     * @param autowireMode the autowire mode to set.
     *                     Must be one of the constants defined in this class.
     * @see #AUTOWIRE_NO
     * @see #AUTOWIRE_BY_NAME
     * @see #AUTOWIRE_BY_TYPE
     * @see #AUTOWIRE_CONSTRUCTOR
     * @see #AUTOWIRE_AUTODETECT
     */
    public void setAutowireMode(AutowireMode autowireMode) {
        this.autowireMode = autowireMode;
    }

    /**
     * Return the autowire mode as specified in the bean definition.
     */
    public int getAutowireMode() {
        return this.autowireMode.getValue();
    }

    /**
     * Return the resolved autowire code,
     * (resolving AUTOWIRE_AUTODETECT to AUTOWIRE_CONSTRUCTOR or AUTOWIRE_BY_TYPE).
     *
     * @see #AUTOWIRE_AUTODETECT
     * @see #AUTOWIRE_CONSTRUCTOR
     * @see #AUTOWIRE_BY_TYPE
     */
    public AutowireMode getResolvedAutowireMode() {
        if (this.autowireMode == AUTOWIRE_AUTODETECT) {
            // Work out whether to apply setter autowiring or constructor autowiring.
            // If it has a no-arg constructor it's deemed to be setter autowiring,
            // otherwise we'll try constructor autowiring.
            Constructor<?>[] constructors = getBeanClass().getConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    return AUTOWIRE_BY_TYPE;
                }
            }
            return AUTOWIRE_CONSTRUCTOR;
        } else {
            return this.autowireMode;
        }
    }

    @Nullable
    public Class<?> resolveBeanClass() {
        String className = getBeanClassName();
        if (className == null) {
            return null;
        }
        Class<?> resolvedClass = null;
        try {
            resolvedClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't resolve class by name", e);
        }
        this.beanClass = resolvedClass;
        return resolvedClass;
    }

    public Class<?> getBeanClass() {
        Object beanClassObject = this.beanClass;
        if (beanClassObject == null) {
            throw new IllegalStateException("No bean class specified on bean definition");
        }
        if (!(beanClassObject instanceof Class<?> introspectedClass)) {
            throw new IllegalStateException(
                    "Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
        }
        return introspectedClass;
    }

    @Override
    public void setFactoryBeanName(@Nullable String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    @Override
    @Nullable
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    /**
     * Set a resolved Java Method for the factory method on this bean definition.
     * @param method the resolved factory method, or {@code null} to reset it
     * @since 5.2
     */
    public void setResolvedFactoryMethod(@Nullable Method method) {
        this.factoryMethodToIntrospect = method;
    }

    /**
     * Return the resolved factory method as a Java Method object, if available.
     * @return the factory method, or {@code null} if not found or not resolved yet
     */
    @Nullable
    public Method getResolvedFactoryMethod() {
        return this.factoryMethodToIntrospect;
    }
}
