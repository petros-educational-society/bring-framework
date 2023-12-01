package com.petros.bringframework.beans.factory.config;

import com.petros.bringframework.core.AssertUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Implementation of the {@link ClassMetadata} interface that provides introspection
 * capabilities about a specific class using reflection.
 *
 * @see ClassMetadata
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

    /**
     * Retrieves the name of the introspected class.
     *
     * @return the name of the class
     */
    @Override
    public String getClassName() {
        return introspectedClass.getName();
    }

    /**
     * Checks if the introspected class is an interface.
     *
     * @return true if the class is an interface; false otherwise
     */
    @Override
    public boolean isInterface() {
        return introspectedClass.isInterface();
    }

    /**
     * Checks if the introspected class is an annotation.
     *
     * @return true if the class is an annotation; false otherwise
     */
    @Override
    public boolean isAnnotation() {
        return introspectedClass.isAnnotation();
    }

    /**
     * Checks if the introspected class is abstract.
     *
     * @return true if the class is abstract; false otherwise
     */
    @Override
    public boolean isAbstract() {
        final int modifiers = introspectedClass.getModifiers();
        return Modifier.isAbstract(modifiers);
    }

    /**
     * Checks if the introspected class is final.
     *
     * @return true if the class is final; false otherwise
     */
    @Override
    public boolean isFinal() {
        final int modifiers = introspectedClass.getModifiers();
        return Modifier.isFinal(modifiers);
    }

    /**
     * Checks if the introspected class is an independent, top-level class (not inner or nested).
     *
     * @return true if the class is independent; false otherwise
     */
    @Override
    public boolean isIndependent() {
        if (introspectedClass.isMemberClass()) {
            // Check if the class is static, meaning it is a static nested class
            return Modifier.isStatic(introspectedClass.getModifiers());
        } else {
            return !introspectedClass.isLocalClass() && !introspectedClass.isAnonymousClass();
        }
    }

    /**
     * Retrieves the name of the enclosing class, if this class is a nested or inner class.
     *
     * @return the name of the enclosing class or null if not nested
     */
    @Nullable
    @Override
    public String getEnclosingClassName() {
        final Class<?> enclosingClass = introspectedClass.getEnclosingClass();
        return enclosingClass != null ? enclosingClass.getName() : null;
    }

    /**
     * Retrieves the name of the superclass of the introspected class.
     *
     * @return the name of the superclass or null if no superclass exists
     */
    @Nullable
    @Override
    public String getSuperClassName() {
        final Class<?> superclass = introspectedClass.getSuperclass();
        return superclass != null ? superclass.getName() : null;
    }

    /**
     * Retrieves an array of interface names implemented by the introspected class.
     *
     * @return an array of interface names or an empty array if no interfaces are implemented
     */
    @Override
    public String[] getInterfaceNames() {
        return Arrays.stream(introspectedClass.getInterfaces())
                .map(Class::getName)
                .toArray(String[]::new);
    }

    /**
     * Retrieves an array of names of member classes declared within the introspected class.
     *
     * @return an array of member class names or an empty array if no member classes are declared
     */
    @Override
    public String[] getMemberClassNames() {
        return Arrays.stream(introspectedClass.getDeclaredClasses())
                .map(Class::getName)
                .toArray(String[]::new);
    }
}
