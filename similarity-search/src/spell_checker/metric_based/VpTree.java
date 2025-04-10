package spell_checker.metric_based;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import object.Point;


/**
 * @author Anatoly Borisov
 */
public class VpTree implements MetricDistanceSearchTree {

    // The following condition must held:
    // MAX_LEAF_SIZE >= VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT
    private static final int MAX_LEAF_SIZE = 15;
    private static final int VANTAGE_POINT_CANDIDATES = 10;
    private static final int TEST_POINT_COUNT = 5;

    VpTreeNode1 root;
    
    Hashtable<Point, Double> map = new Hashtable<Point, Double>();

    
    @Override
    public void addNodes(ArrayList points) {
        
        root = new VpTreeNode1();
        root.points = new ArrayList<Point>();
        
        for (int x=0;x<points.size();x++){
            root.points.add((Point)points.get(x));
        }
        root.buildTreeNode();
        
    }

    @Override
    public void addNode(Point point) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList search(Point point, double distance) {
        
        ArrayList<Point> result = new ArrayList<Point>();
        
        if (root!=null)
            result = (ArrayList)root.findNearbyPoints(point,  null,-1, distance);
        
        return result;
    }

    @Override
    public String getStrategy() {
        return "VpTree";
    }
    
    public class VpTreeNode1{
    
    private VpTreeNode1 left = null;
    private VpTreeNode1 right = null;
    private Point vantagePoint = null;
    public double distance2Parent = 0;
    private double leftRadius = 0;
    
    public List<Point> points;
    
    public VpTreeNode1() {
        
    }

    public List<Point> findNearbyPoints(Point point,Point parentPoint, double dist2Parent, double maxDistance) {
        if (left == null) {
            List<Point> result = new ArrayList<Point>();
            if (dist2Parent==-1){
                dist2Parent = point.distance(parentPoint);
            }
            for (Point p : points) {
                double pDist = map.get(p);
                if (maxDistance>=Math.abs(pDist-dist2Parent))
                {
                if (point.distance(p) <= maxDistance) {
                    result.add(p);
                }}
                //else System.out.println("filtrei");
            }
            return result;
        }

        
//        if (dist2Parent>maxDistance && (leftRadius < (dist2Parent - maxDistance)/2)){
//            return right.findNearbyPoints(point,vantagePoint,-1, maxDistance);
//        }
//        else 
        {
            double distanceToLeftCenter = vantagePoint.distance(point);
            if (distanceToLeftCenter + maxDistance < leftRadius) {
            return left.findNearbyPoints(point,vantagePoint,distanceToLeftCenter, maxDistance);
        } else if (distanceToLeftCenter - maxDistance >= leftRadius) {
            return right.findNearbyPoints(point,vantagePoint,distanceToLeftCenter, maxDistance);
        } else {
            List<Point> result = right.findNearbyPoints(point,vantagePoint,distanceToLeftCenter, maxDistance);
            result.addAll(left.findNearbyPoints(point,vantagePoint,distanceToLeftCenter, maxDistance));
            return result;
        }}
    }

    

    /** List must not be modified after node creation! */
    private void buildTreeNode() {
        
        if (points.size() < MAX_LEAF_SIZE) {
            return;
        }

        Point basePoint = chooseNewVantagePoint(points);
        double distances[] = new double[points.size()];
        double sortedDistances[] = new double[points.size()];

        for (int i = 0; i < points.size(); ++i) {
            Point p = points.get(i);
            distances[i] = basePoint.distance(p);
            p.setDist(distances[i]);
            map.put(p, distances[i]);
            sortedDistances[i] = distances[i];
        }

        Arrays.sort(sortedDistances);
        final double medianDistance = sortedDistances[sortedDistances.length / 2];
        
        left = new VpTreeNode1();
        left.points = new ArrayList<Point>();
        right = new VpTreeNode1();
        right.points = new ArrayList<Point>();
        
        for (int i = distances.length-1;i>=0; i--) {
            if (distances[i] < medianDistance) {
                left.points.add(points.get(i));
            } else {
                right.points.add(points.get(i));
            }
            points.remove(i);
        }

        vantagePoint = basePoint;
        leftRadius = medianDistance;

        
        
        left.buildTreeNode();
        if (left.vantagePoint!=null){
            left.distance2Parent = left.vantagePoint.distance(basePoint);
        }

        right.buildTreeNode();
        if (right.vantagePoint!=null){
            right.distance2Parent = right.vantagePoint.distance(basePoint);
        }

    }
    
    /** Trying to choose a new vantage point with highest distance deviation to other nodes. */
    private Point chooseNewVantagePoint(List<Point> points) {
        ArrayList<Point> candidates = new ArrayList<Point>(VANTAGE_POINT_CANDIDATES);
        ArrayList<Point> testPoints = new ArrayList<Point>(TEST_POINT_COUNT);

        for (int i = 0; i < VANTAGE_POINT_CANDIDATES; ++i) {
            int basePointIndex = (int) (Math.random() * points.size());
            Point candidate = points.get(basePointIndex);
            candidates.add(candidate);
        }

        

        Point bestBasePoint = points.get(0);
        double bestBasePointSigma = 0;

        for (Point basePoint : candidates) {
            double distances[] = new double[points.size()];
            for (int i = 0; i < distances.length; ++i) {
                distances[i] = basePoint.distance(points.get(i));
            }
            double sigma = sigmaSquare(distances);
            if (sigma > bestBasePointSigma) {
                bestBasePointSigma = sigma;
                bestBasePoint = basePoint;
            }
        }

        return bestBasePoint;
    }

    /** Trying to choose a new vantage point with highest distance deviation to other nodes. */
    private Point chooseNewVantagePoint2(List<Point> points) {
        ArrayList<Point> candidates = new ArrayList<Point>(VANTAGE_POINT_CANDIDATES);
        ArrayList<Point> testPoints = new ArrayList<Point>(TEST_POINT_COUNT);

        for (int i = 0; i < VANTAGE_POINT_CANDIDATES; ++i) {
            int basePointIndex = i + (int) (Math.random() * (points.size() - i));
            Point candidate = points.get(basePointIndex);
            points.set(basePointIndex, points.get(i));
            points.set(i, candidate);
            candidates.add(candidate);
        }

        for (int i = VANTAGE_POINT_CANDIDATES; i < VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT; ++i) {
            int testPointIndex = i + (int) (Math.random() * (points.size() - i));
            Point testPoint = points.get(testPointIndex);
            points.set(testPointIndex, points.get(i));
            points.set(i, testPoint);
            testPoints.add(testPoint);
        }

        Point bestBasePoint = points.get(0);
        double bestBasePointSigma = 0;

        for (Point basePoint : candidates) {
            double distances[] = new double[TEST_POINT_COUNT];
            for (int i = 0; i < TEST_POINT_COUNT; ++i) {
                distances[i] = basePoint.distance(testPoints.get(i));
            }
            double sigma = sigmaSquare(distances);
            if (sigma > bestBasePointSigma) {
                bestBasePointSigma = sigma;
                bestBasePoint = basePoint;
            }
        }

        return bestBasePoint;
    }

    private double sigmaSquare(double[] values) {
        double sum = 0;

        for (double value : values) {
            sum += value;
        }

        double avg = sum / values.length;
        double sigmaSq = 0;

        for (double value : values) {
            double dev = value - avg;
            sigmaSq += dev * dev;
        }

        return sigmaSq;
    }
}
}