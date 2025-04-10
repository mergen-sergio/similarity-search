package trie;

/**
 * @author Anatoly Borisov
 */
public class ThreeDPoint implements Point<ThreeDPoint> {
    public final double x;
    public final double y;
    public final double z;
    public double dist = 0;

    public ThreeDPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        
    }

    @Override
    public double distance(ThreeDPoint p) {
        Tester.search_count++;
        double dx = x - p.x;
        double dy = y - p.y;
        double dz = z - p.z;
        double root = Math.sqrt(dx * dx + dy * dy + dz * dz);
        return root;
    }
    
    @Override
    public double optimizedDistance(ThreeDPoint p) {
        return distance(p);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreeDPoint that = (ThreeDPoint) o;
        
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
    public int compareTo(ThreeDPoint o) {
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
