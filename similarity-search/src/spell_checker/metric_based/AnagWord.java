/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spell_checker.metric_based;

import java.util.Arrays;

/**
 *
 * @author Sergio
 */
public class AnagWord {
    public String anag;
    public String word;

    
    
    
    public AnagWord(String w, int prefixSize){
        word = w;
        if (w.length()>prefixSize)
            word = w.substring(0,prefixSize);
        anag = (orderString(word));
    }
    
    @Override
    public String toString(){
        return word;
    }
    
    public String orderString(String input){
    
        
    
    Character charArray[] = new Character[input.length()];
        for (int i = 0; i < input.length(); i++) {
            charArray[i] = input.charAt(i);
        }
    Arrays.sort(charArray);
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); i++) {
            sb.append(charArray[i]);
        }
    return sb.toString();

    
    }
    
    
    
    public static void main(String[] args) {
        /*
        ArrayList<TrieWord> list = new ArrayList<TrieWord>();
        list.add(new TrieWord("teste"));
        list.add(new TrieWord("bolsa"));
        list.add(new TrieWord("casaco"));
        list.add(new TrieWord("pneu"));
        list.add(new TrieWord("boi"));
        Collections.sort(list);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).permut);
        }
        */
        
        AnagWord tw = new AnagWord("teste",10);
        System.out.println(tw.anag);
    }
    /*
    public class distComparator implements Comparator<Character> {

    @Override
    public int compare(Character x, Character y){
  
        if (table.get(x)<table.get(y))
            return -1;
        
        if (table.get(x)>table.get(y)){
            return 1;
        }

        return 0;
    }
}
*/
}
