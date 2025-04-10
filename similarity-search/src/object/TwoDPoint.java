package object;

/**
 * @author Anatoly Borisov
 */
public class TwoDPoint implements Point<TwoDPoint> {
    public final double x;
    public final double y;
    public double dist = 0;
    
    TwoDPoint point;

    public TwoDPoint(double x, double y) {
        this.x = x;
        this.y = y;
        
    }

    @Override
    public double distance(TwoDPoint p) {
        double dx = x - p.x;
        double dy = y - p.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public double optimizedDistance(TwoDPoint p) {
        return distance(p);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TwoDPoint that = (TwoDPoint) o;
        
        return (this.x == that.x && this.y == that.y);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    
        @Override
    public void setDist(Double d) {
        this.dist = d;
    }

    @Override
    public Double getDist() {
        return dist;
    }
    
    @Override
    public int compareTo(TwoDPoint o) {
        if (this.dist<o.dist) return -1;
        if (this.dist>o.dist) return 1;
        return 0;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void setId(int id) {
        
    }

}
