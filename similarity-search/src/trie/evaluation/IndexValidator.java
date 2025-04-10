/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trie.evaluation;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import trie.LinearSearch;
import trie.SpellChecking;
import trie.StringPoint;
import trie.Tester;

/**
 *
 * @author ferna
 */
public class IndexValidator {

    public void validateAll(LinearSearch linearSearch, List<SpellChecking> spellCheckers, List<StringPoint> points, int start, int end, int minLen, int maxLen, int d) throws Exception {

        StringPoint sp;

        if (end > points.size()) {
            end = points.size();
        }
        for (int i = start; i < end; i++) {

            sp = points.get(i);
            if (sp.word.length() < minLen || sp.word.length() > maxLen) {
                continue;
            }

            for (int x = 1; x <= d; x++) {
                checkPrecision(linearSearch, spellCheckers, sp.word, x);
            }
        }
    }

    public void validateRandom(LinearSearch linearSearch, List<SpellChecking> spellCheckers, List<StringPoint> points, int numberOfRuns, int d) throws Exception {

        Random random = new Random();
        StringPoint sp;

        for (int i = 0; i < numberOfRuns; i++) {

            sp = points.get(random.nextInt(points.size()));

            for (int x = 1; x <= d; x++) {
                checkPrecision(linearSearch, spellCheckers, sp.word, x);
            }
        }
    }

    public void checkPrecision(LinearSearch linearSearch, List<SpellChecking> spellCheckers, String word, int dist) throws Exception {
        System.out.println("---- word: " + word + " dist: " + dist);

        List<String> correctResults;
        correctResults = linearSearch.search(word, dist);
        System.out.println("-- found " + correctResults.size() + " results in LINEAR SEARCH");
        for (SpellChecking spellChecker : spellCheckers) {
            System.out.println("-- searching with " + spellChecker.getName());
            List<String> indexResults = spellChecker.search(word, dist);
            System.out.println("-- found " + indexResults.size() + " results in " + Tester.search_count);

            if (indexResults.size() != correctResults.size()) {
                System.out.println("Error: Found " + indexResults.size() + " words but expected was " + correctResults.size());
                System.out.println("Correct:");
                System.out.println(Arrays.toString(correctResults.toArray()));
                for (int i = 0; i < correctResults.size(); i++) {
                    System.out.println(correctResults.get(i));
                }
                System.out.println("But found:");
                System.out.println(Arrays.toString(indexResults.toArray()));
                throw new Exception("");
            }
        }

    }
}
