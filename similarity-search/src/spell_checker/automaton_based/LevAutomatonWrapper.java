/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spell_checker.automaton_based;

import spell_checker.SpellChecking;
import com.BoxOfC.LevenshteinAutomaton.LevenshteinAutomaton;
import com.BoxOfC.MDAG.MDAG;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Sergio
 * Fast String Correction with Levenshtein-Automata (2002) by Klaus Schulz , Stoyan Mihov (http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.16.652) (Presented algorithm used to create automaton-traversal and table-based dictionary search implementations)

Damn Cool Algorithms: Levenshtein Automata by Nick Johnson (http://blog.notdot.net/2010/07/Damn-Cool-Algorithms-Levenshtein-Automata) (Layman's explanation of Levenshtein automata and related search algorithms)

Lucene's FuzzyQuery is 100 times faster in 4.0 by Mike Mccandless (http://blog.mikemccandless.com/2011/03/lucenes-fuzzyquery-is-100-times-faster.html) (Inspiration for the creation of the library)
 */
public class LevAutomatonWrapper implements SpellChecking {

    MDAG myMDAG = null;
    int maxEditDistance = 2;

    public LevAutomatonWrapper(int maxEditDistance){
        this.maxEditDistance = maxEditDistance;
    }
    
    @Override
    public String getName() {
        return "Lev-Aut";
    }

    @Override
    public void addWords(List<String> words) {
        myMDAG = new MDAG(words);
    }

    @Override
    public List<String> search(String query, int maxD) {
        LinkedList<String> ldNeighborsLinkedList = LevenshteinAutomaton.tableFuzzySearch(maxD, query, myMDAG);
        return ldNeighborsLinkedList;

    }

    @Override
    public List<String> topKSearch(String query, int topK) {

        List<String> resultsX = new ArrayList<>();
        for (int i = 0; i < maxEditDistance+1; i++) {
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
