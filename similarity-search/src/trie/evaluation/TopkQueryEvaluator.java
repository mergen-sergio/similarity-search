/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trie.evaluation;

import distance.Levenshtein;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import trie.LinearSearch;
import trie.SpellChecking;

/**
 *
 * @author ferna
 */
public class TopkQueryEvaluator {
    public void evaluateTopkQueries(LinearSearch linearSearch, List<SpellChecking> spellCheckers, String dataFile, String queryFile, int k, int dictionarySize, int times, boolean verbose, boolean includeOneTypo) throws IOException, Exception {

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

        //for (startSuffix = suffix * 1; startSuffix <= initSuffix * 10; startSuffix += initSuffix) {


            ArrayList<String> queries = stringLoader.loadStrings(queryFile, includeOneTypo);
            
            //for (int i = 0; i < 10; i++) 
            {
                oneMeasure = evalQueries(queries, k, dictionarySize, spellCheckers, times, verbose);
                allMeasures.add(oneMeasure);
            }


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

    private List<Object> evalQueries(ArrayList<String> queries, int k, int dictionarySize, List<SpellChecking> checkers, int times, boolean verbose) {
        System.out.println("\nrun-time test");
        System.out.println("top k = " + k);
        List<Object> oneMeasure = new ArrayList();
        oneMeasure.add(dictionarySize);
        for (SpellChecking spellChecker : checkers) {
            System.out.println("testing " + spellChecker.getName());
            long start = -1;
            for (int x = 0; x < times; x++) {
                if (x==10)
                    start = System.currentTimeMillis();
                double count = 0;
                double sum = 0;
                for (int i = 0; i < queries.size(); i++) {
                    List<String> list = spellChecker.topKSearch(queries.get(i), k);
                    count += list.size();
                    sum+=sumDistances(list, queries.get(i));
                }
                if (x == 0 && verbose) {
                    System.out.println("avg results " + count / queries.size());
                    System.out.println("avg distance " + sum/count);
                }

            }
            long end = System.currentTimeMillis();
            long time = end - start;
            oneMeasure.add(time);
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
