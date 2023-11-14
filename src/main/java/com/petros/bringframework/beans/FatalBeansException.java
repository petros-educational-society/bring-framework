package com.petros.bringframework.beans;

import javax.annotation.Nullable;

public class FatalBeansException extends BeansException {

    public FatalBeansException(String msg) {
        super(msg);
    }

    public FatalBeansException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }

}
