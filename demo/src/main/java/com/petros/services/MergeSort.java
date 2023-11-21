package com.petros.services;

public interface MergeSort {
    <T extends Comparable<? super T>> void sort(T[] arr);
}
