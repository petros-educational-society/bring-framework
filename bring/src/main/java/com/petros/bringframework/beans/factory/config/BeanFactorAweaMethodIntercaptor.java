package com.petros.bringframework.beans.factory.config;

import lombok.extern.log4j.Log4j2;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;

@Log4j2
public class BeanFactorAweaMethodIntercaptor {

    @RuntimeType
    public static Object intercept(@This Object self,
                                   @Origin Method method,
                                   @AllArguments Object[] args) throws Throwable {
        log.info("Intercepted method: {} ", method.getName());
        return null;
    }
}
