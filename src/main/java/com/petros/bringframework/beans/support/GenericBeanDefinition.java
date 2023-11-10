package com.petros.bringframework.beans.support;

import javax.annotation.Nullable;

/**
 * @author "Maksym Oliinyk"
 */
public abstract class GenericBeanDefinition extends AbstractBeanDefinition {

    @Nullable
    private String parentName;

    @Override
    public void setParentName(@Nullable String parentName) {
        this.parentName = parentName;
    }

    @Nullable
    @Override
    public String getParentName() {
        return parentName;
    }
}
