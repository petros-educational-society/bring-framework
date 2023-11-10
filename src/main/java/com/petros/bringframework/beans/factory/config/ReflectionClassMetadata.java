package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.core.AssertUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * @author "Maksym Oliinyk"
 */
public class ReflectionClassMetadata implements ClassMetadata {

    protected final Class<?> introspectedClass;

    public ReflectionClassMetadata(@Nonnull Class<?> introspectedClass) {
        AssertUtils.notNull(introspectedClass, "Class must not be null");
        this.introspectedClass = introspectedClass;
    }

    /**
     * Return the underlying Class.
     */
    public final Class<?> getIntrospectedClass() {
        return this.introspectedClass;
    }

    @Override
    public String getClassName() {
        return introspectedClass.getName();
    }

    @Override
    public boolean isInterface() {
        return introspectedClass.isInterface();
    }

    @Override
    public boolean isAnnotation() {
        return introspectedClass.isAnnotation();
    }

    @Override
    public boolean isAbstract() {
        final int modifiers = introspectedClass.getModifiers();
        return Modifier.isAbstract(modifiers);
    }

    @Override
    public boolean isFinal() {
        final int modifiers = introspectedClass.getModifiers();
        return Modifier.isFinal(modifiers);
    }

    @Override
    public boolean isIndependent() {
        if (introspectedClass.isMemberClass()) {
            // Check if the class is static, meaning it is a static nested class
            return Modifier.isStatic(introspectedClass.getModifiers());
        } else {
            return !introspectedClass.isLocalClass() && !introspectedClass.isAnonymousClass();
        }
    }

    @Nullable
    @Override
    public String getEnclosingClassName() {
        final Class<?> enclosingClass = introspectedClass.getEnclosingClass();
        return enclosingClass != null ? enclosingClass.getName() : null;
    }

    @Nullable
    @Override
    public String getSuperClassName() {
        final Class<?> superclass = introspectedClass.getSuperclass();
        return superclass != null ? superclass.getName() : null;
    }

    @Override
    public String[] getInterfaceNames() {
        return Arrays.stream(introspectedClass.getInterfaces())
                .map(Class::getName)
                .toArray(String[]::new);
    }

    @Override
    public String[] getMemberClassNames() {
        return Arrays.stream(introspectedClass.getDeclaredClasses())
                .map(Class::getName)
                .toArray(String[]::new);
    }
}
