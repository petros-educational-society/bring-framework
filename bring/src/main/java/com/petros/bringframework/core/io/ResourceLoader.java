package com.petros.bringframework.core.io;

import javax.annotation.Nullable;

public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * Return a {@code Resource} handle for the specified resource location
     */
    Resource getResource(String location);

    /**
     * Expose the {@link ClassLoader} used by this {@code ResourceLoader}
     */
    @Nullable
    ClassLoader getClassLoader();

}