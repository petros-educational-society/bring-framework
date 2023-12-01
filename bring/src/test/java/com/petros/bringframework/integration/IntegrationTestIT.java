package com.petros.bringframework.integration;

import com.petros.bringframework.context.annotation.AnnotationConfigApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class IntegrationTestIT {
    @Test
    void contextShouldStarted() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.petros.bringframework");

    }


}
