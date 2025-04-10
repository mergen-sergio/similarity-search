package com.eatthepath.jvptree;

import object.StringPoint;
import object.TwoDPoint;

public class TwoDPointDistanceFunction implements DistanceFunction<TwoDPoint> {

    public double getDistance(final TwoDPoint firstPoint, final TwoDPoint secondPoint) {
        return firstPoint.distance(secondPoint);
    }
}
