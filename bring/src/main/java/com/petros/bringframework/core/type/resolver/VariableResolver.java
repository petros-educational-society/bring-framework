package com.petros.bringframework.core.type.resolver;

import com.petros.bringframework.core.type.ResolvableType;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.TypeVariable;

/**
 * Strategy interface used to resolve {@link TypeVariable TypeVariables}.
 */
public interface VariableResolver extends Serializable {

    /**
     * Return the source of the resolver (used for hashCode and equals).
     */
    Object getSource();

    /**
     * Resolve the specified variable.
     *
     * @param variable the variable to resolve
     * @return the resolved variable, or {@code null} if not found
     */
    @Nullable
    ResolvableType resolveVariable(TypeVariable<?> variable);
}