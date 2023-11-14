package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.factory.BeanDefinitionStoreException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanFactoryPostProcessor;
import com.petros.bringframework.beans.factory.config.BeanPostProcessor;
import com.petros.bringframework.core.AssertUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.petros.bringframework.core.AssertUtils.notBlank;
import static java.util.Objects.requireNonNull;

@Slf4j
public class DefaultBeanFactory implements BeanFactory {

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private final Map<String, Object> beanCacheByName = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> beanCacheByType = new ConcurrentHashMap<>();
    private final Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessors = new ConcurrentHashMap<>();
    private final Map<String, BeanPostProcessor> beanPostProcessors = new ConcurrentHashMap<>();
    private final Map<Class<?>, String[]> allBeanNamesByType = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap<>(16);

    private final BeanDefinitionRegistry registry;

    public DefaultBeanFactory(BeanDefinitionRegistry registry) {
        this.registry = registry;
        this.beanFactoryPostProcessors.putAll(getBeansOfType(BeanFactoryPostProcessor.class));
        this.beanPostProcessors.putAll(getBeansOfType(BeanPostProcessor.class));
    }

    @Override
    public boolean containsBean(String name) {
        return beanCacheByName.containsKey(name);
    }

    private <T> void configureBeans(T t) {
        beanPostProcessors.forEach((key, value) -> value.postProcessBeforeInitialization(t, key));
    }

    public void createBeansFromDefinitions() {
        registry.getBeanDefinitions().forEach((beanName, bd) -> {
            Object bean = createBean(bd);
            beanCacheByName.put(beanName, bean);
        });
    }

    @Override
    public Object getBean(String name) {
        return beanCacheByName.get(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        List<Object> beans = beanCacheByName.values().stream()
                .filter(bean -> requiredType.equals(bean.getClass()))
                .toList();
        if (beans.size() > 1) {
            throw new BeanCreationException(requiredType, "Class has more than one beans.");
        }
        return (T) beans.get(0);
    }

    @Override
    public boolean isSingleton(String name) {
        return true;
    }

    @Override
    public boolean isPrototype(String name) {
        return false;
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) {
        Class<?> type = getType(name);
        return typeToMatch.equals(type);
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        notBlank(beanName, "Bean name must not be empty");
        requireNonNull(beanDefinition, "BeanDefinition must not be null");
        registry.registerBeanDefinition(beanName, beanDefinition);
    }


    @Override
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        notBlank(beanName, "'beanName' must not be empty");
        registry.removeBeanDefinition(beanName);
    }

    @Override
    public void destroyBeans() {
        beanCacheByName.clear();
    }

    @Override
    public Class<?> getType(String name) {
        return getBean(name).getClass();
    }

    @Override
    public String[] getAliases(String name) {
        //looks like we cant have Aliases in object. We need some wrapper for it
        return new String[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
        String[] beanNames = getBeanNamesForType(type);
        Map<String, T> result = new LinkedHashMap<>(beanNames.length);
        for (String beanName : beanNames) {
            Object beanInstance = getBean(beanName);
            result.put(beanName, (T) beanInstance);
        }
        return result;
    }

    private String[] getBeanNamesForType(Class<?> type) {
        return allBeanNamesByType.get(type);
    }

    /**
     * Create a bean instance for the given bean definition.
     * <p>All bean retrieval methods delegate to this method for actual bean creation.
     *
     * @return a new instance of the bean
     * @throws BeanCreationException if the bean could not be created
     */
    public Object createBean(BeanDefinition bd) throws BeanCreationException {
        Class<?> clazz = bd.getClass();
        if (clazz.isInterface()) {
            throw new BeanCreationException(clazz, "Specified class is an interface");
        }

        Constructor<?> constructorToUse;
        try {
            constructorToUse = clazz.getDeclaredConstructor();
        } catch (Throwable ex) {
            throw new BeanCreationException(clazz, "No default constructor found", ex);
        }

        Object bean;
        try {
            bean = constructorToUse.newInstance();
        } catch (Throwable e) {
            throw new BeanCreationException(constructorToUse.toString(), "Constructor threw exception", e);
        }

        //we have a lot of checks and autowirings here. maybe will use later
        //populateBean(beanName, bd, bean);

        invokeCustomInitMethod(bean, bd.getInitMethodName());
        return bean;
    }

    private void invokeCustomInitMethod(Object bean, String initMethodName) {

        Class<?> beanClass = bean.getClass();
        try {
            //not sure if it needs. We use org.reflections.ReflectionUtils instead org.springframework.util.ReflectionUtils
            //ReflectionUtils.makeAccessible(methodToInvoke);
            Method initMethod = beanClass.getMethod(initMethodName);
            initMethod.invoke(bean);
        } catch (Throwable ex) {
            throw new BeanCreationException(beanClass, "Can`t invoke init method", ex);
        }
    }
}
