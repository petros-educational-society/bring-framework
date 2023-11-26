package com.petros.services;

import com.petros.bringframework.beans.factory.annotation.InitPlease;
import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.context.annotation.Primary;

import java.util.concurrent.ForkJoinPool;

@Primary
@Component
public class ForkJoinPoolBasedRecursiveMergeSort implements MergeSort {

    @InitPlease
    public void init() {
        System.out.println("ForkJoinPoolBasedRecursiveMergeSort is a primary implementation of MergeSort");
    }

    @Override
    public <T extends Comparable<? super T>> void sort(T[] arr) {
        ForkJoinPool.commonPool().invoke(new MergeSortTask<>(arr));
    }
}
