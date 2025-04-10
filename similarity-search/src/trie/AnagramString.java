/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trie;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author pccli
 */
public class AnagramString {
    
    public ArrayList<String> strings = new ArrayList<String>();
    public String anagram;    
    
    public static String orderString(String input){
    char[] charArray = input.toCharArray();
    Arrays.sort(charArray);
    return new String(charArray);
    }
    
    
}
