package com.petros.bringframework.beans.config.beans;

import com.petros.bringframework.beans.factory.annotation.InitPlease;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class InitPleaseAnnotationTestBean {

    @InitPlease
    public void init() {
        log.info("To begin, let me say:");
    }
}
