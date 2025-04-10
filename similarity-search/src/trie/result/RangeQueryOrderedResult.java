/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trie.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 *
 * @author pccli
 */
public class RangeQueryOrderedResult extends Result {
    
    
    public RangeQueryOrderedResult(int maxDist){
            this.maxDist = maxDist;
        }
    
    public boolean addWord(String word, int dist){
        if (maxDist<dist) return false;
        
        if (first==null){
            first = new Word(word, dist);
            last = first;
            curSize++;
            return true;
        }
        else {
            Word newWord = new Word(word, dist);
            first.prev = newWord;
            newWord.next = first;
            first = newWord;
        }            
        curSize++;
        return true;
        }
    
    @Override
    public boolean needsOrder() {
        return false;
    }
    
    public List<String> toArray(){
            List<Word> list = toWordArray();
            Collections.sort(list);
            return list.stream().map(word->new String(word.word))
             .collect(Collectors.toList());
            
        }
    
    public String toString(){
        return "RangeQueryResult("+maxDist+")";
    }
}
