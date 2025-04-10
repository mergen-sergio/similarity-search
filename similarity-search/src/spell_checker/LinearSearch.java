package spell_checker;

import spell_checker.SpellChecking;
import metric.EditDistance;
import query.result.Result;
import query.result.TopKResult;
import io.gitlab.rxp90.jsymspell.api.DamerauLevenshteinOSA;
import io.gitlab.rxp90.jsymspell.api.StringDistance;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pccli
 */
public class LinearSearch implements SpellChecking {

    public static int search_count = 0;

    ArrayList<String> words = new ArrayList<>();
    
    StringDistance stringDistanceAlgorithm = new DamerauLevenshteinOSA();

    public void addWord(String point) {
        words.add(point);
    }

    @Override
    public void addWords(List<String> words_) {
        for (int x = 0; x < words_.size(); x++) {
            addWord(words_.get(x));
        }
    }

     public List<String> topKSearch(String query, int topK){
        TopKResult top = new TopKResult(topK);
         search(query, top);
         return top.toArray();
    
    }
    
    @Override
    public List<String> search(String query, int maxD) {
        ArrayList<String> result = new ArrayList<String>();

        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            //int dist = stringDistanceAlgorithm.distanceWithEarlyStop(query, word, maxD);
            int curDist = EditDistance.distance(query, word);
            if (curDist <= maxD) {
            //if (dist!=-1){
                result.add(word);
            }
        }

        return result;

    }
    
    public void search(String query, Result result) {
        
        
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            int curDist = EditDistance.distance(query, word);
            //System.out.println(curDist);
            result.addWord(word, curDist);
            
        }
        
    }

    @Override
    public String getName() {
        return "Linear Search";
    }

    
    

}
