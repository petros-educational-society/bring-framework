package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.AnnotatedBeanDefinition;
import com.petros.bringframework.beans.factory.config.AnnotatedTypeMetadata;

/**
 * Centralizes the logic for processing key annotations
 *
 * @author "Maksym Oliinyk"
 */
public abstract class AnnotationConfigUtils {

    public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
        processCommonDefinitionAnnotations(abd, abd.getMetadata());
    }

    static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, AnnotatedTypeMetadata metadata) {
//todo Add processing of annotations, for next fields
//        abd.setLazyInit(lazy.getBoolean("value"));
//        abd.setPrimary(true);
//        abd.setDependsOn(dependsOn.getStringArray("value"));
//        abd.setRole(role.getNumber("value").intValue());
//        abd.setDescription(description.getString("value"));

    }
}
