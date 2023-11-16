package com.petros.bringframework.core.type.resolver;

import com.petros.bringframework.core.type.ResolvableType;

import javax.annotation.Nullable;
import java.lang.reflect.TypeVariable;

public class DefaultVariableResolver implements VariableResolver {

    private final ResolvableType source;

    DefaultVariableResolver(ResolvableType resolvableType) {
        this.source = resolvableType;
    }

    @Override
    @Nullable
    public ResolvableType resolveVariable(TypeVariable<?> variable) {
        return this.source.resolveVariable(variable);
    }

    @Override
    public Object getSource() {
        return this.source;
    }
}