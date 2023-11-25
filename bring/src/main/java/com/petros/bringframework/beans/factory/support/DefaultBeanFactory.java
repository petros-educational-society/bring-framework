package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.TypeConverter;
import com.petros.bringframework.beans.factory.ConfigurableBeanFactory;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanFactoryPostProcessor;
import com.petros.bringframework.beans.factory.config.BeanPostProcessor;
import com.petros.bringframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.core.type.ResolvableType;
import com.petros.bringframework.core.type.convert.ConversionService;
import com.petros.bringframework.factory.config.NamedBeanHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author "Oleksii Skachkov"
 * @author "Marina Vasiuk"
 */
@Slf4j
public class DefaultBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableBeanFactory {
    private final Map<String, Object> beanCacheByName = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> beanCacheByType = new ConcurrentHashMap<>();
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = Collections.synchronizedList(new LinkedList<>());
    private final List<BeanPostProcessor> beanPostProcessors = Collections.synchronizedList(new LinkedList<>());
    private final Map<Class<?>, String[]> allBeanNamesByType = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap<>(16);

    @Nullable
    private TypeConverter typeConverter;
    @Nullable
    private ConversionService conversionService;

    public DefaultBeanFactory(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public boolean containsBean(String name) {
        return beanCacheByName.containsKey(name);
    }

    @Override
    public Object getBean(String name) {
        return beanCacheByName.computeIfAbsent(name, super::getBean);
    }

    @Override
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

    public void postProcessBeforeDistraction() {
        if (!(beanPostProcessors.isEmpty())) {
            for (BeanPostProcessor processor : this.beanPostProcessors) {
                if (processor instanceof DestructionAwareBeanPostProcessor) {
                    beanCacheByName.forEach((beanName, bean) ->
                            ((DestructionAwareBeanPostProcessor) processor).postProcessBeforeDestruction(bean, beanName));
                }
            }
        }
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
            var beanInstance = getBean(beanName);
            result.put(beanName, (T) beanInstance);
        }
        return result;
    }

    @Override
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }

    @Override
    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return beanFactoryPostProcessors;
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
    @SuppressWarnings("unchecked")
    private <T> NamedBeanHolder<T> resolveNamedBean(ResolvableType requiredType, @Nullable Object[] args, boolean throwExceptionIfNonUnique) throws BeansException {
        AssertUtils.notNull(requiredType, "Required type must not be null");
        String[] candidateNames = getBeanNamesForType(requiredType);

        if (candidateNames.length > 1) {
            var autowireCandidates = Arrays.stream(candidateNames)
                    .filter(this::isAutowireCandidate).toList();
            candidateNames = getNewAutowireCandidatesIfPresent(autowireCandidates, candidateNames);
        }

        if (candidateNames.length == 1) {
            return resolveNamedBean(candidateNames[0], requiredType);
        }

        if (candidateNames.length > 1) {
            Map<String, Object> candidates = new LinkedHashMap<>(candidateNames.length);
            for (var beanName : candidateNames) {
                candidates.put(beanName, containsSingleton(beanName) ? getBean(beanName) : getType(beanName));
            }

            var candidateName = determinePrimaryCandidate(candidates);
            if (nonNull(candidateName)) {
                var beanInstance = candidates.get(candidateName);
                if (isNull(beanInstance)) {
                    return null;
                }
                if (beanInstance instanceof Class) {
                    return resolveNamedBean(candidateName, requiredType);
                }
                return new NamedBeanHolder<>(candidateName, (T) beanInstance);
            }

            if (throwExceptionIfNonUnique) {
                throw new NoUniqueBeanDefinitionException(candidates.keySet());
            }
        }

        return null;
    }

    /**
     * Determine the primary candidate in the given set of beans.
     *
     * @param candidates a Map of candidate names and candidate instances
     * @return the name of the primary candidate, or {@code null} if none found
     */
    @Nullable
    protected String determinePrimaryCandidate(Map<String, Object> candidates) {
        String primaryBeanName = null;
        for (var candidateBeanName : candidates.keySet()) {
            if (!isPrimary(candidateBeanName)) {
                continue;
            }

            if (isNull(primaryBeanName)) {
                primaryBeanName = candidateBeanName;
                continue;
            }

            boolean isLocal = containsBeanDefinition(candidateBeanName);
            boolean isPrimary = containsBeanDefinition(primaryBeanName);
            if (isLocal && isPrimary) {
                throw new NoUniqueBeanDefinitionException(candidates.size(),
                        "more than one 'primary' bean found among candidates: " + candidates.keySet());
            }

            if (isLocal) {
                primaryBeanName = candidateBeanName;
            }
        }
        return primaryBeanName;
    }

    /**
     * Return whether the bean definition for the given bean name has been
     * marked as a primary bean.
     *
     * @param beanName the name of the bean
     * @return whether the given bean qualifies as primary
     */
    private boolean isPrimary(String beanName) {
        var transBeanName = transformedBeanName(beanName);
        if (containsBeanDefinition(transBeanName)) {
            return getBeanDefinition(transBeanName).isPrimary();
        }
        return false;
    }

    private <T> NamedBeanHolder<T> resolveNamedBean(String beanName, ResolvableType requiredType) throws BeansException {
        return new NamedBeanHolder<>(beanName, adaptBeanInstance(beanName, getBean(beanName), requiredType.toClass()));
    }

    private String[] getNewAutowireCandidatesIfPresent(List<String> autowireCandidates, String[] oldCandidates) {
        return !autowireCandidates.isEmpty() ? autowireCandidates.toArray(String[]::new) : oldCandidates;
    }

    private boolean isAutowireCandidate(String name) {
        if (containsBeanDefinition(name)) {
            return getBeanDefinition(name).isAutowireCandidate();
        }
        return false;
    }

    private boolean containsBeanDefinition(String beanName) {
        return Arrays.stream(registry.getBeanDefinitionNames())
                .anyMatch(beanName::equalsIgnoreCase);
    }

    private BeanDefinition getBeanDefinition(String beanName) {
        return Optional.ofNullable(registry.getBeanDefinition(beanName))
                .orElseThrow(() -> {
                    if (log.isTraceEnabled()) log.trace("No bean names '{}' found in {}", beanName, this);
                    throw new NoSuchBeanDefinitionException(beanName);
                });
    }

    @Nullable
    @Override
    protected TypeConverter getCustomTypeConverter() {
        return this.typeConverter;
    }

    public void setTypeConverter(@Nullable TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    @Override
    @Nullable
    protected ConversionService getConversionService() {
        return this.conversionService;
    }

    public void setConversionService(@Nullable ConversionService conversionService) {
        this.conversionService = conversionService;
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

        return !result.isEmpty() ? result.toArray(new String[]{}) : new String[]{};
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
        AssertUtils.notNull(beanPostProcessor, "BeanPostProcessor must not be null");
        synchronized (this.beanPostProcessors) {
            this.beanPostProcessors.remove(beanPostProcessor);
            this.beanPostProcessors.add(beanPostProcessor);
        }
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor) {
        synchronized (this.beanFactoryPostProcessors) {
            this.beanFactoryPostProcessors.remove(beanFactoryPostProcessor);
            this.beanFactoryPostProcessors.add(beanFactoryPostProcessor);
        }
    }
}
