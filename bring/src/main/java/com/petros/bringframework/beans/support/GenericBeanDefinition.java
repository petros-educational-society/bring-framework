package com.petros.bringframework.beans.support;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.lang.reflect.Executable;

/**
 * @author "Maksym Oliinyk"
 */
@Getter
@Setter
public abstract class GenericBeanDefinition extends AbstractBeanDefinition {

    @Nullable
    private String parentName;

    /**resolved constructor*/
    @Nullable
    protected Executable resolvedConstructor;

    /** Package-visible field that marks the autowired constructor arguments as resolved. */
    protected boolean autowiredConstructorArgumentsResolved = false;

}
