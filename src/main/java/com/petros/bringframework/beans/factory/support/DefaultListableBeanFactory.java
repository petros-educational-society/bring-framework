package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.exception.BeansException;
import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.config.BeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanDefinitionHolder;
import org.reflections.ReflectionUtils;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory implements BeanFactory, BeanDefinitionRegistry, Serializable {


    /**
     * Map from serialized id to factory instance.
     */
    private static final Map<String, Reference<DefaultListableBeanFactory>> serializableFactories =
            new ConcurrentHashMap<>(8);

    /**
     * Whether to allow re-registration of a different definition with the same name.
     */
    private boolean allowBeanDefinitionOverriding = true;

    /**
     * Whether to allow eager class loading even for lazy-init beans.
     */
    private boolean allowEagerClassLoading = true;

    /**
     * Resolver to use for checking if a bean definition is an autowire candidate.
     */
    //todo
    //private AutowireCandidateResolver autowireCandidateResolver = SimpleAutowireCandidateResolver.INSTANCE;

    /**
     * Map from dependency type to corresponding autowired value.
     */
    private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap<>(16);

    /**
     * Map of bean definition objects, keyed by bean name.
     */
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

    /**
     * Map from bean name to merged BeanDefinitionHolder.
     */
    private final Map<String, BeanDefinitionHolder> mergedBeanDefinitionHolders = new ConcurrentHashMap<>(256);


    /**
     * Create a new DefaultListableBeanFactory.
     */
    public DefaultListableBeanFactory() {
        super();
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {

    }

    @Override
    public void removeBeanDefinition(String beanName) {

    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return null;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return false;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return new String[0];
    }

    @Override
    public int getBeanDefinitionCount() {
        return 0;
    }

    @Override
    public boolean isBeanNameInUse(String beanName) {
        return false;
    }

    @Override
    public void registerAlias(String beanName, String alias) {

    }

    @Override
    public Object getBean(String name) {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return null;
    }

    @Override
    public boolean containsBean(String name) {
        return false;
    }

    @Override
    public boolean isSingleton(String name) {
        return false;
    }

    @Override
    public boolean isPrototype(String name) {
        return false;
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) {
        return false;
    }

    @Override
    public Class<?> getType(String name) {
        return null;
    }

    @Override
    public String[] getAliases(String name) {
        return new String[0];
    }

    //-------------------------------------------------------------------------
    // Typical methods for creating and populating external bean instances
    //-------------------------------------------------------------------------

    @Override
    public Object createBean(String beanName, BeanDefinition bd, @Nullable Object[] args)
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

        //тут куча перевірок та автовайрінг можливо саме цього біна до вже існуючих
        //populateBean(beanName, bd, bean);

        //let we have only one init method every time - "init"
        invokeCustomInitMethod(bean, "init");
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

    @Override
    public void autowireBean(Object existingBean) throws BeansException {

    }

    @Override
    public Object configureBean(Object existingBean, String beanName) throws BeansException {
        return null;
    }
}
