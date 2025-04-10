/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trie;

import java.util.List;

/**
 *
 * @author Sergio
 */
public interface SpellChecking {
    
    public void addWords(List<String> words);
    public List<String> search(String query, int maxD);
    public List<String> topKSearch(String query, int topK);
    public String getName();
}
