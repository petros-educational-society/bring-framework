package com.petros.bringframework.context.support;

import com.petros.bringframework.context.ApplicationContext;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultApplicationContext implements ApplicationContext {
    private Map<String, Object> beanCache;
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
        beanCache = new ConcurrentHashMap<>();
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

    private <T> Class<T> resolveImpl(Class<T> type) {
//        return type.isInterface() ? (Class<T>) config.getImplClass(type) : type;
        throw new RuntimeException("There is no implementation");
    }
}
