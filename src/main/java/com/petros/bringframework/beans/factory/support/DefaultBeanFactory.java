package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeanException;
import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.config.BeanDefinition;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanFactory implements BeanFactory, Serializable {

    private final BeanDefinitionRegistry beanDefinitionRegistry;

    private Map<Class<?>, String[]> allBeanNamesByType;

    /**
     * Map from dependency type to corresponding autowired value.
     */
    private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap<>(16);

    /**
     * Map of bean objects, keyed by bean name.
     */
    private final Map<String, Object> beanMap = new ConcurrentHashMap<>(256);


    /**
     * Create a new DefaultListableBeanFactory.
     */
    public DefaultBeanFactory(BeanDefinitionRegistry beanDefinitionRegistry) {
        super();
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    public void createBeansFromDefinitions() {
        Map<String, BeanDefinition> beanDefinitions = beanDefinitionRegistry.getBeanDefinitions();
        beanDefinitions.forEach((beanName, bd) -> {
            Object bean = createBean(bd);
            beanMap.put(beanName, bean);
        });
    }

    @Override
    public Object getBean(String name) {
        return beanMap.get(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        List<Object> beans = beanMap.values().stream()
                .filter(bean -> requiredType.equals(bean.getClass()))
                .toList();
        if (beans.size() > 1) {
            throw new BeanCreationException(requiredType, "Class has more than one beans.");
        }
        return (T) beans.get(0);
    }

    @Override
    public boolean containsBean(String name) {
        return beanMap.containsKey(name);
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
    public Class<?> getType(String name) {
        Object bean = getBean(name);
        return bean.getClass();
    }

    @Override
    public String[] getAliases(String name) {
        //looks like we cant have Aliases in object. We need some wrapper for it
        return new String[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeanException {
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
    public Object createBean(BeanDefinition bd)
            throws BeanCreationException {

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
            if (initMethod != null) {
                initMethod.invoke(bean);
            }
        } catch (Throwable ex) {
            throw new BeanCreationException(beanClass, "Can`t invoke init method", ex);
        }
    }
}
