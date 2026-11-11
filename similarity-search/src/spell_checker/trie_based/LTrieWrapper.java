/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spell_checker.trie_based;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import spell_checker.SpellChecking;

public class LTrieWrapper implements SpellChecking {

    FBTrieOld.DictionaryTrie t = new FBTrieOld.Trie();

    
    public LTrieWrapper() {
    }

    @Override
    public String getName() {

        return "LTrie 2";
    }

    public void addWords(List<String> words) {
        try {
//Directory directory = new ByteBuffersDirectory();

            for (String word : words) {
                t.insert(word);
            }

        } catch (Exception ex) {
            System.out.println("erro");
        }
    }

    @Override
    public List<String> search(String query, int maxD) {
        List<FBTrieOld.SearchResult> rawResults = t.fuzzy(query, maxD);
        ArrayList<String> list = new ArrayList();

        // Remove duplicatas e ordena (equivalente a sorted(set(...)))
        Set<FBTrieOld.SearchResult> uniqueResults = new TreeSet<>(rawResults);
        for (FBTrieOld.SearchResult uniqueResult : uniqueResults) {
            list.add(uniqueResult.word);
        }
        return list;

    }

    @Override
    public List<String> topKSearch(String query, int topK) {
        return new ArrayList();
    }

}
