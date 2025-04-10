package com.eatthepath.jvptree;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


import com.eatthepath.jvptree.util.MedianDistanceThresholdSelectionStrategy;

public class VPTreeNodeTest {

    private static final int TEST_NODE_SIZE = 32;

    public void testVPNodeNoPoints() {
        new VPTreeNode<>(new ArrayList<Integer>(), new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Number, Integer>(), VPTree.DEFAULT_NODE_CAPACITY);
    }

    public void testVPNodeZeroCapacity() {
        new VPTreeNode<>(java.util.Collections.singletonList(7), new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Number, Integer>(), 0);
    }

    public void testSize() {
        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
        }
    }

    public void testAdd() {
        final Integer testPoint = TEST_NODE_SIZE * 2;

        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {

            testNode.add(testPoint);

        }
    }

    public void testRemove() {
        final Integer pointNotInNode = TEST_NODE_SIZE * 2;
        final Integer pointInNode = TEST_NODE_SIZE / 2;

        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {


            for (int i = 0; i < TEST_NODE_SIZE; i++) {
                testNode.remove(i);
            }

        }
    }

    public void testContains() {
        final Integer pointNotInNode = TEST_NODE_SIZE * 2;

        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            for (int i = 0; i < TEST_NODE_SIZE; i++) {
            }

        }
    }

    public void testRetainAll() {
        final ArrayList<Integer> pointsToRetain = new ArrayList<>();

        for (int i = 0; i < TEST_NODE_SIZE / 8; i++) {
            pointsToRetain.add(i);
        }

        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {

            for (final int point : pointsToRetain) {
            }

        }
    }

    public void testCollectNearestNeighbors() {
        final Integer queryPoint = TEST_NODE_SIZE / 2;
        final int numberOfNeighbors = 3;

        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            final NearestNeighborCollector<Number, Integer> collector =
                    new NearestNeighborCollector<>(queryPoint, new IntegerDistanceFunction(), numberOfNeighbors);

            testNode.collectNearestNeighbors(collector);

        }
    }

    public void testCollectAllWithinRange() {
        final Integer queryPoint = TEST_NODE_SIZE / 2;
        final int maxRange = TEST_NODE_SIZE / 8;

        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            final ArrayList<Integer> collectedPoints = new ArrayList<>();

            testNode.collectAllWithinDistance(queryPoint, maxRange, collectedPoints);


            for (int i = queryPoint - maxRange; i <= queryPoint + maxRange; i++) {
            }
        }
    }

    public void testAddPointsToArray() {
        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            final Integer[] array = new Integer[TEST_NODE_SIZE];
            testNode.addPointsToArray(array, 0);

        }
    }

    public void testCollectIterators() {
        for (final VPTreeNode<Number, Integer> testNode : this.createTestNodes(TEST_NODE_SIZE)) {
            final ArrayList<Iterator<Integer>> iterators = new ArrayList<>();
            testNode.collectIterators(iterators);

            final ArrayList<Integer> pointsFromIterators = new ArrayList<>();

            for (final Iterator<Integer> iterator : iterators) {
                while (iterator.hasNext()) {
                    pointsFromIterators.add(iterator.next());
                }
            }

        }
    }

    private Collection<VPTreeNode<Number, Integer>> createTestNodes(final int nodeSize) {
        final ArrayList<Integer> points = new ArrayList<>(nodeSize);

        for (int i = 0; i < nodeSize; i++) {
            points.add(i);
        }

        final ArrayList<VPTreeNode<Number, Integer>> testNodes = new ArrayList<>(3);

        testNodes.add(new VPTreeNode<>(points, new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Number, Integer>(), points.size() * 2));

        testNodes.add(new VPTreeNode<>(points, new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Number, Integer>(), points.size()));

        testNodes.add(new VPTreeNode<>(points, new IntegerDistanceFunction(),
                new MedianDistanceThresholdSelectionStrategy<Number, Integer>(), points.size() / 8));

        return testNodes;
    }
}
