package com.petros.bringframework.context.annotation;

import com.petros.bringframework.JavaConfig;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.ConfigurableBeanFactory;
import com.petros.bringframework.beans.factory.config.BeanFactoryPostProcessor;
import com.petros.bringframework.beans.factory.config.BeanPostProcessor;
import com.petros.bringframework.beans.factory.config.SimpleBeanFactoryPostProcessor;
import com.petros.bringframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import com.petros.bringframework.beans.factory.config.ConfigurationClassPostProcessor;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.factory.support.DefaultBeanFactory;
import com.petros.bringframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import com.petros.bringframework.context.support.AbstractApplicationContext;
import com.petros.bringframework.core.AssertUtils;

import java.util.Arrays;

import java.util.List;

public class AnnotationConfigApplicationContext extends AbstractApplicationContext {
    private final BeanDefinitionRegistry registry;
    private final DefaultBeanFactory beanFactory;
    private final AnnotatedBeanDefinitionReader reader;
    private final SimpleClassPathBeanDefinitionScanner scanner;

    /**
     * Create a new AnnotationConfigApplicationContext that needs to be populated
     * through {@link #register} calls and then manually {@linkplain #refresh refreshed}.
     */
    public AnnotationConfigApplicationContext() {
        super();
        this.registry = new SimpleBeanDefinitionRegistry();
        this.reader = new AnnotatedBeanDefinitionReader(registry);
        this.scanner = new SimpleClassPathBeanDefinitionScanner(registry);
        this.beanFactory = new DefaultBeanFactory(registry);
    }

    /**
     * Create a new AnnotationConfigApplicationContext, deriving bean definitions
     * from the given component classes and automatically refreshing the context.
     * @param componentClasses one or more component classes &mdash; for example,
     * {@link Configuration @Configuration} classes
     */
    public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
        this();
        register(componentClasses);
        refresh();
    }

    /**
     * Create a new AnnotationConfigApplicationContext, scanning for components
     * in the given packages, registering bean definitions for those components,
     * and automatically refreshing the context.
     * @param basePackages the packages to scan for component classes
     */
    public AnnotationConfigApplicationContext(String... basePackages) {
        this();
        scan(basePackages);
        refresh();
    }




    @Override
    protected void initBeansPostProcessors() {
        beanFactory.getBeansOfType(BeanPostProcessor.class)
                .forEach((name, processor) -> beanFactory.configureBeans(processor));
    }

    @Override
    protected void invokeBeanFactoryPostProcessors(BeanFactory beanFactory) {
        AbstractAutowireCapableBeanFactory factory = (AbstractAutowireCapableBeanFactory) beanFactory;
        factory.getBeanFactoryPostProcessors().forEach(factoryPostProcessor ->
                factoryPostProcessor.postProcessBeanFactory(beanFactory));
    }

    @Override
    protected void destroyBeans() {
        beanFactory.destroyBeans();
    }

    @Override
    protected ConfigurableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * Register one or more component classes to be processed.
     * <p>Note that {@link #refresh()} must be called in order for the context
     * to fully process the new classes.
     * @param componentClasses one or more component classes &mdash; for example,
     * {@link Configuration @Configuration} classes
     * @see #scan(String...)
     * @see #refresh()
     */
    public void register(Class<?>... componentClasses) {
        AssertUtils.notEmpty(componentClasses, "At least one component class must be specified");
        this.reader.register(componentClasses);
    }

    public void scan(String... packages) {
        AssertUtils.notEmpty(packages, "At least one package must be specified");
        this.scanner.scan(packages);
    }

    public <T> T getBean(Class<T> type) {
        return beanFactory.getBean(type);
    }
}
