/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bktree.trie;

/**
 *
 * @author Sergio
 */
public class JacSimilarity {
    
    public static int getDistance(String s1, String s2){
    int s2_dif = 0;
        int i=0, j=0;
        if (s2.length()>s1.length())
        {
            String aux = s1;
            s1 = s2;
            s2 = aux;
        }
        while (i < s1.length() && j<s2.length()) {
            char c1 = s1.charAt(i);
                char c2 = s2.charAt(j);
                if (c1<c2){
                    i++;
                }
                else if (c2<c1){
                    s2_dif++;
                    j++;
                }
                else{
                    i++;
                    j++;
                }
        }
        while (j < s2.length()){
            j++;
            s2_dif++;
        }
    return s1.length()-s2.length()+s2_dif;
    }
    
    public static int getDistance2(String s1, String s2){
        int s1_dif = 0;
        int s2_dif = 0;
        int i=0, j=0;
        while (i < s1.length() && j<s2.length()) {
            char c1 = s1.charAt(i);
                char c2 = s2.charAt(j);
                if (c1<c2){
                    s1_dif++;
                    i++;
                }
                else if (c2<c1){
                    s2_dif++;
                    j++;
                }
                else{
                    i++;
                    j++;
                }
        }
        while (i < s1.length()){
            i++;
            s1_dif++;
        }
        while (j < s2.length()){
            j++;
            s2_dif++;
        }
            
    return Math.max(s1_dif, s2_dif);
    }
    
    public static void main(String[] args) {
        System.out.println(JacSimilarity.getDistance("bcdg", "abcfj"));
        
    }
}
