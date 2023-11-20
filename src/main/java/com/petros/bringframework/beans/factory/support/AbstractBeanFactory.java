package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.TypeConverter;
import com.petros.bringframework.beans.converter.SympleTypeConverter;
import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.exception.TypeMismatchException;
import com.petros.bringframework.beans.factory.ConfigurableBeanFactory;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.support.AbstractBeanDefinition;
import com.petros.bringframework.core.type.convert.ConversionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;
import java.util.Optional;

import static com.petros.bringframework.util.ClassUtils.getQualifiedName;
import static java.util.Objects.nonNull;

/**
 * @author "Maksym Oliinyk"
 */
@Slf4j
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {

    protected final BeanDefinitionRegistry registry;

    public AbstractBeanFactory(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object getBean(String name) {
        return doGetBean(name, null, null, false);
    }

    /**
     * Return an instance, which may be shared or independent, of the specified bean.
     *
     * @param name          the name of the bean to retrieve ( may include factory dereference prefix )
     * @param requiredType  the required type of the bean to retrieve
     * @param args          arguments to use when creating a bean instance using explicit arguments
     *                      (only applied when creating a new instance as opposed to retrieving an existing one)
     * @param typeCheckOnly whether the instance is obtained for a type check,
     *                      not for actual use
     * @return an instance of the bean
     * @throws BeansException if the bean could not be created
     */
    @SuppressWarnings("unchecked")
    protected <T> T doGetBean(String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
            throws BeansException {

        String beanName = name;
        Object beanInstance = null;

        // Eagerly check singleton cache for manually registered singletons.
        Object sharedInstance = getSingleton(beanName);
        if (sharedInstance != null && args == null) {
            beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, null);
        } else {
            try {
                final BeanDefinition mbd = registry.getBeanDefinition(beanName);
                if (mbd.isAbstract()) {
                    throw new IllegalStateException("Bean definition " + beanName + " is abstract ");
                }

                // Guarantee initialization of beans that the current bean depends on.
                String[] dependsOn = mbd.getDependsOn();
                if (dependsOn != null) {
                    for (String dep : dependsOn) {
                        if (isDependent(beanName, dep)) {
                            throw new BeanCreationException(beanName,
                                    "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
                        }
                        registerDependentBean(dep, beanName);
                        try {
                            getBean(dep);
                        } catch (NoSuchBeanDefinitionException ex) {
                            throw new BeanCreationException(beanName,
                                    "'" + beanName + "' depends on missing bean '" + dep + "'", ex);
                        }
                    }
                }

                beanInstance = createBeanInstance(name, args, mbd, beanName);
            } catch (BeansException ex) {
                throw ex;
            }
        }

        return adaptBeanInstance(name, beanInstance, requiredType);
    }

    /**
     * Create a bean instance for the given bean definition (and arguments).
     * The bean definition will already have been merged with the parent definition
     * in case of a child definition.
     * <p>All bean retrieval methods delegate to this method for actual bean creation.
     *
     * @param beanName the name of the bean
     * @param mbd      the merged bean definition for the bean
     * @param args     explicit arguments to use for constructor or factory method invocation
     * @return a new instance of the bean
     * @throws BeanCreationException if the bean could not be created
     */
    protected abstract Object createBean(String beanName, BeanDefinition mbd, @Nullable Object[] args)
            throws BeanCreationException;


    protected Object createBeanInstance(String name, Object[] args, BeanDefinition mbd, String beanName) {
        Object sharedInstance;
        Object beanInstance = null;
        if (mbd.isSingleton()) {
            sharedInstance = getSingleton(beanName, () -> {
                try {
                    return createBean(beanName, mbd, args);
                } catch (BeansException ex) {
                    destroySingleton(beanName);
                    throw ex;
                }
            });
            beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
        } else if (mbd.isPrototype()) {
            //todo create prototype
        } else {
            String scopeName = mbd.getScope();
            //todo create custom scope
        }
        return beanInstance;
    }

    @SuppressWarnings("unchecked")
    protected <T> T adaptBeanInstance(String name, Object bean, @Nullable Class<?> requiredType) {
        if (nonNull(requiredType) && !requiredType.isInstance(bean)) {
            try {
                return (T) Optional.ofNullable(getTypeConverter().convertIfNecessary(bean, requiredType))
                        .orElseThrow(() -> new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass()));
            } catch (TypeMismatchException ex) {
                if (log.isTraceEnabled()) {
                    log.trace("Failed to convert bean '{}' to required type '{}'", name, getQualifiedName(requiredType), ex);
                }
                throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
            }
        }
        return (T) bean;
    }

    private TypeConverter getTypeConverter() {
        var customConverter = getCustomTypeConverter();
        if (customConverter != null) {
            return customConverter;
        }

        var converter = new SympleTypeConverter();
        converter.setConversionService(getConversionService());

        return converter;
    }
    @Nullable
    protected abstract TypeConverter getCustomTypeConverter();

    @Nullable
    protected abstract ConversionService getConversionService();

    /**
     * Get the object for the given bean instance, either the bean
     * instance itsel
     *
     * @param beanInstance the shared bean instance
     * @param name         the name that may include factory dereference prefix
     * @param beanName     the canonical bean name
     * @param mbd          the bean definition
     * @return the object to expose for the bean
     */
    protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, @Nullable BeanDefinition mbd) {
        return beanInstance;
    }

    /**
     * * Resolve the bean class for the specified bean definition,
     * * resolving a bean class name into a Class reference (if necessary)
     * * and storing the resolved Class in the bean definition for further use.
     *
     * @param mbd      bean definition
     * @param beanName the name of the bean
     * @return
     */
    protected Class<?> resolveBeanClass(BeanDefinition mbd, String beanName) {
        Class<?> beanClass = null;
        if (mbd instanceof AbstractBeanDefinition) {
            beanClass = ((AbstractBeanDefinition) mbd).getBeanClass();
        }
        if (beanClass == null) {
            try {
                beanClass = Class.forName(mbd.getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Couldn't return the class object associated with the bean " + beanName, e);
            }
        }
        return beanClass;
    }
}
