package spell_checker.metric_based;

import metric.EditDistance;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import spell_checker.SpellChecking;
import query.result.RangeQueryResult;
import query.result.Result;
import query.result.TopKResult;

/**
 * @author Anatoly Borisov
 */
public class VpTreeE implements SpellChecking {

    // The following condition must held:
    // MAX_LEAF_SIZE >= VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT
    private int VANTAGE_POINT_CANDIDATES = 3;
    private int TEST_POINT_COUNT = 5;
    private int MAX_LEAF_SIZE = VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT;

    VpTreeNode1 root;

    public static int search_count;


    public int clusterSizeAcum = 0;
    public int numberOfClusters = 0;
    public int numberOfNodes = 0;
    public int maxLevel = 0;
    public int maxArity = 0;
    
    int maxDist = 99;

    public VpTreeE(int vantageStrings, int testStrings, int maxDist) {
        VANTAGE_POINT_CANDIDATES = vantageStrings;
        TEST_POINT_COUNT = testStrings;
        //if (leaf_size >= VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT )
        //else MAX_LEAF_SIZE = VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT;
        
        this.maxDist = maxDist;
    }

    public VpTreeE(int maxLeaf, int maxDist) {
        MAX_LEAF_SIZE = maxLeaf;
        this.maxDist = maxDist;
    }

    @Override
    public void addWords(List<String> words) {
        root = new VpTreeNode1();
        root.median = 30;
        root.points = new ArrayList<>();

        for (int x = 0; x < words.size(); x++) {
            root.points.add((String) words.get(x));
        }
        root.buildTreeNode(null);
    }

    @Override
    public List<String> search(String query, int maxD) {
        RangeQueryResult result = new RangeQueryResult(maxD);
        search1(query, result);
        return result.toArray();
    }

    @Override
    public List<String> topKSearch(String query, int topK) {
        TopKResult result = new TopKResult(topK);
        result.maxDist = maxDist;
        search2(query, result);
        return result.toArray();
    }

    @Override
    public String getName() {
        return "VP-Tree";
    }

    public void search1(String point, Result result) {

        if (root != null) {
            root.findNearbyStrings(point, null, -1, result);
        }

    }

    private void search2(String point, Result result) {

        Queue<VpTreeNode1> nodes = new PriorityQueue<>();
        root.d = 0;
        root.rank = 0;
        nodes.add(root);

        while (!nodes.isEmpty()) {

            VpTreeNode1 metaNode = nodes.poll();

            if (metaNode.left == null) {

                int distanceToLeftCenter = (int) EditDistance.distance(metaNode.vantageString,point);
                result.addWord(metaNode.vantageString, distanceToLeftCenter);
                metaNode.clusterSearch(point, result, -1);

                continue;
            }

            int distanceToLeftCenter = (int) EditDistance.distance(metaNode.vantageString, point);
            result.addWord(metaNode.vantageString, distanceToLeftCenter);
            //metaNode.left.d = distanceToLeftCenter;
            metaNode.left.rank = Math.abs(distanceToLeftCenter - (metaNode.left.median/2));
            //metaNode.right.d = distanceToLeftCenter;
            metaNode.right.rank = Math.abs(distanceToLeftCenter - (metaNode.left.median + metaNode.left.median/2));

            //double distanceToLeftCenter = vantageString.distance(point);
            if (distanceToLeftCenter + result.maxDist < metaNode.leftRadius) {
                nodes.add(metaNode.left);
            } else if (distanceToLeftCenter - result.maxDist >= metaNode.leftRadius) {
                nodes.add(metaNode.right);
            } else {
                nodes.add(metaNode.right);
                nodes.add(metaNode.left);
            }

        }

    }

    public class VpTreeNode1 implements Comparable<VpTreeNode1> {

        private VpTreeNode1 left = null;
        private VpTreeNode1 right = null;
        private String vantageString = null;
        public double distance2Parent = 0;
        private double leftRadius = 0;

        int level = 0;

        public int median;
        public double d;
        public int rank;

        public List<String> points;
        //public List<Double> distances_;

        public VpTreeNode1() {

        }

        public void clusterSearch(String point, Result result, double dist2Parent) {
            for (int i = 0; i < points.size(); i++) {
                String p = points.get(i);
                int curDist_ = (int) EditDistance.distance(p, point);
                if (curDist_ <= result.maxDist) {
                    result.addWord(p, curDist_);
                }
            }
        }

