/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trie.evaluation;

import distance.Levenshtein;
import io.gitlab.rxp90.jsymspell.SymSpellImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import trie.LinearSearch;
import trie.SpellChecking;
import trie.wrappers.SymSpellWrapper;

/**
 *
 * @author ferna
 */
public class RangeQueryEvaluator {

    public void evaluateRangeQueries(LinearSearch linearSearch, List<SpellChecking> spellCheckers, String dataFile, String queryFile, int d, int dictionarySize, boolean includeOneTypo) throws IOException, Exception {

        StringLoader stringLoader = new StringLoader();
        DictionaryCreator dictionaryCreator = new DictionaryCreator();

        dictionaryCreator.setFile(dataFile);

        List<List<Object>> allMeasures = new ArrayList();

        List<Object> oneMeasure = new ArrayList();
        oneMeasure.add("size");
        for (SpellChecking spellChecker : spellCheckers) {
            oneMeasure.add(spellChecker.getName());
        }
        allMeasures.add(oneMeasure);
        System.out.println(oneMeasure);

        //d = 2;
        //for (startSuffix = suffix * 1; startSuffix <= initSuffix * 10; startSuffix += initSuffix) {
        ArrayList<String> queries = stringLoader.loadStrings(queryFile, includeOneTypo);
        
        //warmup
        evalQueries(queries, d, dictionarySize, spellCheckers, 10);

        oneMeasure = evalQueries(queries, d, dictionarySize, spellCheckers, 100);
        allMeasures.add(oneMeasure);

        for (int i = allMeasures.size() - 1; i >= 0; i--) {
            List<Object> oneMeasure_ = allMeasures.get(i);
            boolean first = true;
            System.out.println("");
            for (Object object : oneMeasure_) {
                if (!first) {
                    System.out.print(", ");
                }
                System.out.print(object);
                first = false;
            }
        }
    }

    private List<Object> evalQueries(ArrayList<String> queries, int d, int dictionarySize, List<SpellChecking> checkers, int times) {
        System.out.println("\nrun-time test");
        System.out.println("d = " + d);
        List<Object> oneMeasure = new ArrayList();
        oneMeasure.add(dictionarySize);
        SymSpellImpl.CANDIDATES = 0;
        SymSpellImpl.ANALYZED_CANDIDATES = 0;
        for (SpellChecking spellChecker : checkers) {
            System.out.println("testing " + spellChecker.getName());
            long start = System.currentTimeMillis();
            for (int x = 0; x < times; x++) {
                int count = 0;
                double sum = 0;
                for (int i = 0; i < queries.size(); i++) {
                    List<String> list = spellChecker.search(queries.get(i), d);
                    count += list.size();
                    sum+=sumDistances(list, queries.get(i));
                }
                if (x == 0) {
                    System.out.println("avg results " + count / queries.size());
                    System.out.println("avg distance " + sum/count);
                    
                }

            }
            long end = System.currentTimeMillis();
            long time = end - start;
            System.out.println(time);
            oneMeasure.add(time);
            if (spellChecker instanceof SymSpellWrapper) {
                System.out.println("candidates: " + SymSpellImpl.ANALYZED_CANDIDATES);
                System.out.println("candidates: " + SymSpellImpl.CANDIDATES);
            }
        }

        //for (d = 1; d < 3; d++) 
        System.out.println(oneMeasure);
        return oneMeasure;
    }

    private double sumDistances(List<String> candidates, String query){
        double sum = 0;
        for (String candidate : candidates) {
            sum+=Levenshtein.distance(candidate, query);
        }
        return sum;
    }
    
}
