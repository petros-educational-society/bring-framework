package com.petros.bringframework.service;

import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import com.petros.bringframework.context.annotation.Component;
import lombok.NoArgsConstructor;

/**
 * @author "Maksym Oliinyk"
 */
@Component
@NoArgsConstructor
public class TestImpl
        implements Test {

    private MergeSort mergeSort;


    @InjectPlease
    public TestImpl(MergeSort mergeSort) {
        this.mergeSort = mergeSort;
    }


    @Override
    public void testMerge(Integer[] arr) {
        mergeSort.sort(arr);
    }

}
