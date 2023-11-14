package com.petros.bringframework.context.support;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.config.BeanFactoryPostProcessor;
import com.petros.bringframework.beans.factory.support.NoSuchBeanDefinitionException;
import com.petros.bringframework.context.ApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Slf4j
public abstract class AbstractApplicationContext implements ApplicationContext {
    /** BeanFactoryPostProcessors to apply on refresh. */
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    private final Object startupShutdownMonitor = new Object();

    /**
     * Create a new AbstractApplicationContext with no parent.
     */
    public AbstractApplicationContext() {

    }


    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        requireNonNull(postProcessor, "BeanFactoryPostProcessor must not be null");
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    /**
     * Return the list of BeanFactoryPostProcessors that will get applied
     * to the internal BeanFactory.
     */
    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    @Override
    public void initApplicationContext() throws BeansException, IllegalStateException {
        synchronized (startupShutdownMonitor) {

            var beanFactory = getBeanFactory();

            // Prepare the bean factory for use in this context.
//            prepareBeanFactory(beanFactory);

            try {
                // Allows post-processing of the bean factory in context subclasses.
                postProcessBeanFactory(beanFactory);

                // Invoke factory processors registered as beans in the context.
                invokeBeanFactoryPostProcessors(beanFactory);

                // Register bean processors that intercept bean creation.
                registerBeanPostProcessors(beanFactory);

            }

            catch (BeansException ex) {
                if (log.isWarnEnabled()) {
                    log.warn("Exception encountered during context initialization - " +
                            "cancelling refresh attempt: " + ex);
                }

                // Destroy already created singletons to avoid dangling resources.
                destroyBeans();

                // Propagate exception to caller.
                throw ex;
            }
        }
    }

    /**
     * Tell the subclass to refresh the internal bean factory.
     * @return the fresh BeanFactory instance
     */
    protected abstract BeanFactory getBeanFactory();

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. The initial definition resources will have been loaded but no
     * post-processors will have run and no derived bean definitions will have been
     * registered, and most importantly, no beans will have been instantiated yet.
     * <p>This template method allows for registering special BeanPostProcessors
     * etc in certain AbstractApplicationContext subclasses.
     * @param beanFactory the bean factory used by the application context
     */
    protected abstract void postProcessBeanFactory(BeanFactory beanFactory);

    /**
     * Instantiate and invoke all registered BeanFactoryPostProcessor beans,
     * respecting explicit order if given.
     * <p>Must be called before singleton instantiation.
     */
    protected abstract void invokeBeanFactoryPostProcessors(BeanFactory beanFactory);

    /**
     * Instantiate and register all BeanPostProcessor beans,
     * respecting explicit order if given.
     * <p>Must be called before any instantiation of application beans.
     */
    protected abstract void registerBeanPostProcessors(BeanFactory beanFactory);

    /**
     * Template method for destroying all beans that this context manages.
     * The default implementation destroy all cached singletons in this context,
     * invoking {@code DisposableBean.destroy()} and/or the specified
     * "destroy-method".
     * <p>Can be overridden to add context-specific bean destruction steps
     * right before or right after standard singleton destruction,
     * while the context's BeanFactory is still active.
     * @see #getBeanFactory()
     */
    protected void destroyBeans() {
        getBeanFactory().destroyBeans();
    }

    //---------------------------------------------------------------------
    // Implementation of BeanFactory interface
    //---------------------------------------------------------------------

    public Object getBean(String name) throws BeansException {
        return getBeanFactory().getBean(name);
    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(requiredType);
    }

    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return getBeanFactory().isTypeMatch(name, typeToMatch);
    }

    @Nullable
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().getType(name);
    }

    public String[] getAliases(String name) {
        return getBeanFactory().getAliases(name);
    }
}

