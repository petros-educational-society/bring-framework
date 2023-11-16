package com.petros.bringframework;

import com.petros.bringframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import com.petros.bringframework.context.annotation.AnnotationConfigApplicationContext;
import com.petros.bringframework.service.MergeSort;

import java.util.Arrays;

public class BringDemo {
    public static void main(String[] args) {
        var registry = new SimpleBeanDefinitionRegistry();
        final var annotationConfigApplicationContext
                = new AnnotationConfigApplicationContext(registry, "com.petros.bringframework");

        var ms = annotationConfigApplicationContext
                .getBean(MergeSort.class);

        Integer[] arr = {5, 8, 0, 1, 4, -3};
        System.out.println("Before: " + Arrays.toString(arr));
        ms.sort(arr);
        System.out.println("After: " + Arrays.toString(arr));

    }
}
