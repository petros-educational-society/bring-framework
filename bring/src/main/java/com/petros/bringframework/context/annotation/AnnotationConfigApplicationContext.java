package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.ConfigurableBeanFactory;
import com.petros.bringframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.factory.support.DefaultBeanFactory;
import com.petros.bringframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import com.petros.bringframework.context.support.AbstractApplicationContext;
import com.petros.bringframework.core.AssertUtils;

/**
 * {@code AnnotationConfigApplicationContext} is a concrete implementation of
 * {@link AbstractApplicationContext}, providing an application context that can
 * be configured using annotation-based configuration.
 *
 * <p>This context allows for standalone usage where classes can be registered
 * one by one using {@link #register(Class...)} as well as classpath scanning using
 * {@link #scan(String...)}, facilitating the automatic registration and initialization
 * of beans.
 *
 * <p>It can be used for programmatically configuring the context, including but
 * not limited to, registering component classes and scanning packages for bean definitions.
 *
 * @see #register(Class...)
 * @see #scan(String...)
 * @see BeanDefinitionRegistry
 * @see DefaultBeanFactory
 * @see AnnotatedBeanDefinitionReader
 * @see SimpleClassPathBeanDefinitionScanner
 *
 * @author "Viktor Basanets"
 * @author "Maksym Oliinyk"
 * @author "Oleksii Skachkov"
 */
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
        registerShutdownHook();
    }

    /**
     * Create a new AnnotationConfigApplicationContext, deriving bean definitions
     * from the given component classes and automatically refreshing the context.
     *
     * @param componentClasses one or more component classes &mdash; for example,
     *                         {@link Configuration @Configuration} classes
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
     *
     * @param basePackages the packages to scan for component classes
     */
    public AnnotationConfigApplicationContext(String... basePackages) {
        this();
        scan(basePackages);
        refresh();
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
    protected void postProcessBeforeDistraction() {
        beanFactory.postProcessBeforeDistraction();
    }

    @Override
    protected ConfigurableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * Register one or more component classes to be processed.
     * <p>Note that {@link #refresh()} must be called in order for the context
     * to fully process the new classes.
     *
     * @param componentClasses one or more component classes &mdash; for example,
     *                         {@link Configuration} classes
     * @see #scan(String...)
     * @see #refresh()
     */
    public void register(Class<?>... componentClasses) {
        AssertUtils.notEmpty(componentClasses, "At least one component class must be specified");
        this.reader.register(componentClasses);
    }


    /**
     * Scans the specified packages for component classes, registering bean definitions
     * for discovered components.
     *
     * @param packages the packages to scan for component classes
     */
    public void scan(String... packages) {
        AssertUtils.notEmpty(packages, "At least one package must be specified");
        this.scanner.scan(packages);
    }

    public <T> T getBean(Class<T> type) {
        return beanFactory.getBean(type);
    }
}
