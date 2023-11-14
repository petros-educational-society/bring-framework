package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.factory.config.AnnotatedBeanDefinition;
import com.petros.bringframework.beans.factory.config.BeanDefinition;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.petros.bringframework.core.AssertUtils.notBlank;
import static com.petros.bringframework.core.AssertUtils.uncapitalizeAsProperty;
import static com.petros.bringframework.util.ClassUtils.getShortName;

public class AnnotationBeanNameGenerator implements BeanNameGenerator {

    public static final BeanNameGenerator INSTANCE = new AnnotationBeanNameGenerator();
    private final Map<String, Set<String>> metaAnnotationTypesCache;

    private AnnotationBeanNameGenerator() {
        metaAnnotationTypesCache = new ConcurrentHashMap<>();
    }

    @Override
    public String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry beanDefinitionRegistry) {
        if (beanDefinition instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
            var beanName = determineBeanDefinitionNameFrom(annotatedBeanDefinition);
            if (notBlank(beanName)) {
                return beanName;
            }
        }
        return buildBeanName(beanDefinition);
    }

    protected String determineBeanDefinitionNameFrom(AnnotatedBeanDefinition annotatedBeanDefinition) {
        var annotationMetadata = annotatedBeanDefinition.getMetadata();
        var types = annotationMetadata.getAnnotationTypes();
        String beanName = null;
        for (var type : types) {
            var attributes = annotationMetadata.getAnnotationAttributes(type);
            if (attributes != null) {
                var metaTypes = metaAnnotationTypesCache.computeIfAbsent(type, key -> {
                    var res = annotationMetadata.getMetaAnnotationTypes(key);
                    return res.isEmpty() ? Collections.emptySet() : res;
                });
                if (isStereotypeWithNameValue(type, metaTypes, attributes)) {
                    var value = attributes.get("value");
                    if (value instanceof String strVal && !strVal.isEmpty()) {
                        if (beanName != null && !strVal.equals(beanName)) {
                            throw new IllegalStateException("Stereotype annotations suggest inconsistent " +
                                    "component names: '" + beanName + "' versus '" + strVal + "'");
                        }
                        beanName = strVal;
                    }
                }
            }
        }
        return beanName;
    }

    protected boolean isStereotypeWithNameValue(String annotationType, Set<String> metaAnnotationTypes, Map<String, Object> attrs) {
        var strAnnotationType = "com.petros.bringframework.context.annotation.Component";
        boolean isStereotype = annotationType.equals(strAnnotationType) ||
                metaAnnotationTypes.contains(strAnnotationType) ||
                annotationType.equals("jakarta.annotation.ManagedBean") ||
                annotationType.equals("jakarta.inject.Named");
        return isStereotype && attrs != null && attrs.containsKey("value");
    }

    protected String buildBeanName(BeanDefinition beanDefinition) {
        var beanClassName = beanDefinition.getBeanClassName();
        notBlank(beanClassName, "No bean class name set");
        return uncapitalizeAsProperty(getShortName(beanClassName));
    }
}
