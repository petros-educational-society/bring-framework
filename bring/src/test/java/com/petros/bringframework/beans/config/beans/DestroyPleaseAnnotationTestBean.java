package com.petros.bringframework.beans.config.beans;

import com.petros.bringframework.beans.factory.annotation.DestroyPlease;

public class DestroyPleaseAnnotationTestBean {

    @DestroyPlease
    public void after() {
        System.out.println("Good bye! Keep safe!");
    }
}
