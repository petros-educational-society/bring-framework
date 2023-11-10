package com.petros.bringframework.beans;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public abstract class BeanException extends RuntimeException {

    public BeanException(String msg) {
        super(msg);
    }

    public BeanException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }

    @Nullable
    public Throwable getRootCause() {
        var rootCauseAtomic = new AtomicReference<Throwable>();
        Stream.iterate(this.getCause(), cause -> cause != null && cause != rootCauseAtomic.get(), Throwable::getCause)
                .forEachOrdered(rootCauseAtomic::set);
        return rootCauseAtomic.get();
    }

    public Throwable getMostSpecificCause() {
        Throwable rootCause = getRootCause();
        return rootCause != null ? rootCause : this;
    }

    public boolean contains(@Nullable Class<?> exType) {
        if (exType == null) {
            return false;
        }

        if (exType.isInstance(this)) {
            return true;
        }

        var cause = getCause();
        if (cause == this) {
            return false;
        }

        if (cause instanceof BeanException ex) {
            return ex.contains(exType);
        }

        return Stream.iterate(cause, Objects::nonNull, Throwable::getCause)
                .filter(exType::isInstance).filter(c -> c.getCause() != c)
                .anyMatch(c -> true);
    }
}
