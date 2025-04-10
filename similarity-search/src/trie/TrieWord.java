/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

/**
 *
 * @author Sergio
 */
public class TrieWord implements Comparable<TrieWord>{
    public byte[] permut;
    public ArrayList<String> words = new ArrayList<String>();

    
    public static char getChar(Byte b){
        for (Map.Entry<Character, Byte> entry: table.entrySet())
        {
            if (b.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return '?';
    }
    
        public static String getString(byte[] b){
            String word = "";
            for (byte byte1 : b) {
                word+= (char) (byte1 & 0xFF);
            }
            return word;
    }
    
    
    public static Hashtable<Character, Byte> table = new Hashtable<Character, Byte>(){{
//    put('e',new Byte("26"));
//    put('t',new Byte("25"));
//    put('a',new Byte("24"));
//    put('o',new Byte("23"));
//    put('i',new Byte("22"));
//    put('n',new Byte("21"));
//    put('s',new Byte("20"));
//    put('h',new Byte("19"));
//    put('r',new Byte("18"));
//    put('d',new Byte("17"));
//    put('l',new Byte("16"));
//    put('c',new Byte("15"));
//    put('u',new Byte("14"));
//    put('m',new Byte("13"));
//    put('w',new Byte("12"));
//    put('f',new Byte("11"));
//    put('g',new Byte("10"));
//    put('y',new Byte("9"));
//    put('p',new Byte("8"));
//    put('b',new Byte("7"));
//    put('v',new Byte("6"));
//    put('k',new Byte("5"));
//    put('j',new Byte("4"));
//    put('x',new Byte("3"));
//    put('q',new Byte("2"));
//    put('z',new Byte("1"));

//    put('e',new Byte("1"));
//    put('t',new Byte("2"));
//    put('a',new Byte("3"));
//    put('o',new Byte("4"));
//    put('i',new Byte("5"));
//    put('n',new Byte("6"));
//    put('s',new Byte("7"));
//    put('h',new Byte("8"));
//    put('r',new Byte("9"));
//    put('d',new Byte("10"));
//    put('l',new Byte("11"));
//    put('c',new Byte("12"));
//    put('u',new Byte("13"));
//    put('m',new Byte("14"));
//    put('w',new Byte("15"));
//    put('f',new Byte("16"));
//    put('g',new Byte("17"));
//    put('y',new Byte("18"));
//    put('p',new Byte("19"));
//    put('b',new Byte("20"));
//    put('v',new Byte("21"));
//    put('k',new Byte("22"));
//    put('j',new Byte("23"));
//    put('x',new Byte("24"));
//    put('q',new Byte("25"));
//    put('z',new Byte("26"));
//    put('á',new Byte("27"));
//    put('à',new Byte("28"));
//    put('ã',new Byte("29"));
//    put('é',new Byte("30"));
//    put('ê',new Byte("31"));
//    put('í',new Byte("32"));
//    put('ó',new Byte("33"));
//    put('õ',new Byte("34"));
//    put('ô',new Byte("35"));
//    put('ú',new Byte("36"));
//    put('ç',new Byte("37"));
//    put('â',new Byte("38"));
    
    put('a',new Byte("1"));
    put('b',new Byte("2"));
    put('c',new Byte("3"));
    put('d',new Byte("4"));
    put('e',new Byte("5"));
    put('f',new Byte("6"));
    put('g',new Byte("7"));
    put('h',new Byte("8"));
    put('i',new Byte("9"));
    put('j',new Byte("10"));
    put('k',new Byte("11"));
    put('l',new Byte("12"));
    put('m',new Byte("13"));
    put('n',new Byte("14"));
    put('o',new Byte("15"));
    put('p',new Byte("16"));
    put('q',new Byte("17"));
    put('r',new Byte("18"));
    put('s',new Byte("19"));
    put('t',new Byte("20"));
    put('u',new Byte("21"));
    put('v',new Byte("22"));
    put('w',new Byte("23"));
    put('x',new Byte("24"));
    put('y',new Byte("25"));
    put('z',new Byte("26"));
    put('á',new Byte("27"));
    put('à',new Byte("28"));
    put('ã',new Byte("29"));
    put('é',new Byte("30"));
    put('ê',new Byte("31"));
    put('í',new Byte("32"));
    put('ó',new Byte("33"));
    put('õ',new Byte("34"));
    put('ô',new Byte("35"));
    put('ú',new Byte("36"));
    put('ç',new Byte("37"));
    put('â',new Byte("38"));
    
    }};
    
    public TrieWord(String w, int prefixSize){
        words.add(w);
        if (w.length()>prefixSize)
            w = w.substring(0,prefixSize);
        permut = (orderString(w));
    }
    
    @Override
    public String toString(){
        return getString(permut);
    }
    
    public byte[] orderString(String input){
    /*
    char[] charArray = input.toCharArray();
    Arrays.sort(charArray);
    return charArray;
    */
    
    
    byte[] byteArray = new byte[input.length()];
        for (int i = 0; i < input.length(); i++) {
            if (table.get(input.charAt(i))==null)
                System.out.println("...");
                    
            //byteArray[i] = table.get(input.charAt(i));
            byteArray[i] = (byte)input.charAt(i);
        }
        Arrays.sort(byteArray);
    return byteArray;
        
    /*
    Character charArray[] = new Character[input.length()];
        for (int i = 0; i < input.length(); i++) {
            charArray[i] = input.charAt(i);
        }
    Arrays.sort(charArray, new distComparator());
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); i++) {
            sb.append(charArray[i]);
        }
    return sb.toString();

    */
    }
    
    public Character[] toCharArray(String value) {
        // Cannot use Arrays.copyOf because of class initialization order issues
        Character result[] = new Character[value.length()];
        System.arraycopy(value.toCharArray(), 0, result, 0, value.length());
        return result;
    }
    
    public String toString(Character value[]) {
         return Arrays.toString(Arrays.copyOf(value, value.length));
    }
    
    
    
    @Override
    public int compareTo(TrieWord o) {
        //if (this.permut.length()>o.permut.length())
          //  return 1;
        //else if (this.permut.length()<o.permut.length())
          //  return -1;
        //else 
        //return this.permut.compareTo(o.permut);
        return compareToString(this.permut,o.permut);
    }
    
        public int compareToString(byte[] v1, byte[] v2) {
        int len1 = v1.length;
        int len2 = v2.length;
        int lim = Math.min(len1, len2);

        int k = 0;
        while (k < lim) {
            byte c1 = v1[k];
            byte c2 = v2[k];
            if (c1 != c2) {
                return c1-c2;
            }
            k++;
        }
        return len1 - len2;
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
        
        TrieWord tw = new TrieWord("teste",10);
        System.out.println(tw.permut);
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
