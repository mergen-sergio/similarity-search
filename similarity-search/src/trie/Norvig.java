/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ferna
 * http://norvig.com/spell-correct.html
 */
public class Norvig implements SpellChecking {

    private final HashSet<String> dic = new HashSet();

    private final ArrayList<String> edits(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < word.length(); ++i) {
            result.add(word.substring(0, i) + word.substring(i + 1));
        }
        for (int i = 0; i < word.length() - 1; ++i) {
            result.add(word.substring(0, i) + word.substring(i + 1, i + 2) + word.substring(i, i + 1) + word.substring(i + 2));
        }
        for (int i = 0; i < word.length(); ++i) {
            for (char c = 'a'; c <= 'z'; ++c) {
                result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i + 1));
            }
        }
        for (int i = 0; i <= word.length(); ++i) {
            for (char c = 'a'; c <= 'z'; ++c) {
                result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));
            }
        }
        return result;
    }

    @Override
    public void addWords(List<String> words) {
        for (String word : words) {
            dic.add(word);
        }
    }

    private void addOneEditVariations(HashSet<String> variations) {
        String array[] = variations.toArray(new String[0]);
        for (String s : array) {
            List<String> edits = edits(s);
            for (String edit : edits) {
                if (!variations.contains(edit)) {
                    variations.add(edit);
                }
            }
        }
    }

    private List<String> findResults(HashSet<String> variations) {
        List<String> results = new ArrayList();
        for (String variation : variations) {
            if (dic.contains(variation)) {
                results.add(variation);
            }
        }
        return results;
    }

    @Override
    public List<String> search(String query, int maxD) {
        HashSet<String> variations = new HashSet<>();
        variations.add(query);
        for (int i = 1; i <= maxD; i++) {
            addOneEditVariations(variations);
        }

        return findResults(variations);
    }

    @Override
    public List<String> topKSearch(String query, int topK) {

        List<String> results = new ArrayList();
        HashSet<String> variations = new HashSet<>();
        variations.add(query);
        for (int i = 1; i <= 3; i++) {
            addOneEditVariations(variations);
            results = findResults(variations);
            if (results.size() >= topK) {
                return results.subList(0, topK);
            }
        }
        return results;
    }

    @Override
    public String getName() {
        return "Norvig";
    }
}
