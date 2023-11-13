package com.petros.bringframework.beans.factory.config;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MethodMetadata implementation that uses standard reflection to introspect a given Method.
 *
 * @author "Maksym Oliinyk"
 */
public class ReflectionMethodMetadata implements MethodMetadata {

    private final Method introspectedMethod;
    private final Set<Annotation> annotations;

    public ReflectionMethodMetadata(final Method introspectedMethod) {
        this.introspectedMethod = introspectedMethod;
        this.annotations = Arrays.stream(introspectedMethod.getDeclaredAnnotations()).collect(Collectors.toSet());
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Nullable
    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        Map<String, Object> attributes = new HashMap<>();

        Annotation annotation = getAnnotation(annotationName);
        if (annotation != null) {
            for (Method method : annotation.annotationType().getDeclaredMethods()) {
                try {
                    Object value = method.invoke(annotation);
                    attributes.put(method.getName(), value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return attributes;
    }

    @Override
    public String getMethodName() {
        return introspectedMethod.getName();
    }

    @Override
    public String getDeclaringClassName() {
        return this.introspectedMethod.getDeclaringClass().getName();
    }

    @Override
    public String getReturnTypeName() {
        return this.introspectedMethod.getReturnType().getName();
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this.introspectedMethod.getModifiers());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(this.introspectedMethod.getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedMethod.getModifiers());
    }

    @Override
    public boolean isOverridable() {
        return !isStatic() && !isFinal() && !isPrivate();
    }


    private boolean isPrivate() {
        return Modifier.isPrivate(this.introspectedMethod.getModifiers());
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return (this == other || (other instanceof ReflectionMethodMetadata that &&
                this.introspectedMethod.equals(that.introspectedMethod)));
    }

    @Override
    public int hashCode() {
        return this.introspectedMethod.hashCode();
    }

}