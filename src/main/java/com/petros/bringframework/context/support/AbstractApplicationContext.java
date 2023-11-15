package com.petros.bringframework.context.support;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.factory.config.BeanFactoryPostProcessor;
import com.petros.bringframework.context.ApplicationContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Slf4j
public abstract class AbstractApplicationContext implements ApplicationContext {
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    private final Object startupShutdownMonitor = new Object();

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        requireNonNull(postProcessor, "BeanFactoryPostProcessor must not be null");
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    @Override
    public void init() throws BeansException, IllegalStateException {
        synchronized (startupShutdownMonitor) {
            try {
                createBeansFromDefinitions();
//                invokeBeanFactoryPostProcessors();
//                initBeansPostProcessors();
            } catch (BeansException ex) {
                if (log.isWarnEnabled()) {
                    log.warn("Exception encountered during context initialization - cancelling refresh attempt: {}", ex.getMessage(), ex);
                }
                destroyBeans();
                throw ex;
            }
        }
    }

    /**
     * Tell the subclass to refresh the internal bean factory.
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
     * @param beanFactory the bean factory used by the application context
     */
    protected abstract void initBeansPostProcessors();

    /**
     * Instantiate and invoke all registered BeanFactoryPostProcessor beans,
     * respecting explicit order if given.
     * <p>Must be called before singleton instantiation.
     */
    protected abstract void invokeBeanFactoryPostProcessors();

    protected abstract void destroyBeans();
}
