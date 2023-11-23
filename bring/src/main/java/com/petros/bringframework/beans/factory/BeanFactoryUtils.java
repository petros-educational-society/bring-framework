package com.petros.bringframework.beans.factory;

import com.petros.bringframework.core.AssertUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.petros.bringframework.beans.factory.BeanFactory.FACTORY_BEAN_PREFIX;

/**
 * Convenience methods operating on bean factories, in particular
 * on the {@link BeanFactory} interface.
 *
 * <p>Returns bean counts, bean names or bean instances,
 * taking into account the nesting hierarchy of a bean factory.
 *
 * @author Viktor Basanets
 * @Project: bring-framework
 */

public abstract class BeanFactoryUtils {

    /**
     * Cache from name with factory bean prefix to stripped name without dereference
     */
    private static final Map<String, String> transformedBeanNameCache = new ConcurrentHashMap<>();

    /**
     * Return the actual bean name, stripping out the factory dereference
     * prefix (if any, also stripping repeated factory prefixes if found).
     * @param name the name of the bean
     * @return the transformed name
     */
    public static String transformedBeanName(String name) {
        AssertUtils.notNull(name, "'name' must not be null");
        if (!name.startsWith(FACTORY_BEAN_PREFIX)) {
            return name;
        }
        return transformedBeanNameCache.computeIfAbsent(name, n -> n.replace(FACTORY_BEAN_PREFIX, ""));
    }
}
