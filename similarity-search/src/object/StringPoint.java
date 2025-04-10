package object;

import metric.EditDistance;

/**
 * @author Anatoly Borisov
 */
public class StringPoint implements Point<StringPoint> {
    public final String word;
    public double dist = 0;
    int id;

    public StringPoint(String w) {
        this.word = w.toLowerCase();
    }

    @Override
    public double distance(StringPoint p) {
        return EditDistance.distance(word, p.word);
    }

    @Override
    public double optimizedDistance(StringPoint p) {
        return distance(p);
    }
    
    @Override
    public String toString() {
        return word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringPoint that = (StringPoint) o;
        
        return word.equals(that.word);
    }

    @Override
    public int hashCode() {
        return word.hashCode();
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
    public int compareTo(StringPoint o) {
        if (this.dist<o.dist) return -1;
        if (this.dist>o.dist) return 1;
        return 0;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}
