package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.exception.BeanCurrentlyInCreationException;
import com.petros.bringframework.beans.factory.config.SingletonBeanRegistry;
import com.petros.bringframework.core.AssertUtils;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author "Maksym Oliinyk"
 */
@Log4j2
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    /**
     * Cache of singleton objects: bean name to bean instance.
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /**
     * Names of beans that are currently in creation.
     */
    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    /**
     * Map between dependent bean names: bean name to Set of dependent bean names.
     */
    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);

    /**
     * Map between depending bean names: bean name to Set of bean names for the bean's dependencies.
     */
    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        AssertUtils.notNull(beanName, "'beanName' is required");
        AssertUtils.notNull(singletonObject, "'singletonObject' is required");
        synchronized (this.singletonObjects) {
            final Object oldObject = singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject +
                        "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
            }
            addSingleton(beanName, singletonObject);
        }
    }

    private void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
        }
    }

    @Nullable
    @Override
    public Object getSingleton(String beanName) {
        Object singletonObject = this.singletonObjects.get(beanName);
        //todo test this method and find ways of solving null reference
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            synchronized (this.singletonObjects) {
                return this.singletonObjects.get(beanName);
            }
        }
        return singletonObject;
    }

    /**
     * Return the (raw) singleton object registered under the given name,
     * creating and registering a new one if none registered yet.
     *
     * @param beanName         the name of the bean
     * @param singletonFactory the SingletonFactory to lazily create the singleton
     *                         with, if necessary
     * @return the registered singleton object
     */
    public Object getSingleton(String beanName, SingletonFactory<?> singletonFactory) {
        AssertUtils.notNull(beanName, "Bean name must not be null");
        synchronized (this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                log.debug("Creating shared instance of singleton bean '" + beanName + "'");
                beforeSingletonCreation(beanName);
                try {
                    singletonObject = singletonFactory.getObject();
                } catch (BeanCreationException ex) {
                    log.debug("Couldn't create bean '" + beanName + "'", ex);
                    throw ex;
                } finally {
                    afterSingletonCreation(beanName);
                }
                addSingleton(beanName, singletonObject);

            }
            return singletonObject;
        }
    }

    @Override
    public boolean containsSingleton(String beanName) {
        AssertUtils.notNull(beanName, "'beanName' is required");
        return this.singletonObjects.containsKey(beanName);
    }

    @Override
    public String[] getSingletonNames() {
        synchronized (this.singletonObjects) {
            return singletonObjects.keySet().toArray(new String[0]);
        }
    }

    @Override
    public int getSingletonCount() {
        return singletonObjects.values().size();
    }

    /**
     * Clear all cached singleton instances in this registry.
     */
    protected void clearSingletonCache() {
        synchronized (this.singletonObjects) {
            this.singletonObjects.clear();
        }
    }

    /**
     * Return whether the specified singleton bean is currently in creation (within the entire factory).
     *
     * @param beanName the name of the bean
     * @return whether the bean is currently in creation
     */
    protected boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    /**
     * Callback before singleton creation.
     * <p>The default implementation register the singleton as currently in creation.
     *
     * @param beanName the name of the singleton about to be created
     */
    protected void beforeSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName);
        }
    }

    /**
     * Callback after singleton creation.
     * <p>The default implementation marks the singleton as not in creation anymore.
     *
     * @param beanName the name of the singleton that has been created
     * @see #isSingletonCurrentlyInCreation
     */
    protected void afterSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
        }
    }

    /**
     * Determine whether the specified dependent bean has been registered as dependent on the given bean
     *
     * @param beanName          the name of the bean to check
     * @param dependentBeanName the name of the dependent bean
     * @return
     */
    protected boolean isDependent(String beanName, String dependentBeanName) {
        synchronized (this.dependentBeanMap) {
            if (dependentBeanMap.containsKey(beanName)) {
                return dependentBeanMap.get(beanName).contains(dependentBeanName);
            }
            return false;
        }
    }

    /**
     * Register a dependent bean for the given bean,
     * to be destroyed before the given bean is destroyed.
     *
     * @param beanName          the name of the bean
     * @param dependentBeanName the name of the dependent bean
     */
    public void registerDependentBean(String dependentBeanName, String beanName) {
        synchronized (dependentBeanMap) {
            final Set<String> dependentBeans = dependentBeanMap.computeIfAbsent(beanName, k -> new LinkedHashSet());
            if (!dependentBeans.add(dependentBeanName)) {
                return;
            }
        }

        synchronized (dependenciesForBeanMap) {
            final Set<String> dependenciesForBean = this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet());
            dependenciesForBean.add(beanName);
        }
    }

    /**
     * Remove the bean with the given name from the singleton cache
     *
     * @param beanName the name of the bean
     */
    protected void removeSingleton(String beanName) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.remove(beanName);
        }
    }

    /**
     * Destroy the given bean. Must destroy beans that depend on the given
     * bean before the bean itself. Should not throw any exceptions.
     *
     * @param beanName the name of the bean
     */
    protected void destroyBean(String beanName) {
        synchronized (dependentBeanMap) {
            final Set<String> dependentBeans = Optional.ofNullable(dependentBeanMap.remove(beanName))
                    .orElse(Collections.emptySet());

            dependentBeans.forEach(dependentBeanName -> {
                destroySingleton(dependentBeanName);
            });
        }

        synchronized (dependenciesForBeanMap){
            for (Iterator<Map.Entry<String, Set<String>>> it = this.dependentBeanMap.entrySet().iterator(); it.hasNext();) {
                final Map.Entry<String, Set<String>> entry = it.next();
                final Set<String> dependenciesToClean = entry.getValue();
                dependenciesToClean.remove(beanName);
                if (dependenciesToClean.isEmpty()) {
                    it.remove();
                }
            }
        }
        this.dependenciesForBeanMap.remove(beanName);
    }

    /**
     * Destroy the given bean. Delegates to {@code destroyBean}
     * if a corresponding disposable bean instance is found.
     *
     * @param beanName the name of the bean
     */
    public void destroySingleton(String beanName) {
        removeSingleton(beanName);
        destroyBean(beanName);
    }


}


