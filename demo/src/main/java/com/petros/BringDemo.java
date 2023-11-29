package com.petros;

import com.petros.bringframework.context.annotation.AnnotationConfigApplicationContext;
import com.petros.config.JavaConfig;
import com.petros.services.SayHello;
import com.petros.services.Test;

import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

@Log4j2
public class BringDemo {
    public static void main(String[] args) {

        final var annotationConfigApplicationContext
//                = new AnnotationConfigApplicationContext( "com.petros");
                = new AnnotationConfigApplicationContext(JavaConfig.class);

        var ms = annotationConfigApplicationContext
                .getBean(Test.class);

        Integer[] arr = {5, 8, 0, 1, 4, -3};
        log.info("Before: {}", Arrays.toString(arr));
        ms.testMerge(arr);
        log.info("After: {}", Arrays.toString(arr));

        var sayHello = annotationConfigApplicationContext.getBean(SayHello.class);
        sayHello.print();
    }
}
