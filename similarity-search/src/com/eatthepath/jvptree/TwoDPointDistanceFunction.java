package com.eatthepath.jvptree;

import trie.Tester;
import trie.StringPoint;
import trie.Tester;
import trie.TwoDPoint;

public class TwoDPointDistanceFunction implements DistanceFunction<TwoDPoint> {

    public double getDistance(final TwoDPoint firstPoint, final TwoDPoint secondPoint) {
        Tester.search_count++;
        return firstPoint.distance(secondPoint);
    }
}
