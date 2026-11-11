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
public class LinearAnagramSearch implements SpellChecking {

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
            int anagDist = distanceAnagram(query, word);
            if (anagDist > maxD) continue;
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
            int anagDist = distanceAnagram(query, word);
            if (anagDist > result.maxDist) continue;
            int curDist = EditDistance.distance(query, word);
            //System.out.println(curDist);
            result.addWord(word, curDist);
            
        }
        
    }
    
    public int distanceAnagram(String s1, String s2) {
            int s2_dif = 0;
            int i = 0, j = 0;
            if (s2.length() > s1.length()) {
                String aux = s1;
                s1 = s2;
                s2 = aux;
            }
            while (i < s1.length() && j < s2.length()) {
                char c1 = s1.charAt(i);
                char c2 = s2.charAt(j);
                if (c1 < c2) {
                    i++;
                } else if (c2 < c1) {
                    s2_dif++;
                    j++;
                } else {
                    i++;
                    j++;
                }
            }
            while (j < s2.length()) {
                j++;
                s2_dif++;
            }
            return s1.length() - s2.length() + s2_dif;
        }

    @Override
    public String getName() {
        return "Linear Anagram Search";
    }

    
    

}
