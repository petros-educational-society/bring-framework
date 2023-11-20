package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.TypeConverter;
import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.factory.ConfigurableBeanFactory;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanFactoryPostProcessor;
import com.petros.bringframework.beans.factory.config.BeanPostProcessor;
import com.petros.bringframework.core.AssertUtils;
import com.petros.bringframework.core.type.ResolvableType;
import com.petros.bringframework.core.type.convert.ConversionService;
import com.petros.bringframework.factory.config.NamedBeanHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
    private final Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessors = new ConcurrentHashMap<>();
    private final Map<String, BeanPostProcessor> beanPostProcessors = new ConcurrentHashMap<>();
    private final Map<Class<?>, String[]> allBeanNamesByType = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap<>(16);

    @Nullable
    private TypeConverter typeConverter;
    @Nullable
    private ConversionService conversionService;

    public DefaultBeanFactory(BeanDefinitionRegistry registry) {
        super(registry);
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
    public <T> void configureBeans(T t) {
        beanPostProcessors.forEach((key, value) -> value.postProcessBeforeInitialization(t, key));
    }

    @Override
    public Object getBean(String name) {
        return beanCacheByName.get(name);
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
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) {
        final Object beanInstance = getSingleton(name);
        if (beanInstance != null) {
            return typeToMatch.isInstance(beanInstance);
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
    @SuppressWarnings("unchecked")
    private <T> NamedBeanHolder<T> resolveNamedBean(ResolvableType requiredType, @Nullable Object[] args, boolean throwExceptionIfNonUnique) throws BeansException {
        AssertUtils.notNull(requiredType, "Required type must not be null");
        String[] candidateNames = getBeanNamesForType(requiredType);

        if (candidateNames.length > 1) {
            var autowireCandidates = Arrays.stream(candidateNames)
                    .filter(this::absentInDefinitionsAndAutowireCandidate).toList();
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
//            if (isNull(candidateName)) {
//                candidateName = determineHighestPriorityCandidate(candidates);
//            }

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
     * @param candidates a Map of candidate names and candidate instances
     * @return the name of the primary candidate, or {@code null} if none found
     */
    @Nullable
    protected String determinePrimaryCandidate(Map<String, Object> candidates) {
        String primaryBeanName = null;
        for (var candidateBeanName : candidates.keySet()) {
            if (isPrimary(candidateBeanName)) {
                if (nonNull(primaryBeanName)) {
                    boolean candidateLocal = containsBeanDefinition(candidateBeanName);
                    if (candidateLocal && containsBeanDefinition(primaryBeanName)) {
                        throw new NoUniqueBeanDefinitionException(candidates.size(),
                                "more than one 'primary' bean found among candidates: " + candidates.keySet());
                    }
                    if (candidateLocal) {
                        primaryBeanName = candidateBeanName;
                    }
                } else {
                    primaryBeanName = candidateBeanName;
                }
            }
        }
        return primaryBeanName;
    }

//    /**
//     * Determine the candidate with the highest priority in the given set of beans.
//     * <p>Based on {@code @jakarta.annotation.Priority}. As defined by the related
//     * the highest priority.
//     * @param candidates a Map of candidate names and candidate instances
//     * (or candidate classes if not created yet) that match the required type
//     * @return the name of the candidate with the highest priority,
//     * or {@code null} if none found
//     */
//    @Nullable
//    protected String determineHighestPriorityCandidate(Map<String, Object> candidates) {
//        String highestPriorityBeanName = null;
//        Integer highestPriority = null;
//        for (var entry : candidates.entrySet()) {
//            var candidateBeanName = entry.getKey();
//            var beanInstance = entry.getValue();
//            if (nonNull(beanInstance)) {
//                Integer candidatePriority = getPriority(beanInstance);
//                if (nonNull(candidatePriority)) {
//                    if (nonNull(highestPriority)) {
//                        if (candidatePriority.equals(highestPriority)) {
//                            throw new NoUniqueBeanDefinitionException(candidates.size(),
//                                    "Multiple beans found with the same priority ('" + highestPriority +
//                                            "') among candidates: " + candidates.keySet());
//                        } else if (candidatePriority < highestPriority) {
//                            highestPriorityBeanName = candidateBeanName;
//                            highestPriority = candidatePriority;
//                        }
//                    } else {
//                        highestPriorityBeanName = candidateBeanName;
//                        highestPriority = candidatePriority;
//                    }
//                }
//            }
//        }
//        return highestPriorityBeanName;
//    }

    /**
     * Return whether the bean definition for the given bean name has been
     * marked as a primary bean.
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

    private boolean absentInDefinitionsAndAutowireCandidate(String name) {
        return !containsBeanDefinition(name) || getBeanDefinition(name).isAutowireCandidate();
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

//    private Object getSingleton(String name) {
//        final Object singleton = beanCacheByName.get(name);
//        if (singleton == null) {
//            throw new NotImplementedException();
//        }
//        return singleton;
//    }

    //todo remove
//    private Object getSingleton(String name) {
//        final Object singelton = beanCacheByName.get(name);
//        if (singelton == null) {
//            throw new NotImplementedException();
//        }
//        return singelton;
//    }

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

    /**
     * Initialize the given PropertyEditorRegistry with the custom editors
     * that have been registered with this BeanFactory.
     * <p>To be called for BeanWrappers that will create and populate bean
     * instances, and for SimpleTypeConverter used for constructor argument
     * and factory method type conversion.
     */
//    protected void registerCustomEditors(PropertyEditorRegistry registry) {
//        if (registry instanceof DefaultPropertyEditorRegistry defaultRegistry) {
//            defaultRegistry.useConfigValueEditors();
//        }
//        if (!this.propertyEditorRegistrars.isEmpty()) {
//            for (PropertyEditorRegistrar registrar : this.propertyEditorRegistrars) {
//                try {
//                    registrar.registerCustomEditors(registry);
//                }
//                catch (BeanCreationException ex) {
//                    Throwable rootCause = ex.getMostSpecificCause();
//                    if (rootCause instanceof BeanCurrentlyInCreationException bce) {
//                        String bceBeanName = bce.getBeanName();
//                        if (bceBeanName != null && isCurrentlyInCreation(bceBeanName)) {
//                            if (logger.isDebugEnabled()) {
//                                logger.debug("PropertyEditorRegistrar [" + registrar.getClass().getName() +
//                                        "] failed because it tried to obtain currently created bean '" +
//                                        ex.getBeanName() + "': " + ex.getMessage());
//                            }
//                            onSuppressedException(ex);
//                            continue;
//                        }
//                    }
//                    throw ex;
//                }
//            }
//        }
//        if (!this.customEditors.isEmpty()) {
//            this.customEditors.forEach((requiredType, editorClass) ->
//                    registry.registerCustomEditor(requiredType, BeanUtils.instantiateClass(editorClass)));
//        }
//    }

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
                matchFound = isTypeMatch(beanName, resolvableType);
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
}
