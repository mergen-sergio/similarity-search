/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package evaluation;

import metric.EditDistance;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import spell_checker.LinearSearch;
import spell_checker.SpellChecking;
import spell_checker.trie_based.ATrie;
import spell_checker.trie_based.FBTrie;

/**
 *
 * @author ferna
 */
public class TopkQueryEvaluator {

    public void evaluateTopkQueries(LinearSearch linearSearch, List<SpellChecking> spellCheckers, String dataFile, String queryFile, int k, int dictionarySize, int times, int typos, int minPrefixSize, int maxPrefixSize) throws IOException, Exception {

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
        
        ArrayList<String> warmupQueries = stringLoader.loadStrings(queryFile, 99999, typos, maxPrefixSize);

        //warmup
        evalQueries(warmupQueries, k, dictionarySize, spellCheckers, 20, false, maxPrefixSize);
        
        for (int prefixSize = minPrefixSize; prefixSize < maxPrefixSize; prefixSize+=1) {
            
        
        ArrayList<String> queries = stringLoader.loadStrings(queryFile, 99999, typos, prefixSize);

        
        //execution
        oneMeasure = evalQueries(queries, k, dictionarySize, spellCheckers, times, true, prefixSize);
        allMeasures.add(oneMeasure);
        }
        System.out.println("\nFinal result:");
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
    
    private List<Object> evalFasterQuery(ArrayList<String> queries, int k, int dictionarySize, List<SpellChecking> checkers, int times, boolean verbose) {
    if (verbose) {
        System.out.println("\nTesting individual top-k queries");
        System.out.println("top k = " + k);
    }

    List<Object> results = new ArrayList<>();
    results.add(dictionarySize);

    for (SpellChecking spellChecker : checkers) {
        if (verbose) {
            System.out.println("\ntesting " + spellChecker.getName());
        }

        String fastestQuery = null;
        long minQueryTime = Long.MAX_VALUE;

        // Invert order: test each query string individually across the specified runs
        for (String query : queries) {
            long queryStart = System.currentTimeMillis();

            // Run this specific query 'times' times
            for (int x = 0; x < times; x++) {
                List<String> list = spellChecker.topKSearch(query, k);
            }

            long queryDuration = System.currentTimeMillis() - queryStart;

            if (verbose) {
                System.out.println("  Query: \"" + query + "\" -> " + queryDuration + " ms total (" + times + " runs)");
            }

            // Track the query with the minimum execution time for this checker
            if (queryDuration < minQueryTime) {
                minQueryTime = queryDuration;
                fastestQuery = query;
            }
        }

        if (verbose) {
            System.out.println("-> Fastest query for " + spellChecker.getName() + ": \"" + fastestQuery + "\" (" + minQueryTime + " ms)");
        }

        // Add the findings for this checker to your results list
        results.add(spellChecker.getName());
        results.add(fastestQuery);
        results.add(minQueryTime);
    }

    return results;
}

    private List<Object> evalQueries(ArrayList<String> queries, int k, int dictionarySize, List<SpellChecking> checkers, int times, boolean verbose, int prefixSize) {
        if (verbose) {
            System.out.println("\nResting top-k queries");
            System.out.println("top k = " + k);
        }

        int warmup = 0;
        List<Object> oneMeasure = new ArrayList();
        oneMeasure.add(dictionarySize);
        oneMeasure.add(prefixSize);
        for (SpellChecking spellChecker : checkers) {
            if (verbose) {
                System.out.println("\ntesting " + spellChecker.getName());
            }
            
            ATrie.NUM_VISITED_NODES = 0;
            ATrie.NUM_EDIT_COMP = 0;
            ATrie.MAX_BUCKETS = 0;
            FBTrie.NUM_VISITED_NODES = 0;

            List<Long> iterationTimes = new ArrayList<>();
            long minTimes = Long.MAX_VALUE;
            long start = -1;
            long groupStart = -1; // Timer for the current group of 5

            for (int x = 0; x < times; x++) {
                // Start timing for this specific batch of 5
                if (x % 5 == 0) {
                    groupStart = System.currentTimeMillis();
                }

                // Align the start timer to the exact beginning of the post-warmup phase (x = 10)
                if (x == warmup) {
                    start = System.currentTimeMillis();
                }

                double count = 0;
                double sum = 0;
                for (int i = 0; i < queries.size(); i++) {
                    List<String> list = spellChecker.topKSearch(queries.get(i), k);
                    count += list.size();
                    sum += sumDistances(list, queries.get(i));
                }

                if (x == 0 && verbose) {
                    System.out.println("avg results " + count / queries.size());
                    System.out.println("avg distance " + sum / count);
                    
                    if (spellChecker.getName().equals("A-Trie"))
                    {
                        System.out.println("NUM_VISITED_NODES: "+ATrie.NUM_VISITED_NODES);
                        System.out.println("NUM_EDIT_COMP: "+ATrie.NUM_EDIT_COMP);
                        System.out.println("MAX_BUCKETS: "+ATrie.MAX_BUCKETS);
                    }
                    else if (spellChecker.getName().equals("FB-Trie"))
                    {
                        System.out.println("NUM_VISITED_NODES: "+FBTrie.NUM_VISITED_NODES);
                    }
                }

                // At the end of every 5th iteration block
                if ((x + 1) % 5 == 0) {
                    long groupDuration = System.currentTimeMillis() - groupStart;

                    // ONLY record after warm-up AND where x >= 14 
                    // (since x=10 to x=14 completes the first post-warmup batch of 5)
                    if (x >= warmup + 4) {
                        iterationTimes.add(groupDuration);
                        if (groupDuration < minTimes) {
                            minTimes = groupDuration;
                        }
                    }
                }
            }

            long end = System.currentTimeMillis();
            long time = end - start; // Perfectly bounds the post-warmup execution time

            if (verbose) {
                System.out.println("time (ms): " + time);
            }

            double meanTime = iterationTimes.stream().mapToLong(l -> l).average().orElse(0.0);
            double varianceTime = iterationTimes.stream().mapToLong(l -> l)
                    .mapToDouble(l -> Math.pow(l - meanTime, 2))
                    .average().orElse(0.0);
            double stdDevTime = Math.sqrt(varianceTime);

            // Now iterationTimes.size() precisely matches the batches covered by 'time'
            oneMeasure.add(time);
            int iterationTimes_ = iterationTimes.size();
            if (iterationTimes_==0)
                iterationTimes_ = 1;
//            oneMeasure.add(time / iterationTimes_);
//            oneMeasure.add(stdDevTime);
//            oneMeasure.add(minTimes);
        }

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
