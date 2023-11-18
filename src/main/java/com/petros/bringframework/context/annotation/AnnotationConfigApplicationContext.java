package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.ConfigurableBeanFactory;
import com.petros.bringframework.beans.factory.config.BeanPostProcessor;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.factory.support.DefaultBeanFactory;
import com.petros.bringframework.context.support.AbstractApplicationContext;
import com.petros.bringframework.core.AssertUtils;

public class AnnotationConfigApplicationContext extends AbstractApplicationContext {

    private final SimpleClassPathBeanDefinitionScanner scanner;
    private final DefaultBeanFactory beanFactory;

    public AnnotationConfigApplicationContext(BeanDefinitionRegistry registry, String... packages) {
        super();
        this.scanner = new SimpleClassPathBeanDefinitionScanner(registry);
        scan(packages);
        this.beanFactory = new DefaultBeanFactory(registry);
        init();
    }

    @Override
    protected void initBeansPostProcessors() {
        beanFactory.getBeansOfType(BeanPostProcessor.class)
                .forEach((name, processor) -> beanFactory.configureBeans(processor));
    }

    @Override
    protected void invokeBeanFactoryPostProcessors(BeanFactory beanFactory) {

    }

    @Override
    protected void destroyBeans() {
        beanFactory.destroyBeans();
    }

    @Override
    protected ConfigurableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    protected void createBeansFromDefinitions() {
        refresh();
    }

    public void scan(String... packages) {
        AssertUtils.notEmpty(packages, "At least one package must be specified");
        this.scanner.scan(packages);
    }

    public <T> T getBean(Class<T> type) {
        return beanFactory.getBean(type);
    }
}
