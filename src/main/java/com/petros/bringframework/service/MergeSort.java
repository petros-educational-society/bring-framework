package com.petros.bringframework.service;

public interface MergeSort {
    <T extends Comparable<? super T>> void sort(T[] arr);
}
