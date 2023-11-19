package com.petros.bringframework.beans.factory.support;

import com.petros.bringframework.beans.BeansException;

@FunctionalInterface
public interface SingletonFactory<T> {

    /**
     * Return an instance (possibly shared or independent)
     * of the object managed by this factory.
     *
     * @return the resulting instance
     * @throws BeansException in case of creation errors
     */
    T getObject() throws BeansException;

}