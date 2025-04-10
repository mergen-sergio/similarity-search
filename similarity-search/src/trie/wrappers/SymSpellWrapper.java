/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trie.wrappers;

import trie.SpellChecking;
import io.gitlab.rxp90.jsymspell.SymSpell;
import io.gitlab.rxp90.jsymspell.SymSpellBuilder;
import io.gitlab.rxp90.jsymspell.Verbosity;
import io.gitlab.rxp90.jsymspell.api.Bigram;
import io.gitlab.rxp90.jsymspell.api.SuggestItem;
import io.gitlab.rxp90.jsymspell.exceptions.NotInitializedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Sergio
 */
public class SymSpellWrapper implements SpellChecking {

    SymSpell symSpell = null;

    int maxEditDistance = 2;
    boolean includeUnknowns = false;
    int prefixLenght = 7;

    public SymSpellWrapper(int maxEditDistance, int prefixLenght){
        this.maxEditDistance = maxEditDistance;
        this.prefixLenght = prefixLenght;
    }
    
    @Override
    public String getName() {
        return "Sym-Spell";
    }

    @Override
    public void addWords(List<String> words) {

        Map<Bigram, Long> bigrams = new HashMap();

        //bigrams = Files.lines(Paths.get("src/bigrams.txt"))
        //                       .map(line -> line.split(" "))
        //                     .collect(Collectors.toMap(tokens -> new Bigram(tokens[0], tokens[1]), tokens -> Long.parseLong(tokens[2])));
        Map<String, Long> unigrams = new HashMap();;
        //unigrams = Files.lines(Paths.get("C:\\teste\\mwords\\354984si.ngl"))
        //                        .map(line -> line.split(","))
        //                      .collect(Collectors.toMap(tokens -> tokens[0], tokens -> Long.parseLong(tokens[1])));

        for (int x = 0; x < words.size(); x++) {
            unigrams.put(words.get(x), 1L);
        }

        symSpell = new SymSpellBuilder().setUnigramLexicon(unigrams)
                .setBigramLexicon(bigrams)
                .setMaxDictionaryEditDistance(maxEditDistance)
                .setPrefixLength(prefixLenght)
                .setWordsList(words)
                .createSymSpell();
    }

    @Override
    public List<String> search(String query, int maxD) {
        List<String> result = new ArrayList();
        try {
            List<SuggestItem> items = symSpell.lookup(query, Verbosity.ALL, maxD, false);
            return items.stream().map(item -> new String(item.getSuggestion()))
                    .collect(Collectors.toList());
        } catch (NotInitializedException ex) {
            System.out.println("erro");
        }
        return result;

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
