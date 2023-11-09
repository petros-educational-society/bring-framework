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

    private final Class<?> clazz;

    public ReflectionClassMetadata(@Nonnull Class<?> clazz) {
        AssertUtils.notNull(clazz, "Class must not be null");
        this.clazz = clazz;
    }

    @Override
    public String getClassName() {
        return clazz.getName();
    }

    @Override
    public boolean isInterface() {
        return clazz.isInterface();
    }

    @Override
    public boolean isAnnotation() {
        return clazz.isAnnotation();
    }

    @Override
    public boolean isAbstract() {
        final int modifiers = clazz.getModifiers();
        return Modifier.isAbstract(modifiers);
    }

    @Override
    public boolean isFinal() {
        final int modifiers = clazz.getModifiers();
        return Modifier.isFinal(modifiers);
    }

    @Override
    public boolean isIndependent() {
        if (clazz.isMemberClass()) {
            // Check if the class is static, meaning it is a static nested class
            return Modifier.isStatic(clazz.getModifiers());
        } else {
            return !clazz.isLocalClass() && !clazz.isAnonymousClass();
        }
    }

    @Nullable
    @Override
    public String getEnclosingClassName() {
        final Class<?> enclosingClass = clazz.getEnclosingClass();
        return enclosingClass != null ? enclosingClass.getName() : null;
    }

    @Nullable
    @Override
    public String getSuperClassName() {
        final Class<?> superclass = clazz.getSuperclass();
        return superclass != null ? superclass.getName() : null;
    }

    @Override
    public String[] getInterfaceNames() {
        return Arrays.stream(clazz.getInterfaces())
                .map(Class::getName)
                .toArray(String[]::new);
    }

    @Override
    public String[] getMemberClassNames() {
        return Arrays.stream(clazz.getDeclaredClasses())
                .map(Class::getName)
                .toArray(String[]::new);
    }
}