        public void findNearbyStrings(String point, String parentString, double dist2Parent, Result result) {

            
            int distanceToLeftCenter = (int) EditDistance.distance(this.vantageString, point);
            result.addWord(this.vantageString, distanceToLeftCenter);
            
            if (left == null) {
                clusterSearch(point, result, dist2Parent);
                return;
            }

            
            //double distanceToLeftCenter = vantageString.distance(point);
            if (distanceToLeftCenter + result.maxDist < leftRadius) {
                left.findNearbyStrings(point, vantageString, distanceToLeftCenter, result);
            } else if (distanceToLeftCenter - result.maxDist >= leftRadius) {
                right.findNearbyStrings(point, vantageString, distanceToLeftCenter, result);
            } else {
                right.findNearbyStrings(point, vantageString, distanceToLeftCenter, result);
                left.findNearbyStrings(point, vantageString, distanceToLeftCenter, result);
            }
        }

        /**
         * List must not be modified after node creation!
         */
        private void buildTreeNode(String vp) {

            if (!(points.size() >= MAX_LEAF_SIZE)) {
                vantageString = points.remove(0);
                return;
            }

            String baseString = chooseNewVantageString(points);

            points.remove(baseString);
            if (points.isEmpty()) {
                vantageString = baseString;
                return;
            }

            double distances[] = new double[points.size()];
            double sortedDistances[] = new double[points.size()];

            for (int i = 0; i < points.size(); ++i) {
                String p = points.get(i);
                distances[i] = EditDistance.distance(baseString, p);
                //p.setDist(distances[i]);
                sortedDistances[i] = distances[i];
            }

            Arrays.sort(sortedDistances);

            ArrayList<String> leftStrings = new ArrayList<>();
            //ArrayList<Double> leftDistances = new ArrayList<>();
            ArrayList<String> rightStrings = new ArrayList<>();
            //ArrayList<Double> rightDistances = new ArrayList<>();
            double medianDistance = 0;
            double maxLeftDistance = -1;
            double maxRightDistance = -1;

            double leftMedian = 0;
            double rightMedian = 0;
            int leftCount = 0;
            int rightCount = 0;
            
            medianDistance = sortedDistances[sortedDistances.length / 2];
            
            for (int i = distances.length - 1; i >= 0; i--) {
                if (distances[i] < medianDistance) {
                    leftMedian+=distances[i];
                    leftCount++;
                    leftStrings.add(points.get(i));
                    //leftDistances.add(distances[i]);
                    if (distances[i] > maxLeftDistance) {
                        maxLeftDistance = distances[i];
                    }
                } else {
                    rightMedian+=distances[i];
                    rightCount++;
                    rightStrings.add(points.get(i));
                    //rightDistances.add(distances[i]);
                    if (distances[i] > maxRightDistance) {
                        maxRightDistance = distances[i];
                    }
                }
                points.remove(i);
            }
//            if (distances_ != null) {
//                distances_.clear();
//            }

            vantageString = baseString;
            leftRadius = medianDistance;

            if (leftStrings.isEmpty() || rightStrings.isEmpty()) {
                if (leftStrings.size() > 0) {
                    this.points = leftStrings;
                    //this.distances_ = leftDistances;
                } else {
                    this.points = rightStrings;
                    //this.distances_ = rightDistances;
                }
                return;
            }

            left = new VpTreeNode1();
            left.median = (int) medianDistance;
            left.level = level + 1;
            right = new VpTreeNode1();
            right.median = this.median;
            right.level = level + 1;

            if (left.level > maxLevel) {
                maxLevel = left.level;
            }

            left.points = leftStrings;
            //left.distances_ = leftDistances;
            //left.maxDist = middleString.medianDistance;
            //if (middleString.medianDistance<5)
            //System.out.println(middleString.medianDistance);
            left.buildTreeNode(vantageString);
            //if (left.vantageString != null) {
            //  left.distance2Parent = left.vantageString.distance(baseString);
            //}

            right.points = rightStrings;
            //right.distances_ = rightDistances;
            //right.maxDist = middleString.medianDistance;
            right.buildTreeNode(vantageString);
            //if (right.vantageString != null) {
            //  right.distance2Parent = right.vantageString.distance(baseString);
            //}

        }

        @Override
        public int compareTo(VpTreeNode1 o) {

            return Integer.compare(rank, o.rank);

            /*
            int comp = Double.compare(rank, o.rank);
            if (comp != 0) {
                return comp;
            }

            return Double.compare(node.factor, (o.node.factor));
             */
        }

