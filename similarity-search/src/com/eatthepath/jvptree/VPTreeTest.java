package com.eatthepath.jvptree;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class VPTreeTest {

    private static final int TEST_TREE_SIZE = 256;

    public void testGetNearestNeighbors() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        final Integer queryPoint = TEST_TREE_SIZE / 2;
        final int numberOfNeighbors = 3;

        final List<Integer> nearestNeighbors = vpTree.getNearestNeighbors(queryPoint, numberOfNeighbors);

    }

    public void testGetAllWithinRange() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        final Integer queryPoint = TEST_TREE_SIZE / 2;
        final int maxDistance = TEST_TREE_SIZE / 8;

        final List<Integer> pointsWithinRange = vpTree.getAllWithinDistance(queryPoint, maxDistance);


        for (int i = queryPoint - maxDistance; i <= queryPoint + maxDistance; i++) {
        }
    }

    public void testSize() {
        final ArrayList<Integer> points = new ArrayList<>();

        for (int i = 0; i < TEST_TREE_SIZE; i++) {
            points.add(i);
        }

        {
            final VPTree<Number, Integer> initiallyEmptyTree = new VPTree<>(new IntegerDistanceFunction());

            initiallyEmptyTree.addAll(points);


            initiallyEmptyTree.removeAll(points);

        }

        {
            final VPTree<Number, Integer> initiallyPopulatedTree = new VPTree<>(new IntegerDistanceFunction(), points);
        }
    }

    public void testIsEmpty() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);
        final Integer testPoint = 12;


        vpTree.add(testPoint);

        vpTree.remove(testPoint);
    }

    public void testAdd() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);
        final Integer testPoint = 12;


    }

    public void testAddAll() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);

        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

    }

    public void testRemove() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);
        final Integer testPoint = 12;


        vpTree.add(testPoint);
    }

    public void testRemoveAll() {
        final ArrayList<Integer> pointsToRemove = new ArrayList<>();

        for (int i = 0; i < TEST_TREE_SIZE; i += 2) {
            pointsToRemove.add(i);
        }

        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);


        for (final Integer point : pointsToRemove) {
        }

    }

    public void testRetainAll() {
        final ArrayList<Integer> pointsToRetain = new ArrayList<>();

        for (int i = 0; i < TEST_TREE_SIZE; i += 2) {
            pointsToRetain.add(i);
        }

        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);


        for (final Integer point : pointsToRetain) {
        }

    }

    public void testClear() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);


        vpTree.clear();
    }

    public void testContains() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(0);

        final Integer pointAdded = 12;
        final Integer pointNotAdded = 7;


        vpTree.add(pointAdded);

        vpTree.remove(pointAdded);
    }

    public void testContainsAll() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Number, Integer> vpTree = new VPTree<>(new IntegerDistanceFunction(), points);


        points.add(numberOfPoints + 1);
    }

    public void testIterator() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Number, Integer> vpTree = new VPTree<>(new IntegerDistanceFunction(), points);

        final ArrayList<Integer> pointsFromIterator = new ArrayList<>();
        final Iterator<Integer> iterator = vpTree.iterator();

        while (iterator.hasNext()) {
            pointsFromIterator.add(iterator.next());
        }

    }

    public void testToArray() {
        final int numberOfPoints = 256;
        final ArrayList<Integer> points = new ArrayList<>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            points.add(i);
        }

        final VPTree<Number, Integer> vpTree = new VPTree<>(new IntegerDistanceFunction(), points);
        final Object[] array = vpTree.toArray();


        for (final Object point : array) {
        }
    }

    public void testToArrayTArray() {
        final VPTree<Number, Integer> vpTree = this.createTestTree(TEST_TREE_SIZE);

        {
            final Integer[] array = vpTree.toArray(new Integer[0]);


            for (final Integer point : array) {
            }
        }

        {
            final Integer[] array = vpTree.toArray(new Integer[vpTree.size()]);


            for (final Integer point : array) {
            }
        }

        {
            final Integer[] array = vpTree.toArray(new Integer[vpTree.size() + 1]);


            for (int i = 0; i < vpTree.size(); i++) {
            }

        }
    }

    private VPTree<Number, Integer> createTestTree(final int numberOfPoints) {
        final List<Integer> points;

        if (numberOfPoints == 0) {
            points = null;
        } else {
            points = new ArrayList<>(numberOfPoints);

            for (int i = 0; i < numberOfPoints; i++) {
                points.add(i);
            }
        }

        return new VPTree<>(new IntegerDistanceFunction(), points);
    }
}
