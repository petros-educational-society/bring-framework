package com.petros.bringframework.beans.config;

import javax.annotation.Nullable;

public interface FakeBeanDefinition {
    void setBeanClassName(@Nullable String beanClassName);

    @Nullable
    String getBeanClassName();

    void setScope(@Nullable String scope);

    @Nullable
    String getScope();
}
