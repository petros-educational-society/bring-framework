package com.petros.bringframework.beans;

import javax.annotation.Nullable;

public class FatalBeanException extends BeanException {

    public FatalBeanException(String msg) {
        super(msg);
    }

    public FatalBeanException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }

}
