package com.petros.bringframework.context.support;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.ConfigurableBeanFactory;
import com.petros.bringframework.beans.factory.config.BeanPostProcessor;
import com.petros.bringframework.beans.factory.config.SimpleBeanFactoryPostProcessor;
import com.petros.bringframework.context.ConfigurableApplicationContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractApplicationContext implements ConfigurableApplicationContext {

    private final Object startupShutdownMonitor = new Object();

    @Override
    public void init()
            throws BeansException, IllegalStateException {
        synchronized (startupShutdownMonitor) {
            try {
                createBeansFromDefinitions();
            } catch (BeansException ex) {
                if (log.isWarnEnabled()) {
                    log.warn("Exception encountered during context initialization - cancelling refresh attempt: {}", ex.getMessage(), ex);
                }
                destroyBeans();
                throw ex;
            }
        }
    }

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        ConfigurableBeanFactory beanFactory = obtainFreshBeanFactory();

        // Prepare the bean factory for use in this context.
        prepareBeanFactory(beanFactory);

        try {
            // Allows post-processing of the bean factory in context subclasses.
            postProcessBeanFactory(beanFactory);

            // Invoke factory processors registered as beans in the context.
            invokeBeanFactoryPostProcessors(beanFactory);

            // Register bean processors that intercept bean creation.
            registerBeanPostProcessors(beanFactory);

            // Instantiate all remaining (non-lazy-init) singletons.
            finishBeanFactoryInitialization(beanFactory);

        } catch (BeansException ex) {
            if (log.isWarnEnabled()) {
                log.warn("Exception encountered during context initialization - " +
                         "cancelling refresh attempt: " + ex);
            }

            // Destroy already created singletons to avoid dangling resources.
            destroyBeans();

            // Reset 'active' flag.
            //            cancelRefresh(ex);

            // Propagate exception to caller.
            throw ex;
        } finally {
            // Reset common introspection caches, since we
            // might not ever need metadata for singleton beans anymore...
            resetCommonCaches();
        }
    }

    /**
     * Finish the initialization of this context's bean factory, initializing all remaining singleton beans.
     *
     * @param beanFactory the bean factory used by the application context
     */
    protected void finishBeanFactoryInitialization(ConfigurableBeanFactory beanFactory) {
        // Instantiate all remaining (non-lazy-init) singletons.
        beanFactory.preInstantiateSingletons();
    }

    /**
     * Reset common reflection metadata caches, in particular the
     * {@link ReflectionUtils}, {@link AnnotationUtils} and {@link ResolvableType} caches.
     */
    protected void resetCommonCaches() {
        //todo inplement
    }

    /**
     * Instantiate and register all BeanPostProcessor beans, respecting explicit order if given.
     * Must be called before any instantiation of application beans.
     *
     * @param beanFactory the bean factory used by the application context
     */
    protected void registerBeanPostProcessors(ConfigurableBeanFactory beanFactory) {
        beanFactory.getBeansOfType(BeanPostProcessor.class).values()
                   .forEach(beanFactory::addBeanPostProcessor);
    }

    /**
     * Configure the factory's standard context characteristics, such as post-processors
     *
     * @param beanFactory the bean factory used by the application context
     */
    protected void prepareBeanFactory(final ConfigurableBeanFactory beanFactory) {
        // For subclasses: do nothing by default.
        beanFactory.addBeanFactoryPostProcessor(new SimpleBeanFactoryPostProcessor(beanFactory));
    }

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. The initial definition resources will have been loaded but no
     * post-processors will have run and no derived bean definitions will have been
     * registered, and most importantly, no beans will have been instantiated yet.
     * <p>This template method allows for registering special BeanPostProcessors
     * etc in certain AbstractApplicationContext subclasses.
     *
     * @param beanFactory the bean factory used by the application context
     */
    protected void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) {
        // For subclasses: do nothing by default.
    }

    protected ConfigurableBeanFactory obtainFreshBeanFactory() {
        return getBeanFactory();
    }

    /**
     * Return the single internal BeanFactory held by context implementation
     *
     * @return beanFactory the bean factory used by the application context
     */
    protected abstract ConfigurableBeanFactory getBeanFactory();

    /**
     * Tell the subclass to refresh the internal bean factory.
     *
     * @return the fresh BeanFactory instance
     */
    protected abstract void createBeansFromDefinitions();

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. The initial definition resources will have been loaded but no
     * post-processors will have run and no derived bean definitions will have been
     * registered, and most importantly, no beans will have been instantiated yet.
     * <p>This template method allows for registering special BeanPostProcessors
     * etc in certain AbstractApplicationContext subclasses.
     *
     * @param beanFactory the bean factory used by the application context
     */
    protected abstract void initBeansPostProcessors();

    /**
     * Instantiate and invoke all registered BeanFactoryPostProcessor beans,
     * respecting explicit order if given.
     * <p>Must be called before singleton instantiation.
     */
    protected abstract void invokeBeanFactoryPostProcessors(BeanFactory beanFactory);

    protected abstract void destroyBeans();

}
