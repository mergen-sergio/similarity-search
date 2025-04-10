/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spell_checker.edit_based;

import spell_checker.SpellChecking;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio
 */
public class FastSSWrapper implements SpellChecking{

    FastSimilarSearch fastSS = new FastSimilarSearch();
    int maxDistance = 2;
    
    @Override
    public String getName() {
        return "FastSS";
    }
    
    public FastSSWrapper(int maxDistance){
    this.maxDistance = maxDistance;
    }
    
    @Override
    public void addWords(List<String> words) {
        try {
            FastSimilarSearch.setLevenshteinWordDistance(maxDistance);
            FastSimilarSearch.type = FastSimilarSearch.FASTBLOCKSS;
            FastSimilarSearch.loadData(words);
        } catch (IOException ex) {
            System.out.println("erro");
        }
    }

    @Override
    public List<String> search(String query, int maxD) {
        FastSimilarSearch.setLevenshteinWordDistance(maxD);
        return fastSS.fastBlockLD(query);
                //fastSS.fastLD2(query);
    }

    @Override
    public List<String> topKSearch(String query, int topK) {
        List<String> resultsX = new ArrayList<>();
        for (int i = 0; i < maxDistance+1; i++) {
            List<String> results = search(query, i);
            results.removeAll(resultsX);
            for (int j = 0; j < results.size(); j++) {
                resultsX.add(results.get(j));
                if (resultsX.size() == topK) {
                    return resultsX;
                }
            }
        }
        return resultsX;
    }
    
}
