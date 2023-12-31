package com.petros.services;

import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.context.annotation.DependsOn;import lombok.NoArgsConstructor;

/**
 * @author "Maksym Oliinyk"
 */
@Component
@DependsOn({"forkJoinPoolBasedRecursiveMergeSort"})
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
