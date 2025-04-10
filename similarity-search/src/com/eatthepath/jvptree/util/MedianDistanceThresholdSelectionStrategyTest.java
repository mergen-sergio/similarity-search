package com.eatthepath.jvptree.util;


import java.util.ArrayList;
import java.util.List;


import com.eatthepath.jvptree.IntegerDistanceFunction;
import com.eatthepath.jvptree.util.MedianDistanceThresholdSelectionStrategy;

public class MedianDistanceThresholdSelectionStrategyTest {

    public void testSelectThreshold() {
        final MedianDistanceThresholdSelectionStrategy<Number, Integer> strategy =
                new MedianDistanceThresholdSelectionStrategy<>();

        {
            final List<Integer> singleIntegerList = new ArrayList<>();
            singleIntegerList.add(7);

        }

        {
            final List<Integer> multipleIntegerList = new ArrayList<>();
            multipleIntegerList.add(2);
            multipleIntegerList.add(9);
            multipleIntegerList.add(3);
            multipleIntegerList.add(1);
            multipleIntegerList.add(6);
            multipleIntegerList.add(4);
            multipleIntegerList.add(8);
            multipleIntegerList.add(5);
            multipleIntegerList.add(7);

        }
    }

    public void testSelectThresholdEmptyList() {
        new MedianDistanceThresholdSelectionStrategy<Number, Integer>().selectThreshold(
                new ArrayList<Integer>(), 0, new IntegerDistanceFunction());
    }
}
