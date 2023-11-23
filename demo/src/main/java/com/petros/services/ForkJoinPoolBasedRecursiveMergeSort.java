package com.petros.services;

import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.context.annotation.Primary;

import java.util.concurrent.ForkJoinPool;

@Primary
@Component
public class ForkJoinPoolBasedRecursiveMergeSort implements MergeSort {
    @Override
    public <T extends Comparable<? super T>> void sort(T[] arr) {
        ForkJoinPool.commonPool().invoke(new MergeSortTask<>(arr));
    }
}
