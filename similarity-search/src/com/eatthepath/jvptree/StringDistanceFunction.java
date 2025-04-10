package com.eatthepath.jvptree;

import object.StringPoint;

public class StringDistanceFunction implements DistanceFunction<StringPoint> {

    public double getDistance(final StringPoint firstPoint, final StringPoint secondPoint) {
        return firstPoint.distance(secondPoint);
    }
}
