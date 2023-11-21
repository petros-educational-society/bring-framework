package com.petros.bringframework;

import com.petros.bringframework.context.annotation.AnnotationConfigApplicationContext;
import com.petros.bringframework.service.Test;
import com.petros.configtest.HelloWorld;

import java.util.Arrays;

public class BringDemo {
    public static void main(String[] args) {

        final var annotationConfigApplicationContext
//                = new AnnotationConfigApplicationContext( "com.petros.bringframework");
                = new AnnotationConfigApplicationContext(JavaConfig.class);

        var ms = annotationConfigApplicationContext
                .getBean(Test.class);

        Integer[] arr = {5, 8, 0, 1, 4, -3};
        System.out.println("Before: " + Arrays.toString(arr));
        ms.testMerge(arr);
        System.out.println("After: " + Arrays.toString(arr));


        var helloWorld = annotationConfigApplicationContext
                .getBean(HelloWorld.class);
        helloWorld.print();
    }
}
