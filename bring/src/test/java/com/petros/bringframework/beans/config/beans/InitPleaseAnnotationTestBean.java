package com.petros.bringframework.beans.config.beans;

import com.petros.bringframework.beans.factory.annotation.InitPlease;

public class InitPleaseAnnotationTestBean {

    @InitPlease
    public void init() {
        System.out.println("To begin, let me say:");
    }
}
