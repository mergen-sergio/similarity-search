/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trie.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author pccli
 */
public class TopKResult extends Result {
    
    public int maxSize = -1;
    
    public TopKResult(int size){
            this.maxSize = size;
        }
    

        public void clear(){
        super.clear();
        maxDist = Integer.MAX_VALUE-1;
        }

    
    @Override
    public boolean addWord(String word, int dist){
        
        
        if (maxDist<dist) return false;
        if (curSize>=maxSize && maxDist<=dist) return false;
        
        
        if (first==null){
            first = new Word(word, dist);
            last = first;
            
            curSize++;
            if (curSize==maxSize)
                if (dist<maxDist) maxDist = dist;
            
            return true;
        }
        else {
            Word cur = first;
            Word ant = null;
            while (cur!=null){
                //if (cur.word.equals(word)) return false;
                if (cur.dist>=dist) break;
                ant = cur;
                cur = cur.next;
            }
            if (curSize>=maxSize && cur==null)
                return false;
            
            curSize++;
            
            Word newWord = new Word(word, dist);
            if (ant==null){
                newWord.next = first;
                first.prev = newWord;
                first = newWord;
            }
            else {
                newWord.next = ant.next;
                if (ant.next!=null)
                    ant.next.prev = newWord;
                ant.next = newWord;
                newWord.prev = ant;
                if (cur==null){
                    last = newWord;
                    //if (last.dist<maxDist) 
                        //maxDist = last.dist;
                }
            
            }
            
            if (curSize==maxSize){
            if (last.dist<maxDist) 
                    maxDist = last.dist;
            }
            
            else if (curSize>maxSize){
                last.prev.next = null;
                last = last.prev;
                
                if (last.dist<maxDist) 
                    maxDist = last.dist;
                
                curSize--;
            }
            
            
        
        }
        return true;
        }

    @Override
    public boolean needsOrder() {
        return true;
    }
    
    public String toString(){
        return "TopKResult("+maxSize+")";
    }
    
    
    public static void main(String[] args) {
        //ArrayList<StringPoint> points = new ArrayList();
        ArrayList<Integer> index = new ArrayList();
        for (int i = 0; i < 10000; i++) {
            index.add(i);
        }
        
        Collections.shuffle(index);
        
        Result r = new TopKResult(2);
        Random random = new Random();
        for (int i = 0; i < index.size(); i++) {
            //int in = index.get(i);
            int in = random.nextInt(index.size());
            r.addWord(new String(String.valueOf(in)), in);
            TopKResult.print(r);
        }
        
        
        
    }
    
    public static void print(Result r){
        List<String> points = r.toArray();
        StringBuffer buf = new StringBuffer();
        buf.append(r.maxDist);
        buf.append("[");
        for (int i = 0; i < points.size(); i++) {
            buf.append(points.get(i)+",");
        }
        buf.append("]");
        System.out.println(buf.toString());
    }
    
}
