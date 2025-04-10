/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trie.result;


/**
 *
 * @author pccli
 */
public class ClosestResult extends Result{
 
  
    public void clear(){
        super.clear();
        maxDist = Integer.MAX_VALUE;
        }
    
     public boolean addWord(String word, int dist){
        if (maxDist<dist) return false;
        
        if (first==null){
            first = new Word(word, dist);
            last = first;
            maxDist = dist;
            curSize++;
            return true;
        }
        else {
            Word newWord = new Word(word, dist);
            if (first.dist>dist){
                maxDist = dist;
                first = newWord;
                last = newWord;
                curSize = 1;
            }
            else {
                first.prev = newWord;
                newWord.next = first;
                first = newWord;
                curSize++;
            }
                    
            }
        
        return true;
        }
     
     @Override
    public boolean needsOrder() {
        return true;
    }
    
    public String toString(){
        return "ClosestResult";
    }
}
