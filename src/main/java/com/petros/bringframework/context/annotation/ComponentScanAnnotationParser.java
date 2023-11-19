package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.BeanDefinitionHolder;
import com.petros.bringframework.beans.factory.support.AnnotationAttributes;
import com.petros.bringframework.beans.factory.support.BeanDefinitionRegistry;
import com.petros.bringframework.util.ClassUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.petros.bringframework.util.ClassUtils.toStringArray;

/**
 * Parser for the @ComponentScan annotation.
 *
 * @author "Vasiuk Maryna"
 */
public class ComponentScanAnnotationParser {

    private final BeanDefinitionRegistry registry;

    public ComponentScanAnnotationParser(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public Set<BeanDefinitionHolder> parse(AnnotationAttributes componentScan, final String declaringClass) {
        SimpleClassPathBeanDefinitionScanner scanner = new SimpleClassPathBeanDefinitionScanner(this.registry);

        String[] basePackagesArray = componentScan.getStringArray("basePackages");
        List<String> basePackages = Arrays.asList(basePackagesArray);

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(declaringClass));
        }

        return scanner.doScan(toStringArray(basePackages));
    }
}
