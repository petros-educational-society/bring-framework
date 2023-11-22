package com.petros.bringframework.service;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class MergeSortTask<T extends Comparable<? super T>> extends RecursiveAction {

    private final T[] arr;
    private final int n;

    public MergeSortTask(T[] arr) {
        this.arr = arr;
        this.n = arr.length;
    }

    @Override
    protected void compute() {
        if (n < 2) {
            return;
        }

        T[] left = Arrays.copyOfRange(arr, 0, n / 2);
        T[] right = Arrays.copyOfRange(arr, n / 2, n);

        var leftTask = new MergeSortTask<>(left);
        var rightTask = new MergeSortTask<>(right);

        leftTask.fork();
        rightTask.compute();
        leftTask.join();

        int l = 0, r = 0;
        while (l < left.length && r < right.length) {
            arr[l + r] = left[l].compareTo(right[r]) < 0 ? left[l++] : right[r++];
        }
        if (left.length - l != 0) {
            System.arraycopy(left, l, arr, l + r, left.length - l);
            return;
        }
        if (right.length - l != 0) {
            System.arraycopy(right, r, arr, l + r, right.length - r);
        }
    }
}
