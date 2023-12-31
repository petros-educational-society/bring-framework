package com.petros.bringframework.beans.factory.config;

import javax.annotation.Nullable;

/**
 * Interface that defines abstract metadata of a specific class,
 * in a form that does not require that class to be loaded yet.
 *
 * @author "Maksym Oliinyk"
 */
public interface ClassMetadata {

    /**
     * Return the name of the underlying class.
     */
    String getClassName();

    /**
     * Return whether the underlying class represents an interface.
     */
    boolean isInterface();

    /**
     * Return whether the underlying class represents an annotation.
     *
     * @since 4.1
     */
    boolean isAnnotation();

    /**
     * Return whether the underlying class is marked as abstract.
     */
    boolean isAbstract();

    /**
     * Return whether the underlying class represents a concrete class,
     * i.e. neither an interface nor an abstract class.
     */
    default boolean isConcrete() {
        return !(isInterface() || isAbstract());
    }

    /**
     * Return whether the underlying class is marked as 'final'.
     */
    boolean isFinal();

    /**
     * Determine whether the underlying class is independent, i.e. whether
     * it is a top-level class or a nested class (static inner class) that
     * can be constructed independently of an enclosing class.
     */
    boolean isIndependent();

    /**
     * Return whether the underlying class is declared within an enclosing
     * class (i.e. the underlying class is an inner/nested class or a
     * local class within a method).
     * <p>If this method returns {@code false}, then the underlying
     * class is a top-level class.
     */
    default boolean hasEnclosingClass() {
        return (getEnclosingClassName() != null);
    }

    /**
     * Return the name of the enclosing class of the underlying class,
     * or {@code null} if the underlying class is a top-level class.
     */
    @Nullable
    String getEnclosingClassName();

    /**
     * Return whether the underlying class has a superclass.
     */
    default boolean hasSuperClass() {
        return (getSuperClassName() != null);
    }

    /**
     * Return the name of the superclass of the underlying class,
     * or {@code null} if there is no superclass defined.
     */
    @Nullable
    String getSuperClassName();

    /**
     * Return the names of all interfaces that the underlying class
     * implements, or an empty array if there are none.
     */
    String[] getInterfaceNames();

    /**
     * Return the names of all classes declared as members of the class represented by
     * this ClassMetadata object. This includes public, protected, default (package)
     * access, and private classes and interfaces declared by the class, but excludes
     * inherited classes and interfaces. An empty array is returned if no member classes
     * or interfaces exist.
     */
    String[] getMemberClassNames();

}