/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package evaluation;

import metric.EditDistance;
import io.gitlab.rxp90.jsymspell.SymSpellImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import spell_checker.LinearSearch;
import spell_checker.SpellChecking;

/**
 *
 * @author ferna
 */
public class RangeQueryEvaluator {

    public void evaluateRangeQueries(LinearSearch linearSearch, List<SpellChecking> spellCheckers, String dataFile, String queryFile, int d, int dictionarySize, int times,  boolean includeOneTypo) throws IOException, Exception {

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

        ArrayList<String> queries = stringLoader.loadStrings(queryFile, includeOneTypo);

        //warmup
        evalQueries(queries, d, dictionarySize, spellCheckers, 10, false);

        //execution
        oneMeasure = evalQueries(queries, d, dictionarySize, spellCheckers, times, true);
        allMeasures.add(oneMeasure);

        System.out.println("\nFinal Result:");
        for (int i = 0; i < allMeasures.size(); i++) {
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

    private List<Object> evalQueries(ArrayList<String> queries, int d, int dictionarySize, List<SpellChecking> checkers, int times, boolean verbose) {
        if (verbose){
        System.out.println("\nTesting range queries");
        System.out.println("d = " + d);
        }
        List<Object> oneMeasure = new ArrayList();
        oneMeasure.add(dictionarySize);
        SymSpellImpl.CANDIDATES = 0;
        SymSpellImpl.ANALYZED_CANDIDATES = 0;
        for (SpellChecking spellChecker : checkers) {
            if (verbose)
                System.out.println("\ntesting " + spellChecker.getName());
            long start = System.currentTimeMillis();
            for (int x = 0; x < times; x++) {
                int count = 0;
                double sum = 0;
                for (int i = 0; i < queries.size(); i++) {
                    List<String> list = spellChecker.search(queries.get(i), d);
                    count += list.size();
                    sum += sumDistances(list, queries.get(i));
                }
                if (x == 0 && verbose) {
                    System.out.println("avg results " + count / queries.size());
                    System.out.println("avg distance " + sum / count);

                }

            }
            long end = System.currentTimeMillis();
            long time = end - start;
            if (verbose)
                System.out.println("time (ms):" + time);
            oneMeasure.add(time);
        }

        //System.out.println(oneMeasure);
        return oneMeasure;
    }

    private double sumDistances(List<String> candidates, String query) {
        double sum = 0;
        for (String candidate : candidates) {
            sum += EditDistance.distance(candidate, query);
        }
        return sum;
    }

}
