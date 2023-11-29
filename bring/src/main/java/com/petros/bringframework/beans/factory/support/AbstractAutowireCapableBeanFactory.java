package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.BeanFactoryUtils;
import com.petros.bringframework.beans.factory.BeanAware;
import com.petros.bringframework.beans.factory.config.AnnotationBeanPostProcessor;
import com.petros.bringframework.beans.factory.config.AutowireCapableBeanFactory;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanPostProcessor;
import com.petros.bringframework.beans.support.GenericBeanDefinition;
import com.petros.bringframework.context.support.ConstructorResolver;
import com.petros.bringframework.core.type.ResolvableType;
import com.petros.bringframework.util.AutowireClassUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Abstract bean factory superclass that implements default bean creation.
 * Implements the AutowireCapableBeanFactory interface in addition to AbstractBeanFactory's createBean method.
 *
 * @author "Maksym Oliinyk"
 */
@Slf4j
public abstract class AbstractAutowireCapableBeanFactory
        extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    /**
     * Strategy for creating bean instances.
     */
    private InstantiationStrategy instantiationStrategy;

    public AbstractAutowireCapableBeanFactory(BeanDefinitionRegistry registry) {
        super(registry);
        instantiationStrategy = new SimpleInstantiationStrategy();
    }


    /**
     * Central method of this class: creates a bean instance,
     * populates the bean instance, applies post-processors, etc.
     */
    @Override
    protected Object createBean(String beanName, BeanDefinition mbd, @Nullable Object[] args)
            throws BeanCreationException {
        try {
            return doCreateBean(beanName, mbd, args);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, " Unexpected exception during bean creation", ex);
        }
    }

    protected Object doCreateBean(String beanName, BeanDefinition mbd, Object[] args) {
        BeanWrapper instanceWrapper = createBeanInstance(beanName, mbd, args);

        Object bean = instanceWrapper.wrappedInstance();

        invokeAwareMethod(bean);

        configureBean(beanName, bean);

        return bean;
    }

    /**
     * Configures the given bean before and after initialization, applying specific
     * post-processing logic if it's not a BeanPostProcessor itself.
     * It retrieves all post processors that are instances of AnnotationBeanPostProcessor first
     * and allows them to perform postProcessBeforeInitialization on the bean.
     * Following this, the method initializes the bean based on the configured
     * post-processors.
     *
     * @param beanName The name of the bean being configured.
     * @param bean     The instance of the bean to be configured.
     * @throws BeanCreationException if an error occurs during post-processing
     *                              or initialization of the bean.
     */
    private void configureBean(String beanName, Object bean) {
        if (!(bean instanceof BeanPostProcessor)) {
            try {
                List<BeanPostProcessor> annotationBeanPostProcessors = getBeanPostProcessors()
                        .stream()
                        .filter(AnnotationBeanPostProcessor.class::isInstance)
                        .toList();

                annotationBeanPostProcessors.forEach(bp -> bp.postProcessBeforeInitialization(bean, beanName));
                initializeBean(bean, beanName, annotationBeanPostProcessors);
            } catch (Throwable ex) {
                throw new BeanCreationException(beanName, "Post-processing for %s failed".formatted(beanName), ex);
            }
        }
    }

    private void initializeBean(Object bean, String beanName, @Nullable List<BeanPostProcessor> annotationBeanPostProcessors) {
        applyBeanPostProcessorBeforeInitialization(bean, beanName, annotationBeanPostProcessors);
        invokeInitMethod(bean, beanName);
        applyBeanPostProcessorAfterInitialization(bean, beanName, annotationBeanPostProcessors);
    }

    private void applyBeanPostProcessorBeforeInitialization(Object bean, String beanName, @Nullable List<BeanPostProcessor> annotationBeanPostProcessors) {
        var beanPostProcessors = new ArrayList<>(getBeanPostProcessors());
        if (annotationBeanPostProcessors != null) {
            beanPostProcessors.removeAll(annotationBeanPostProcessors);
        }
        beanPostProcessors.forEach(b -> b.postProcessBeforeInitialization(bean, beanName));
    }

    private void applyBeanPostProcessorAfterInitialization(Object bean, String beanName, @Nullable List<BeanPostProcessor> annotationBeanPostProcessors) {
        var beanPostProcessors = new ArrayList<>(getBeanPostProcessors());
        if (annotationBeanPostProcessors != null) {
            beanPostProcessors.removeAll(annotationBeanPostProcessors);
        }
        beanPostProcessors.forEach(b -> b.postProcessAfterInitialization(bean, beanName));
    }

    private void invokeInitMethod(Object bean, String beanName) {
        //todo: implement
    }

    private void invokeAwareMethod(Object bean) {
        if (bean instanceof BeanAware beanAware) {
            beanAware.setBeanFactory(this);
        }
    }

    @Override
    public boolean isTypeMatch(String name, BeanDefinition bd, ResolvableType typeToMatch) {
        final Object beanInstance = getSingleton(name);
        if (beanInstance != null) {
            return typeToMatch.isInstance(beanInstance);
        }
        Class<?> predictedType = predictBeanType(name, bd, typeToMatch);
        return predictedType != null;
    }

    protected Class<?> predictBeanType(String name, BeanDefinition bd, ResolvableType typeToMatch) {
        final Class<?> targetType = resolveBeanClass(bd, name);
        if (targetType != null && typeToMatch.isAssignableFrom(targetType)) {
            return targetType;
        } else {
            return null;
        }
    }


    /**
     * Create a new instance for the specified bean, using an appropriate instantiation strategy:
     * factory method, constructor autowiring, or simple instantiation.
     *
     * @param beanName the name of the bean
     * @param mbd      the bean definition for the bean
     * @param args     explicit arguments to use for constructor or factory method invocation
     * @return a BeanWrapper for the new instance
     * @see #autowireConstructor
     * @see #instantiateBean
     */
    protected BeanWrapper createBeanInstance(String beanName, BeanDefinition mbd, @Nullable Object[] args) {
        // Make sure bean class is actually resolved at this point.
        Class<?> beanClass = resolveBeanClass(mbd, beanName);

        if (!(mbd instanceof GenericBeanDefinition gbd)) {
            throw new BeanCreationException(beanName, "Bean definition isn't GenericBeanDefinition");
        } else {
            if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers())) {
                throw new BeanCreationException(beanName, "Bean class isn't public: " + beanClass.getName());
            }

            if (mbd.getFactoryMethodName() != null) {
                return instantiateUsingFactoryMethod(beanName, (GenericBeanDefinition) mbd, args);
            }

            // Shortcut when re-creating the same bean...
            boolean resolved = false;
            boolean autowireNecessary = false;
            if (args == null) {
                if (gbd.getResolvedConstructor() != null) {
                    resolved = true;
                    autowireNecessary = gbd.isAutowiredConstructorArgumentsResolved();
                }
            }
            if (resolved) {
                Constructor<?>[] ctors = getConstructors(gbd.getResolvedConstructor());
                Object[] argss = getArgss(gbd);
                if (autowireNecessary) {
                    return autowireConstructor(beanName, gbd, ctors, argss);
                } else {
                    return instantiateBean(beanName, gbd, ctors, argss);
                }
            } else {
                Map<Boolean, Constructor<?>> constructors = AutowireClassUtils.determineCandidateConstructors(beanName, gbd);
                Constructor<?> autowiredConstructor = constructors.get(Boolean.TRUE);
                if (autowiredConstructor != null) {
                    return autowireConstructor(beanName, gbd, getConstructors(autowiredConstructor), null);
                }

                Constructor<?> constructor = constructors.get(Boolean.FALSE);
                if (constructor != null) {
                    return instantiateBean(beanName, gbd, getConstructors(constructor), null);
                } else {
                    throw new BeanCreationException(beanName, "Unresolved constructor.");
                }
            }
        }
    }

    private BeanWrapper instantiateUsingFactoryMethod(String beanName, GenericBeanDefinition mbd, Object[] args) {
        return new ConstructorResolver(this).instantiateUsingFactoryMethod(beanName, mbd, args);
    }

    private static Object[] getArgss(GenericBeanDefinition gbd) {
        return gbd.getConstructorArgumentValues().getIndexedArgumentValues().values().stream()
                .map(valueHolder -> (Class<?>) valueHolder.getType())
                .toArray();
    }

    private static Constructor<?>[] getConstructors(Executable resolvedConstructor) {
        return Stream.of(resolvedConstructor).toArray(Constructor<?>[]::new);
    }

    /**
     * Handling the autowiring of constructor parameters. Delegates the actual autowiring process to the autowireConstructor method of {@link ConstructorResolver}.
     * The method is responsible for determining the appropriate constructor to use for the bean based on the provided arguments,
     * available constructors, and the autowiring mode set in the bean definition.
     * It resolves the arguments required for constructor invocation, either using the provided explicit arguments
     * or by autowiring them based on the types defined in the constructor parameters.
     *
     * @param beanName     The name of the bean
     * @param mbd          definition contains metadata
     * @param ctors        constructors of the bean class.
     * @param explicitArgs nn array of objects representing the explicit arguments to be used for constructor invocation.
     * @return {@link BeanWrapper} instance. This wrapper encapsulates the newly created bean instance
     */
    protected BeanWrapper autowireConstructor(
            String beanName, GenericBeanDefinition mbd, Constructor<?>[] ctors, @Nullable Object[] explicitArgs) {
        return new ConstructorResolver(this).autowireConstructor(beanName, mbd, ctors, explicitArgs);
    }

    /**
     * Return the bean name, stripping out the factory dereference prefix if necessary,
     * and resolving aliases to canonical names.
     * @param name the user-specified name
     * @return the transformed bean name
     */
    protected String transformedBeanName(String name) {
        var aliases = getAliases(name);
        return BeanFactoryUtils.transformedBeanName(name);
    }

    /**
     * Instantiate the given bean using its default constructor.
     *
     * @param beanName the name of the bean
     * @param gbd      the bean definition for the bean
     * @return a BeanWrapper for the new instance
     */
    private BeanWrapper instantiateBean(String beanName, GenericBeanDefinition gbd, Constructor<?>[] ctors, @Nullable Object[] explicitArgs) {
        Object instance = instantiationStrategy.instantiate(gbd, beanName, ctors, explicitArgs);
        return new BeanWrapper(instance, instance.getClass());
    }

    public BeanWrapper instantiateBean(GenericBeanDefinition bd, @Nullable String beanName, BeanFactory owner,
                                        @Nullable Object factoryBean, final Method factoryMethod, Object[] args) {
        Object instance = instantiationStrategy.instantiate(bd, beanName, owner, factoryBean, factoryMethod, args);
        return new BeanWrapper(instance, instance.getClass());
    }

}
