package com.petros.bringframework.beans.factory.config;

/**
 * @author "Maksym Oliinyk"
 */
public interface MethodMetadata extends AnnotatedTypeMetadata {
    /**
     * Get the name of the underlying method.
     */
    String getMethodName();

    /**
     * Get the fully-qualified name of the class that declares the underlying method.
     */
    String getDeclaringClassName();

    /**
     * Get the fully-qualified name of the underlying method's declared return type.
     *
     * @since 4.2
     */
    String getReturnTypeName();

    /**
     * Determine whether the underlying method is effectively abstract:
     * i.e. marked as abstract in a class or declared as a regular,
     * non-default method in an interface.
     *
     * @since 4.2
     */
    boolean isAbstract();

    /**
     * Determine whether the underlying method is declared as 'static'.
     */
    boolean isStatic();

    /**
     * Determine whether the underlying method is marked as 'final'.
     */
    boolean isFinal();

    /**
     * Determine whether the underlying method is overridable,
     * i.e. not marked as static, final, or private.
     */
    boolean isOverridable();
}
