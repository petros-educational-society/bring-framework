package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.factory.BeanFactory;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.beans.support.GenericBeanDefinition;
import com.petros.bringframework.util.AutowireClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * {@link com.petros.bringframework.beans.factory.config.BeanPostProcessor BeanPostProcessor}
 * implementation that modify bean definitions with metadata corresponding to the constructor or factory method.
 *
 * @author "Skachkov Oleksii"
 */
public class SimpleBeanFactoryPostProcessor implements BeanFactoryPostProcessor{

    private final BeanFactory beanFactory;

    public SimpleBeanFactoryPostProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void postProcessBeanFactory(BeanFactory beanFactory) {
        BeanDefinitionRegistry registry = beanFactory.getBeanDefinitionRegistry();
        Map<String, BeanDefinition> beanDefinitions = registry.getBeanDefinitions();
        beanDefinitions.forEach(this::updateBeanDefinitionConstructor);
    }

    private void updateBeanDefinitionConstructor(String name, BeanDefinition bd) {
        if (bd.getFactoryMethodName() != null) {
            return;
        }
        Map<Boolean, Constructor<?>> constructors = AutowireClassUtils.determineCandidateConstructors(name, bd);
        Constructor<?> autowiredConstructor = constructors.get(Boolean.TRUE);
        GenericBeanDefinition beanDefinition = (GenericBeanDefinition)bd;
        if (autowiredConstructor != null) {
            Parameter[] parameters = autowiredConstructor.getParameters();
            SimpleConstructorArgumentValues values = new SimpleConstructorArgumentValues();
            for (int i = 0; i < parameters.length; i++) {
                values.addIndexedArgumentValue(i, parameters[i].getParameterizedType(), parameters[i].getName());
            }
            beanDefinition.setConstructorArgumentValues(values);
            beanDefinition.setResolvedConstructor(autowiredConstructor);
            beanDefinition.setAutowiredConstructorArgumentsResolved(true);
            beanDefinition.setAutowireMode(AutowireMode.AUTOWIRE_CONSTRUCTOR);
        } else {
            Constructor<?> constructor = constructors.get(Boolean.FALSE);
            if (constructor != null) {
                Parameter[] parameters = constructor.getParameters();
                SimpleConstructorArgumentValues values = new SimpleConstructorArgumentValues();
                for (int i = 0; i < parameters.length; i++) {
                    values.addIndexedArgumentValue(i, parameters[i].getParameterizedType(), parameters[i].getName());
                }
                beanDefinition.setConstructorArgumentValues(values);
                beanDefinition.setResolvedConstructor(constructor);
            }
        }
    }
}
