package com.petros.bringframework.beans.support;

import com.petros.bringframework.core.type.ResolvableType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

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

    @Nullable
    volatile ResolvableType targetType;

    /** Specify the target type of this bean definition, if known in advance. */
    public void setTargetType(@Nullable Class<?> targetType) {
        this.targetType = (targetType != null ? ResolvableType.forRawClass(targetType) : null);
    }

    @SneakyThrows
    public void setTargetType(@Nullable String targetTypeName) {
        this.targetType = (targetType != null
                           ? ResolvableType.forRawClass(Class.forName(targetTypeName)) : null);
    }

}
