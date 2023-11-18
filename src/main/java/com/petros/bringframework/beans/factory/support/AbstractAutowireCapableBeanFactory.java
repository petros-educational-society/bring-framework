package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.factory.config.AutowireCapableBeanFactory;
import com.petros.bringframework.beans.factory.config.AutowireMode;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.support.GenericBeanDefinition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static com.petros.bringframework.util.AutowireClassUtils.determineCandidateConstructors;

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
        Class<?> beanType = instanceWrapper.wrappedClass();
        //todo implement bean post processors
        return bean;
    }


    /**
     * Create a new instance for the specified bean, using an appropriate instantiation strategy:
     * factory method, constructor autowiring, or simple instantiation.
     *
     * @param beanName the name of the bean
     * @param mbd      the bean definition for the bean
     * @param args     explicit arguments to use for constructor or factory method invocation
     * @return a BeanWrapper for the new instance
     * @see #obtainFromSupplier
     * @see #instantiateUsingFactoryMethod
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

            // Shortcut when re-creating the same bean...
            boolean resolved = false;
            boolean autowireNecessary = false;
            if (args == null) {
                if (gbd.getResolvedConstructor() != null) {
                    resolved = true;
                    autowireNecessary = gbd.isConstructorArgumentsResolved();
                }
            }
            if (resolved) {
                if (autowireNecessary) {
                    return autowireConstructor(beanName, gbd, null, null);
                } else {
                    return instantiateBean(beanName, gbd);
                }
            }

            // Candidate constructors for autowiring?


            Constructor<?>[] ctors = determineCandidateConstructors(beanClass, beanName);
            if (ctors != null || gbd.getResolvedAutowireMode() == AutowireMode.AUTOWIRE_CONSTRUCTOR ||
                    gbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
                return autowireConstructor(beanName, gbd, ctors, args);
            }

            // Preferred constructors for default construction?
//            ctors = gbd.getPreferredConstructors();
//            if (ctors != null) {
//                return autowireConstructor(beanName, gbd, ctors, null);
//            }

            // No special handling: simply use no-arg constructor.
            return instantiateBean(beanName, gbd);
        }
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
            String beanName, GenericBeanDefinition mbd, @Nullable Constructor<?>[] ctors, @Nullable Object[] explicitArgs) {

        return null;
//        return new ConstructorResolver(this).autowireConstructor(beanName, mbd, ctors, explicitArgs);
    }

    /**
     * Instantiate the given bean using its default constructor.
     *
     * @param beanName the name of the bean
     * @param mbd      the bean definition for the bean
     * @return a BeanWrapper for the new instance
     */
    private BeanWrapper instantiateBean(String beanName, GenericBeanDefinition gbd) {
        Object instance = instantiationStrategy.instantiate(gbd, beanName);
        return new BeanWrapper(instance, instance.getClass());
    }

}
