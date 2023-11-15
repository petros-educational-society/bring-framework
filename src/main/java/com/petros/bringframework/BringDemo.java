package com.petros.bringframework;

import com.petros.bringframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import com.petros.bringframework.context.annotation.AnnotationConfigApplicationContext;
import com.petros.bringframework.service.MergeSortRecursively;

import java.util.Arrays;

public class BringDemo {
    public static void main(String[] args) {
        var registry = new SimpleBeanDefinitionRegistry();
        var ms = new AnnotationConfigApplicationContext(registry, "com.petros.bringframework")
                .getBean(MergeSortRecursively.class);

        Integer[] arr = {5, 8, 0, 1, 4, -3};
        System.out.println("Before: " + Arrays.toString(arr));
        ms.sort(arr);
        System.out.println("After: " + Arrays.toString(arr));

    }
}
