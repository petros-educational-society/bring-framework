package com.petros.bringframework.context.annotation;

/**
 * @author "Maksym Oliinyk"
 */
public record ScopeMetadata(String scopeName, ScopedProxyMode scopedProxyMode) {
    public String getScopeName() {
        return this.scopeName;
    }
}