        /**
         * Trying to choose a new vantage point with highest distance deviation
         * to other nodes.
         */
        private String chooseNewVantageString(List<String> points) {
            ArrayList<String> candidates = new ArrayList<String>(VANTAGE_POINT_CANDIDATES);
            ArrayList<String> testStrings = new ArrayList<String>(TEST_POINT_COUNT);

            int offset = points.size() / VANTAGE_POINT_CANDIDATES;

            for (int i = 0; i < VANTAGE_POINT_CANDIDATES; ++i) {
                //int baseStringIndex = (int) (Math.random() * points.size());
                int baseStringIndex = i * offset;
                String candidate = points.get(baseStringIndex);
                candidates.add(candidate);
            }

            String bestBaseString = points.get(0);
            double bestBaseStringSigma = 0;

            for (String baseString : candidates) {
                double distances[] = new double[points.size()];
                for (int i = 0; i < distances.length; ++i) {
                    distances[i] = EditDistance.distance(baseString, points.get(i));
                }
                double sigma = sigmaSquare(distances);
                if (sigma > bestBaseStringSigma) {
                    bestBaseStringSigma = sigma;
                    bestBaseString = baseString;
                }
            }

            return bestBaseString;
        }

        /**
         * Trying to choose a new vantage point with highest distance deviation
         * to other nodes.
         */
        private String chooseNewVantageString2(List<String> points) {
            ArrayList<String> candidates = new ArrayList<String>(VANTAGE_POINT_CANDIDATES);
            ArrayList<String> testStrings = new ArrayList<String>(TEST_POINT_COUNT);

            for (int i = 0; i < VANTAGE_POINT_CANDIDATES; ++i) {
                int baseStringIndex = i + (int) (Math.random() * (points.size() - i));
                String candidate = points.get(baseStringIndex);
                points.set(baseStringIndex, points.get(i));
                points.set(i, candidate);
                candidates.add(candidate);
            }

            for (int i = VANTAGE_POINT_CANDIDATES; i < VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT; ++i) {
                int testStringIndex = i + (int) (Math.random() * (points.size() - i));
                String testString = points.get(testStringIndex);
                points.set(testStringIndex, points.get(i));
                points.set(i, testString);
                testStrings.add(testString);
            }

            String bestBaseString = points.get(0);
            double bestBaseStringSigma = 0;

            for (String baseString : candidates) {
                double distances[] = new double[TEST_POINT_COUNT];
                for (int i = 0; i < TEST_POINT_COUNT; ++i) {
                    distances[i] = EditDistance.distance(baseString, testStrings.get(i));
                }
                double sigma = sigmaSquare(distances);
                if (sigma > bestBaseStringSigma) {
                    bestBaseStringSigma = sigma;
                    bestBaseString = baseString;
                }
            }

            return bestBaseString;
        }

        private double sigmaSquare(double[] values) {

            double avg = avg(values);
            double sigmaSq = 0;

            for (double value : values) {
                double dev = value - avg;
                sigmaSq += dev * dev;
            }

            return sigmaSq;
        }

        private double avg(double[] values) {
            double sum = 0;

            for (double value : values) {
                sum += value;
            }

            double avg = sum / values.length;

            return avg;
        }

        private double max(double[] values) {
            double max = 0;

            for (double value : values) {

                if (value > max) {
                    max = value;
                }
            }

            return max;
        }

        /**
         * Trying to choose a new vantage point with smallest distance deviation
         * to other nodes.
         */
        private BestString chooseNewVantageString3(List<String> points) {
            ArrayList<String> candidates = new ArrayList<String>(VANTAGE_POINT_CANDIDATES);
            ArrayList<String> testStrings = new ArrayList<String>(TEST_POINT_COUNT);

            int offset = points.size() / VANTAGE_POINT_CANDIDATES;

            for (int i = 0; i < VANTAGE_POINT_CANDIDATES; ++i) {
                //int baseStringIndex = (int) (Math.random() * points.size());
                int baseStringIndex = i * offset;
                String candidate = points.get(baseStringIndex);
                candidates.add(candidate);
            }

            BestString bp = new BestString();
            bp.bestString = points.get(0);
            bp.sigmaDistance = Double.MAX_VALUE;

            for (String baseString : candidates) {
                double distances[] = new double[points.size()];
                for (int i = 0; i < distances.length; ++i) {
                    distances[i] = EditDistance.distance(baseString, points.get(i));
                }
                double avg = max(distances);
                //double sigma = sigmaSquare(distances);
                if (avg < bp.sigmaDistance) {
                    bp.sigmaDistance = avg;
                    bp.bestString = baseString;
                    bp.medianDistance = avg;
                }
            }

            return bp;
        }
    }

    class BestString {

        String bestString;
        double sigmaDistance;
        double medianDistance;

    }
}
