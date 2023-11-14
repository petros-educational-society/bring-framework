package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.BeanDefinitionStoreException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.factory.support.DefaultBeanFactory;
import com.petros.bringframework.beans.factory.support.NoSuchBeanDefinitionException;
import com.petros.bringframework.context.support.AbstractApplicationContext;
import com.petros.bringframework.core.AssertUtils;
import lombok.SneakyThrows;

public class AnnotationConfigApplicationContext extends AbstractApplicationContext {

    private final SimpleClassPathBeanDefinitionScanner scanner;
    private final DefaultBeanFactory beanFactory;

    public AnnotationConfigApplicationContext(BeanDefinitionRegistry registry, String... packages) {
        super();
        this.beanFactory = new DefaultBeanFactory(registry);
        this.scanner = new SimpleClassPathBeanDefinitionScanner(registry);
        scan(packages);
    }

    @Override
    protected void postProcessBeanFactory(BeanFactory beanFactory) {

    }

    @Override
    protected void invokeBeanFactoryPostProcessors(BeanFactory beanFactory) {

    }

    @Override
    protected void registerBeanPostProcessors(BeanFactory beanFactory) {

    }

    @Override
    public Object getBean(String name) {
        throw new RuntimeException("There is no implementation");
    }

    @SneakyThrows
    @Override
    public <T> T getBean(Class<T> type) {
//        if (beanCache.containsKey(type)) {
//            return type.cast(beanCache.get(type));
//        }
//        var implClass = resolveImpl(type);
//        var obj = factory.createObject(implClass);
//        if (implClass.isAnnotationPresent(Singleton.class)) {
//            beanCache.put(type.getName(), obj);
//        }
//        return obj;
        throw new UnsupportedOperationException("There is no implementation");
    }

    @Override
    protected BeanFactory getBeanFactory() {
        return beanFactory;
    }

    private <T> Class<T> resolveImpl(Class<T> type) {
//        return type.isInterface() ? (Class<T>) config.getImplClass(type) : type;
        throw new RuntimeException("There is no implementation");
    }

    public void scan(String... packages) {
        AssertUtils.notEmpty(packages, "At least one package must be specified");
        this.scanner.scan(packages);
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws BeanDefinitionStoreException {

        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        this.beanFactory.removeBeanDefinition(beanName);
    }
}
