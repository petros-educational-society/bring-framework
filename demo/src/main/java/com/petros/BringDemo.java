package com.petros;

import com.petros.bringframework.context.annotation.AnnotationConfigApplicationContext;
import com.petros.services.SayHello;
import com.petros.services.Test;
import com.petros.configuration.JavaConfig;

import java.util.Arrays;

public class BringDemo {
    public static void main(String[] args) {

        final var annotationConfigApplicationContext
//                = new AnnotationConfigApplicationContext( "com.petros");
                = new AnnotationConfigApplicationContext(JavaConfig.class);

        var ms = annotationConfigApplicationContext
                .getBean(Test.class);

        Integer[] arr = {5, 8, 0, 1, 4, -3};
        System.out.println("Before: " + Arrays.toString(arr));
        ms.testMerge(arr);
        System.out.println("After: " + Arrays.toString(arr));


        var sayHello = annotationConfigApplicationContext.getBean(SayHello.class);
        sayHello.print();
    }
}
