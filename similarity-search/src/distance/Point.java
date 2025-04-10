package distance;


/**
 * An base interface for point in VP Tree.
 * @author Anatoly Borisov
 */
public interface Point<T extends Point<T>> extends Comparable<T>{
    /**
     * Calculates distance to another point.
     * The metric must hold the following condition for points A, B, C:
     * A.distance(C) <= A.distance(B) + B.distance(C)
     */
    public double distance(T p);
    
    public void setDist(Double d);
    
    public Double getDist();
}
