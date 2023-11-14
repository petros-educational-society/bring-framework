package com.petros.bringframework.service;

import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;

public class MergeSortRecursively implements MergeSort {
    @Override
    public <T extends Comparable<? super T>> void sort(T[] arr) {
        if (arr.length < 2) {
            return;
        }

        var left = copyOfRange(arr, 0, arr.length / 2);
        var right = copyOfRange(arr, arr.length / 2, arr.length);

        sort(left);
        sort(right);

        int l = 0, r = 0;
        while (l < left.length && r < right.length ) {
            arr[l + r] = left[l].compareTo(right[r]) < 1 ? left[l++] : right[r++];
        }

        arraycopy(left, l, arr, l + r, left.length - l);
        arraycopy(right, r, arr, l + r, right.length - r);
    }
}
