package com.petros.bringframework.beans.factory.support;

import javax.annotation.Nullable;

final class NullBean {

    NullBean() {
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return (this == other || other == null);
    }

    @Override
    public int hashCode() {
        return NullBean.class.hashCode();
    }

    @Override
    public String toString() {
        return "null";
    }

}
