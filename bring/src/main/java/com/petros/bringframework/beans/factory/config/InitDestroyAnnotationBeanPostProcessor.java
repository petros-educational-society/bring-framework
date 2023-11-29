package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.beans.BeansException;
import com.petros.bringframework.beans.exception.BeanCreationException;
import com.petros.bringframework.beans.factory.annotation.DestroyPlease;
import com.petros.bringframework.beans.factory.annotation.InitPlease;
import com.petros.bringframework.util.ClassUtils;
import com.petros.bringframework.util.ReflectionUtils;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link com.petros.bringframework.beans.factory.config.AnnotationBeanPostProcessor} implementation
 * that invokes annotated init and destroy methods. Supports {@link com.petros.bringframework.beans.factory.annotation.InitPlease}
 * and {@link com.petros.bringframework.beans.factory.annotation.DestroyPlease}
 * as init annotation and destroy annotation, respectively.
 *
 * <p>Init and destroy annotations may be applied to methods of any visibility:
 * public, package-protected, protected, or private. Multiple such methods
 * may be annotated, but it is recommended to only annotate one single
 * init method and destroy method, respectively.
 *
 * @author "Vasiuk Maryna"
 */
@SuppressWarnings("serial")
@Log4j2
public class InitDestroyAnnotationBeanPostProcessor
        implements DestructionAwareBeanPostProcessor, Serializable {

    private final transient LifecycleMetadata emptyLifecycleMetadata =
            new LifecycleMetadata(Object.class, Collections.emptyList(), Collections.emptyList()) {
                @Override
                public void invokeInitMethods(Object target, String beanName) {
                }

                @Override
                public void invokeDestroyMethods(Object target, String beanName) {
                }

                @Override
                public boolean hasDestroyMethods() {
                    return false;
                }
            };

    @Nullable
    private Class<? extends Annotation> initAnnotationType;

    @Nullable
    private Class<? extends Annotation> destroyAnnotationType;

    @Nullable
    private final transient Map<Class<?>, LifecycleMetadata> lifecycleMetadataCache = new ConcurrentHashMap<>(256);

    public InitDestroyAnnotationBeanPostProcessor() {
        setInitAnnotationType(InitPlease.class);
        setDestroyAnnotationType(DestroyPlease.class);
    }


    /**
     * Specify the init annotation to check for, indicating initialization
     * methods to call after configuration of a bean.
     */
    public void setInitAnnotationType(Class<? extends Annotation> initAnnotationType) {
        this.initAnnotationType = initAnnotationType;
    }

    /**
     * Specify the destroy annotation to check for, indicating destruction
     * methods to call when the context is shutting down.
     */
    public void setDestroyAnnotationType(Class<? extends Annotation> destroyAnnotationType) {
        this.destroyAnnotationType = destroyAnnotationType;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        LifecycleMetadata metadata = findLifecycleMetadata(bean.getClass());
        try {
            metadata.invokeInitMethods(bean, beanName);
        } catch (InvocationTargetException ex) {
            throw new BeanCreationException(beanName, "Invocation of init method failed", ex.getTargetException());
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Failed to invoke init method", ex);
        }
        return bean;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        LifecycleMetadata metadata = findLifecycleMetadata(bean.getClass());
        try {
            metadata.invokeDestroyMethods(bean, beanName);
        } catch (InvocationTargetException ex) {
            String msg = "Destroy method on bean with name '" + beanName + "' threw an exception";
            log.warn(msg, ex.getTargetException());
        } catch (Throwable ex) {
            log.warn("Failed to invoke destroy method on bean with name '" + beanName + "'", ex);
        }
    }

    private LifecycleMetadata findLifecycleMetadata(Class<?> clazz) {
        if (this.lifecycleMetadataCache == null) {
            // Happens after deserialization, during destruction...
            return buildLifecycleMetadata(clazz);
        }
        // Quick check on the concurrent map first, with minimal locking.
        LifecycleMetadata metadata = this.lifecycleMetadataCache.get(clazz);
        if (metadata == null) {
            synchronized (this.lifecycleMetadataCache) {
                metadata = this.lifecycleMetadataCache.get(clazz);
                if (metadata == null) {
                    metadata = buildLifecycleMetadata(clazz);
                    this.lifecycleMetadataCache.put(clazz, metadata);
                }
                return metadata;
            }
        }
        return metadata;
    }

    private LifecycleMetadata buildLifecycleMetadata(final Class<?> clazz) {
        if (!ClassUtils.isCandidateClass(clazz, Arrays.asList(this.initAnnotationType, this.destroyAnnotationType))) {
            return this.emptyLifecycleMetadata;
        }

        List<LifecycleElement> initMethods = new ArrayList<>();
        List<LifecycleElement> destroyMethods = new ArrayList<>();
        Class<?> targetClass = clazz;

        do {
            final List<LifecycleElement> currInitMethods = new ArrayList<>();
            final List<LifecycleElement> currDestroyMethods = new ArrayList<>();

            ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                if (this.initAnnotationType != null && method.isAnnotationPresent(this.initAnnotationType)) {
                    LifecycleElement element = new LifecycleElement(method);
                    currInitMethods.add(element);
                    log.trace("Found init method on class [" + clazz.getName() + "]: " + method);
                }
                if (this.destroyAnnotationType != null && method.isAnnotationPresent(this.destroyAnnotationType)) {
                    currDestroyMethods.add(new LifecycleElement(method));
                    log.trace("Found destroy method on class [" + clazz.getName() + "]: " + method);
                }
            });

            initMethods.addAll(0, currInitMethods);
            destroyMethods.addAll(currDestroyMethods);
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);

        return (initMethods.isEmpty() && destroyMethods.isEmpty() ? this.emptyLifecycleMetadata :
                new LifecycleMetadata(clazz, initMethods, destroyMethods));
    }

    /**
     * Class representing information about annotated init and destroy methods.
     */
    private static class LifecycleMetadata {

        private final Class<?> targetClass;

        private final Collection<LifecycleElement> initMethods;

        private final Collection<LifecycleElement> destroyMethods;

        public LifecycleMetadata(Class<?> targetClass, Collection<LifecycleElement> initMethods,
                                 Collection<LifecycleElement> destroyMethods) {

            this.targetClass = targetClass;
            this.initMethods = initMethods;
            this.destroyMethods = destroyMethods;
        }

        public void invokeInitMethods(Object target, String beanName) throws Throwable {
            Collection<LifecycleElement> initMethodsToIterate = this.initMethods;
            if (!initMethodsToIterate.isEmpty()) {
                for (LifecycleElement element : initMethodsToIterate) {
                    log.trace("Invoking init method on bean '" + beanName + "': " + element.getMethod());
                    element.invoke(target);
                }
            }
        }

        public void invokeDestroyMethods(Object target, String beanName) throws Throwable {
            Collection<LifecycleElement> destroyMethodsToUse = this.destroyMethods;
            if (!destroyMethodsToUse.isEmpty()) {
                for (LifecycleElement element : destroyMethodsToUse) {
                    log.trace("Invoking destroy method on bean '" + beanName + "': " + element.getMethod());
                    element.invoke(target);
                }
            }
        }

        public boolean hasDestroyMethods() {
            return !this.destroyMethods.isEmpty();
        }
    }


    /**
     * Class representing injection information about an annotated method.
     */
    private static class LifecycleElement {

        private final Method method;

        private final String identifier;

        public LifecycleElement(Method method) {
            if (method.getParameterCount() != 0) {
                throw new IllegalStateException("Lifecycle method annotation requires a no-arg method: " + method);
            }
            this.method = method;
            this.identifier = (Modifier.isPrivate(method.getModifiers()) ?
                    ClassUtils.getQualifiedMethodName(method) : method.getName());
        }

        public Method getMethod() {
            return this.method;
        }

        public void invoke(Object target) throws Throwable {
            this.method.setAccessible(true);
            this.method.invoke(target, (Object[]) null);
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof LifecycleElement)) {
                return false;
            }
            LifecycleElement otherElement = (LifecycleElement) other;
            return (this.identifier.equals(otherElement.identifier));
        }

        @Override
        public int hashCode() {
            return this.identifier.hashCode();
        }
    }

}
