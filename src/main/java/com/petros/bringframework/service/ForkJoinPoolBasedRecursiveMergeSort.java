package com.petros.bringframework.service;

import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.context.annotation.Primary;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;

@Primary
@Component
public class ForkJoinPoolBasedRecursiveMergeSort implements MergeSort {
    @Override
    public <T extends Comparable<? super T>> void sort(T[] arr) {
        ForkJoinPool.commonPool().invoke(new MergeSortTask<>(arr));
    }
}
