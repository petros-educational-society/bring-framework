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

/**
 * {@link BeanNameGenerator} implementation for bean classes annotated with the
 * {@link com.petros.bringframework.context.annotation.Component @Component} annotation or
 * with another annotation that is itself annotated with {@code @Component} as a
 * meta-annotation.
 *
 * <p>If the annotation's value doesn't indicate a bean name, an appropriate
 * name will be built based on the short name of the class (with the first
 * letter lower-cased). For example:
 *
 * <pre class="code">com.xyz.FooServiceImpl -&gt; fooServiceImpl</pre>
 *
 * @see BeanNameGenerator
 * @author "Viktor Basanets"
 */
public class AnnotationBeanNameGenerator implements BeanNameGenerator {

    public static final BeanNameGenerator INSTANCE = new AnnotationBeanNameGenerator();
    private final Map<String, Set<String>> metaAnnotationTypesCache;

    private AnnotationBeanNameGenerator() {
        metaAnnotationTypesCache = new ConcurrentHashMap<>();
    }

    //todo: remove beanDefinitionRegistry if not required
    /**
     * Generates a bean name for the provided bean definition.
     *
     * @param beanDefinition        the bean definition for which to generate a bean name
     * @param beanDefinitionRegistry the registry for bean definitions (may not be used)
     * @return the generated bean name
     */
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

    /**
     * Determines the bean definition name from the annotated bean definition.
     *
     * @param annotatedBeanDefinition the annotated bean definition
     * @return the determined bean name
     */
    protected String determineBeanDefinitionNameFrom(AnnotatedBeanDefinition annotatedBeanDefinition) {
        var annotationMetadata = annotatedBeanDefinition.getMetadata();
        var types = annotationMetadata.getAnnotationTypes();
        String beanName = null;
        for (var type : types) {
            var attributes = annotationMetadata.getAnnotationAttributes(type);
            if (attributes != null && !attributes.isEmpty()) {
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

    /**
     * Checks if the given annotation type is a stereotype with a 'value' attribute.
     *
     * @param annotationType      the annotation type
     * @param metaAnnotationTypes the set of meta-annotation types
     * @param attrs               the attributes of the annotation
     * @return {@code true} if it is a stereotype with a 'value' attribute; otherwise, {@code false}
     */
    protected boolean isStereotypeWithNameValue(String annotationType, Set<String> metaAnnotationTypes, Map<String, Object> attrs) {
        var strAnnotationType = "com.petros.bringframework.context.annotation.Component";
        boolean isStereotype = annotationType.equals(strAnnotationType) ||
                metaAnnotationTypes.contains(strAnnotationType) ||
                annotationType.equals("jakarta.annotation.ManagedBean") ||
                annotationType.equals("jakarta.inject.Named");
        return isStereotype && attrs != null && attrs.containsKey("value");
    }

    /**
     * Builds a bean name for the given bean definition.
     *
     * @param beanDefinition the bean definition
     * @return the built bean name
     */
    protected String buildBeanName(BeanDefinition beanDefinition) {
        var beanClassName = beanDefinition.getBeanClassName();
        notBlank(beanClassName, "No bean class name set");
        return uncapitalizeAsProperty(getShortName(beanClassName));
    }
}
