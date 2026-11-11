/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spell_checker.ngram_based;

import spell_checker.trie_based.*;
import io.gitlab.rxp90.jsymspell.api.DamerauLevenshteinOSA;
import query.result.RangeQueryResult;
import query.result.Result;
import query.result.TopKResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import metric.Levensthein;
import metric.UKonnen;
import spell_checker.SpellChecking;

/**
 *
 * @author Sergio
 */
import java.util.*;

public class NGram implements SpellChecking {

    private final Map<String, Set<String>> index = new HashMap<>();
    private final Set<String> dictionary = new HashSet<>();
    private int N = 3; // tamanho do n-grama

    public NGram(int ngram){
        N = ngram;
    }
    
    public String getName(){
    return "Custom Ngram";
    }
    
    @Override
    public void addWords(List<String> words) {
        for (String word : words) {
            dictionary.add(word);
            for (String ngram : buildNgrams(word)) {
                index.computeIfAbsent(ngram, k -> new HashSet<>()).add(word);
            }
        }
    }

    @Override
    public List<String> search(String query, int maxD) {
        Map<String, Integer> candidates = collectCandidatesWithScores(query);
        List<String> results = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : candidates.entrySet()) {
            String word = entry.getKey();
            int dist = editDistance(query, word);
            if (dist <= maxD) {
                results.add(word);
            }
        }
        return results;
    }

    
    public List<String> topKSearch1(String query, int topK) {
        Map<String, Integer> candidates = collectCandidatesWithScores(query);

        // ordenar candidatos por score de n-grams (maior primeiro)
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(candidates.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        
        
        List<String> results = new ArrayList<>();
        int limit = Math.min(topK * 1, sorted.size()); // examinar só os melhores
        for (int i = 0; i < limit; i++) {
            String word = sorted.get(i).getKey();
            results.add(word);
        }
            
        return results;
    }
    
    @Override
    public List<String> topKSearch(String query, int topK) {
        Map<String, Integer> candidates = collectCandidatesWithScores(query);

        // ordenar candidatos por score de n-grams (maior primeiro)
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(candidates.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        // aplicar edit distance apenas nos top candidatos
        PriorityQueue<Map.Entry<String, Integer>> pq =
            new PriorityQueue<>(Map.Entry.comparingByValue());

        int limit = Math.min(topK * 4, sorted.size()); // examinar só os melhores
        for (int i = 0; i < limit; i++) {
            String word = sorted.get(i).getKey();
            int dist = editDistance(query, word);
            pq.offer(new AbstractMap.SimpleEntry<>(word, dist));
        }

        List<String> results = new ArrayList<>();
        for (int i = 0; i < topK && !pq.isEmpty(); i++) {
            results.add(pq.poll().getKey());
        }
        return results;
    }

    // coleta candidatos e conta n-grams em comum
    private Map<String, Integer> collectCandidatesWithScores(String query) {
        Map<String, Integer> scores = new HashMap<>();
        Set<String> queryNgrams = buildNgrams(query);

        for (String ngram : queryNgrams) {
            Set<String> words = index.get(ngram);
            if (words != null) {
                for (String word : words) {
                    scores.put(word, scores.getOrDefault(word, 0) + 1);
                }
            }
        }
        return scores;
    }

    private Set<String> buildNgrams(String s) {
        Set<String> ngrams = new HashSet<>();
        String padded = "_" + s + "_";
        for (int i = 0; i <= padded.length() - N; i++) {
            ngrams.add(padded.substring(i, i + N));
        }
        return ngrams;
    }

    // algoritmo clássico de Levenshtein
    private int editDistance(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[m][n];
    }
}