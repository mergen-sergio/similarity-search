package com.eatthepath.jvptree;


import java.util.ArrayList;


public class NearestNeighborCollectorTest {

    private NearestNeighborCollector<Number, Integer> collector;

    private static final int CAPACITY = 5;

    public void setup() {
        this.collector = new NearestNeighborCollector<>(0, new IntegerDistanceFunction(), CAPACITY);
    }

    public void testOfferPointAndGetFarthestPoint() {

        this.collector.offerPoint(17);

        this.collector.offerPoint(2);

        this.collector.offerPoint(19);

        for (int i = 0; i < CAPACITY; i++) {
            this.collector.offerPoint(3);
        }


        for (int i = 0; i < CAPACITY; i++) {
            this.collector.offerPoint(20);
        }


    }

    public void testToSortedList() {

        this.collector.offerPoint(19);
        this.collector.offerPoint(77);
        this.collector.offerPoint(4);
        this.collector.offerPoint(1);
        this.collector.offerPoint(2);
        this.collector.offerPoint(62);
        this.collector.offerPoint(8375);
        this.collector.offerPoint(3);
        this.collector.offerPoint(5);
        this.collector.offerPoint(5);

        final ArrayList<Integer> expectedList = new ArrayList<>();
        java.util.Collections.addAll(expectedList, 1, 2, 3, 4, 5);

    }
}
