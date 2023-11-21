package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.factory.ConfigurableBeanFactory;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanFactoryPostProcessor;
import com.petros.bringframework.beans.factory.config.BeanPostProcessor;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.core.type.ResolvableType;
import com.petros.bringframework.factory.config.NamedBeanHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author "Oleksii Skachkov"
 * @author "Marina Vasiuk"
 */
@Slf4j
public class DefaultBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableBeanFactory {
    private final Map<String, Object> beanCacheByName = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> beanCacheByType = new ConcurrentHashMap<>();
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = Collections.synchronizedList(new LinkedList<>());
    private final Map<String, BeanPostProcessor> beanPostProcessors = new ConcurrentHashMap<>();
    private final Map<Class<?>, String[]> allBeanNamesByType = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap<>(16);

    public DefaultBeanFactory(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public boolean containsBean(String name) {
        return beanCacheByName.containsKey(name);
    }

    @Override
    public <T> void configureBeans(T t) {
        beanPostProcessors.forEach((key, value) -> value.postProcessBeforeInitialization(t, key));
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

    @Override
    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return beanFactoryPostProcessors;
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

    private String[] getBeanNamesForType(Class<?> type) {
        return getBeanNamesForType(type, true);
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
            cache.put(type, resolvedBeanNames);
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
                matchFound = isTypeMatch(beanName, beanDefinition, resolvableType);
            }
            if (matchFound) {
                result.add(beanName);
            }
        }

        return result != null && !result.isEmpty() ? result.toArray(new String[]{}) : new String[]{};
    }

    @Override
    public void preInstantiateSingletons() throws BeansException {
        List<String> beanNames = new ArrayList<>(this.registry.getBeanDefinitions().keySet());
        for (String beanName : beanNames) {
            final BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (!beanDef.isAbstract() && beanDef.isSingleton() && !beanDef.isLazyInit()) {
                if (isFactoryBean(beanName)) {
                    //todo implement if needed
                    throw new NotImplementedException();
                } else {
                    getBean(beanName);
                }
            }
        }
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        //todo finish implementation
//        AssertUtils.notNull(beanPostProcessor, "BeanPostProcessor must not be null");
//        synchronized (this.beanPostProcessors) {
//            // Remove from old position, if any
//            this.beanPostProcessors.remove(beanPostProcessor);
//            // Add to end of list
//            this.beanPostProcessors.add(beanPostProcessor);
//        }
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor) {
        synchronized (this.beanFactoryPostProcessors) {
            this.beanFactoryPostProcessors.remove(beanFactoryPostProcessor);
            this.beanFactoryPostProcessors.add(beanFactoryPostProcessor);
        }
    }
}
