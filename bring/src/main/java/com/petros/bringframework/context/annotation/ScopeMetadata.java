package com.petros.bringframework.context.annotation;

import com.petros.bringframework.beans.factory.config.BeanDefinition;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Describes scope characteristics for a Bring-managed bean including the scope name and the scoped-proxy behavior.
 *
 * @author "Maksym Oliinyk"
 */
@Data
public class ScopeMetadata {

    private String scopeName = BeanDefinition.SCOPE_SINGLETON;
    private ScopedProxyMode scopedProxyMode = ScopedProxyMode.NO;

}
