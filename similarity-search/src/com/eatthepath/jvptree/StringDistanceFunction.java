package com.eatthepath.jvptree;

import trie.StringPoint;
import trie.Tester;

public class StringDistanceFunction implements DistanceFunction<StringPoint> {

    public double getDistance(final StringPoint firstPoint, final StringPoint secondPoint) {
        Tester.search_count++;
        return firstPoint.distance(secondPoint);
    }
}
