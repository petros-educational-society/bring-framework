package com.petros.bringframework.beans.factory.support;

import javax.annotation.Nullable;

/**
 * Class used to indicate a null object within the Bring framework.
 * A null object refers to an instance that represents "nothing" or "no value".
 * It is typically used in scenarios
 * where the absence of a valid object needs to be explicitly represented.
 *
 * @author "Vadym Vovk"
 */
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
