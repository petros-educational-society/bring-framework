package com.petros.bringframework.beans.config.beans;

import com.petros.bringframework.beans.factory.annotation.DestroyPlease;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DestroyPleaseAnnotationTestBean {

    @DestroyPlease
    public void after() {
        log.info("Good bye! Keep safe!");
    }
}
