package com.petros.bringframework.context.support;

import com.petros.bringframework.beans.BeanException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.config.BeanFactoryPostProcessor;
import com.petros.bringframework.beans.factory.config.BeanPostProcessor;
import com.petros.bringframework.context.ApplicationContext;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultApplicationContext implements ApplicationContext {
    private Map<String, Object> beanCache;
    private Map<String, ? extends BeanFactoryPostProcessor> beanFactoryPostProcessors;
    private Map<String, ? extends BeanPostProcessor> beanPostProcessors;

    private BeanFactory beanFactory;
//    @Getter
//    private Reflections scaner;
//    private BeanDefinitionRegistry registry;
//    private Config config;
//    private ObjectFactory factory;

    public DefaultApplicationContext(String packageToScan, Map<Class, Class> ifc2impl) {
//        registry = new SimpleBeanDefinitionRegistry();
//        scaner = new Reflections(packageToScan);
//        scaner = new SimplePathScanBeanDefinitionScaner(registry);
//        config = new JavaConfig(scaner, ifc2impl);
//        factory = new ObjectFactory(this);
        this.beanCache = new ConcurrentHashMap<>();
        this.beanFactoryPostProcessors = this.getBeansOfType(BeanFactoryPostProcessor.class);
        this.beanPostProcessors = this.getBeansOfType(BeanPostProcessor.class);
    }

    private <T> void configureBeans(T t) {
        beanPostProcessors.forEach((key, value) -> value.postProcessBeforeInitialization(t, key));
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
        throw new RuntimeException("There is no implementation");
    }

    @Override
    public boolean containsBean(String name) {
        throw new RuntimeException("There is no implementation");
    }

    @Override
    public boolean isSingleton(String name) {
        throw new RuntimeException("There is no implementation");
    }

    @Override
    public boolean isPrototype(String name) {
        throw new RuntimeException("There is no implementation");
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) {
        throw new RuntimeException("There is no implementation");
    }

    @Override
    public Class<?> getType(String name) {
        throw new RuntimeException("There is no implementation");
    }

    @Override
    public String[] getAliases(String name) {
        throw new RuntimeException("There is no implementation");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeanException {
        return beanFactory.getBeansOfType(type);
    }


    private <T> Class<T> resolveImpl(Class<T> type) {
//        return type.isInterface() ? (Class<T>) config.getImplClass(type) : type;
        throw new RuntimeException("There is no implementation");
    }
}
