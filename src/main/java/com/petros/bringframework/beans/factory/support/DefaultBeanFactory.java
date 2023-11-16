package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanFactoryPostProcessor;
import com.petros.bringframework.beans.factory.config.BeanPostProcessor;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.core.type.ResolvableType;
import com.petros.bringframework.factory.config.NamedBeanHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author "Oleksii Skachkov"
 * @author "Marina Vasiuk"
 */
@Slf4j
public class DefaultBeanFactory implements BeanFactory {
    private final Map<String, Object> beanCacheByName = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> beanCacheByType = new ConcurrentHashMap<>();
    private final Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessors = new ConcurrentHashMap<>();
    private final Map<String, BeanPostProcessor> beanPostProcessors = new ConcurrentHashMap<>();
    private final Map<Class<?>, String[]> allBeanNamesByType = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap<>(16);

    private final BeanDefinitionRegistry registry;

    public DefaultBeanFactory(BeanDefinitionRegistry registry) {
        this.registry = registry;
//        this.beanFactoryPostProcessors.putAll(getBeansOfType(BeanFactoryPostProcessor.class));
//        final Map<? extends Class<?>, List<ReflectionScannedGenericBeanDefinition>> collect =
//                registry.getBeanDefinitions().entrySet().stream()
//                        .filter(e -> ReflectionScannedGenericBeanDefinition.class.isInstance(e.getValue()))
//                        .map(e -> (ReflectionScannedGenericBeanDefinition) e.getValue())
//                        .collect(Collectors.groupingBy(bd -> ReflectionClassMetadata.class.cast(bd.getMetadata()).getIntrospectedClass()));
//        this.beanPostProcessors.putAll(getBeansOfType(BeanPostProcessor.class));

    }

    @Override
    public boolean containsBean(String name) {
        return beanCacheByName.containsKey(name);
    }

    @Override
    public void createBeansFromDefinitions() {
        registry.getBeanDefinitions().forEach((beanName, bd) -> {
            Object bean = createBean(bd);
            beanCacheByName.put(beanName, bean);
            //todo: does it make sense to add the bean class-name to the beanDefinition when scanning packages and use it here?
            beanCacheByType.put(bean.getClass(), bean);
        });
    }

    @Override
    public <T> void configureBeans(T t) {
        beanPostProcessors.forEach((key, value) -> value.postProcessBeforeInitialization(t, key));
    }

    @Override
    public Object getBean(String name) {
        return beanCacheByName.get(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        T resolved = resolveBean(ResolvableType.forRawClass(requiredType), null);
        if (resolved == null) {
            throw new NoSuchBeanDefinitionException(requiredType);
        }
        return resolved;
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
    public boolean isTypeMatch(String name, ResolvableType typeToMatchh) {
        final Object beanInstance = getSingleton(name);
        if (beanInstance != null) {
            return typeToMatchh.isInstance(beanInstance);
        }
        return false;
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

    /**
     * Create a bean instance for the given bean definition.
     * <p>All bean retrieval methods delegate to this method for actual bean creation.
     *
     * @return a new instance of the bean
     * @throws BeanCreationException if the bean could not be created
     */
    @SneakyThrows
    private Object createBean(BeanDefinition bd) throws BeanCreationException {
//        var clazz = ReflectionClassMetadata.class.cast(bd).getIntrospectedClass();
        var clazz = Class.forName(bd.getBeanClassName());
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

//        invokeCustomInitMethod(bean, bd.getInitMethodName());
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


    @Nullable
    private <T> T resolveBean(ResolvableType requiredType, @Nullable Object[] args) {
        NamedBeanHolder<T> namedBean = resolveNamedBean(requiredType, args, true);
        if (namedBean != null) {
            return namedBean.getBeanInstance();
        }
        return null;
    }

    @Nullable
    private <T> NamedBeanHolder<T> resolveNamedBean(ResolvableType requiredType, @Nullable Object[] args, boolean throwExceptionIfNonUnique) throws BeansException {
        AssertUtils.notNull(requiredType, "Required type must not be null");
        String[] candidateNames = getBeanNamesForType(requiredType);

        if (candidateNames.length > 1) {
            throw new NotImplementedException("Work with several beans of same type not implemented yet");
        }

        if (candidateNames.length == 1) {
            final String beanName = candidateNames[0];
            Object bean = getBean(beanName);
            return new NamedBeanHolder<>(beanName, adaptBeanInstance(beanName, bean, requiredType.toClass()));
        } else if (candidateNames.length > 1) {
            Map<String, Object> candidates = new LinkedHashMap<>(candidateNames.length);
            if (throwExceptionIfNonUnique) {
                throw new NoUniqueBeanDefinitionException(candidates.keySet());
            }
        }

        return null;
    }

    private Object getSingleton(String name) {
        final Object singelton = beanCacheByName.get(name);
        if (singelton == null) {
            throw new NotImplementedException();
        }
        return singelton;
    }

    private <T> T adaptBeanInstance(String name, Object bean, @Nullable Class<?> requiredType) {
        // Check if required type matches the type of the actual bean instance.
        if (requiredType != null && !requiredType.isInstance(bean)) {
            throw new NotImplementedException("Work with converters not implemented");
        }
        return (T) bean;
    }

    private String[] getBeanNamesForType(Class<?> type) {
        return allBeanNamesByType.get(type);
    }

    private String[] getBeanNamesForType(ResolvableType type) {
        Class<?> resolved = type.resolve();
        if (resolved != null && !type.hasGenerics()) {
            return getBeanNamesForType(resolved, true);
        } else {
            throw new NotImplementedException("Work with generics not implemented yet");
        }
    }

    private String[] getBeanNamesForType(Class<?> type, boolean allowEagerInit) {
        final Map<Class<?>, String[]> cache = this.allBeanNamesByType;
        String[] resolvedBeanNames = cache.get(type);
        if (resolvedBeanNames != null) {
            return resolvedBeanNames;
        } else {
            resolvedBeanNames = doGetBeanNamesForType(ResolvableType.forRawClass(type), true);
        }

        return resolvedBeanNames;
    }

    private String[] doGetBeanNamesForType(ResolvableType resolvableType, boolean allowEagerInit) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : registry.getBeanDefinitions().entrySet()) {
            final String beanName = beanDefinitionEntry.getKey();
            final BeanDefinition beanDefinition = beanDefinitionEntry.getValue();
            boolean matchFound = false;
            if (beanDefinition.isSingleton()) {
                matchFound = isTypeMatch(beanName, resolvableType);
            }
            if (matchFound) {
                result.add(beanName);
            }
        }

        return result != null && !result.isEmpty() ? result.toArray(new String[]{}) : new String[]{};
    }
}
