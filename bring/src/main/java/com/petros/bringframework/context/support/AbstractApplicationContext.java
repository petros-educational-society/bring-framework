package com.petros.bringframework.context.support;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.ConfigurableBeanFactory;
import com.petros.bringframework.beans.factory.config.BeanPostProcessor;
import com.petros.bringframework.beans.factory.config.ConfigurationClassPostProcessor;
import com.petros.bringframework.beans.factory.config.SimpleBeanFactoryPostProcessor;
import com.petros.bringframework.context.ConfigurableApplicationContext;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;

/**
 * Abstract base class implementing the {@link ConfigurableApplicationContext} interface,
 * providing common functionality for concrete application context implementations.
 * This class manages the lifecycle of an application context, including initialization,
 * configuration, and resource management.
 * <p>
 * The class defines core methods for initializing and refreshing the application context,
 * registering shutdown hooks, and managing the lifecycle of beans within the context.
 * <p>
 * This class employs synchronization mechanisms to ensure thread-safe startup and shutdown
 * operations of the context. It also integrates with the JVM shutdown process to perform
 * clean-up activities upon JVM termination.
 * <p>
 * Subclasses of this abstract class are responsible for implementing specific behavior
 * related to obtaining and managing the underlying bean factory, as well as handling the
 * lifecycle phases of the application context.
 *
 * @see ConfigurableApplicationContext
 * @author "Viktor Basanets"
 * @author "Maksym Oliinyk"
 * @author "Vasiuk Maryna"
 */
@Log4j2
public abstract class AbstractApplicationContext implements ConfigurableApplicationContext {

    private final Object startupShutdownMonitor = new Object();

    /** Reference to the JVM shutdown hook, if registered. */
    @Nullable
    private Thread shutdownHook;

    /**
     * {@link Thread#getName() Name} of the {@linkplain #registerShutdownHook()
     * shutdown hook} thread: {@value}.
     * @see #registerShutdownHook()
     */
    String SHUTDOWN_HOOK_THREAD_NAME = "BringContextShutdownHook";

    @Override
    public void init()
            throws BeansException, IllegalStateException {
        synchronized (startupShutdownMonitor) {
            try {
                refresh();
            } catch (BeansException ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Exception encountered during context initialization - cancelling refresh attempt: {}", ex.getMessage(), ex);
                }
                destroyBeans();
                throw ex;
            }
        }
    }

    /**
     * Perform a fresh start-up of the context, initializing and configuring the underlying BeanFactory.
     * Prepare the bean factory for use in this context.
     * Allows post-processing of the bean factory in context subclasses.
     * Invoke factory processors registered as beans in the context.
     * Register bean processors that intercept bean creation.
     * Instantiate all remaining (non-lazy-init) singletons.
     * Destroy already created singletons to avoid dangling resources.
     * @throws BeansException if initialization fails
     * @throws IllegalStateException if the context has already been refreshed
     */
    @Override
    public void refresh() throws BeansException, IllegalStateException {
        ConfigurableBeanFactory beanFactory = obtainFreshBeanFactory();

        prepareBeanFactory(beanFactory);

        try {
            postProcessBeanFactory(beanFactory);

            invokeBeanFactoryPostProcessors(beanFactory);

            registerBeanPostProcessors(beanFactory);

            finishBeanFactoryInitialization(beanFactory);

        } catch (BeansException ex) {
            if (log.isDebugEnabled()) {
                log.debug("Exception encountered during context initialization - " +
                        "cancelling refresh attempt: " + ex);
            }

            destroyBeans();

            throw ex;
        } finally {
            resetCommonCaches();
        }
    }

    /**
     * Register a shutdown hook {@linkplain Thread#getName() named}
     * {@code BringContextShutdownHook} with the JVM runtime, closing this
     * context on JVM shutdown unless it has already been closed at that time.
     * <p>Delegates to {@code doClose()} for the actual closing procedure.
     */
    @Override
    public void registerShutdownHook() {
        if (this.shutdownHook == null) {
            // No shutdown hook registered yet.
            this.shutdownHook = new Thread(SHUTDOWN_HOOK_THREAD_NAME) {
                @Override
                public void run() {
                    synchronized (startupShutdownMonitor) {
                        doClose();
                    }
                }
            };
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }

    /**
     * Actually performs context closing: invokes postProcessBeforeDestruction methods and
     * destroys the singletons in the bean factory of this application context.
     * @see #registerShutdownHook()
     */
    protected void doClose() {
        postProcessBeforeDistraction();

        destroyBeans();
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
     * Configure the factory's standard context characteristics, such as factory-post-processors
     *
     * @param beanFactory the bean factory used by the application context
     */
    protected void prepareBeanFactory(final ConfigurableBeanFactory beanFactory) {
        beanFactory.addBeanFactoryPostProcessor(new ConfigurationClassPostProcessor());
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
     * Instantiate and invoke all registered BeanFactoryPostProcessor beans,
     * respecting explicit order if given.
     * <p>Must be called before singleton instantiation.
     */
    protected abstract void invokeBeanFactoryPostProcessors(BeanFactory beanFactory);

    /**
     * Destroy all beans in the application context, releasing their resources.
     */
    protected abstract void destroyBeans();

    /**
     * Perform post-processing before destruction of the context.
     */
    protected abstract void postProcessBeforeDistraction();

}
