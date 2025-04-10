/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package query.result;


/**
 *
 * @author pccli
 */
public class RangeQueryResult extends Result {
    
    
    public RangeQueryResult(int maxDist){
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
    
    public String toString(){
        return "RangeQueryResult("+maxDist+")";
    }
}
