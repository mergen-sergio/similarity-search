package distance;

/**
 * @author Anatoly Borisov
 */
public class StringPoint implements Point<StringPoint> {
    public final String word;
    public double dist = 0;

    public StringPoint(String w) {
        this.word = w.toLowerCase();
    }

    @Override
    public double distance(StringPoint p) {
        return Levenshtein.distance(word, p.word);
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
        
        return word.equals(o);
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
}
