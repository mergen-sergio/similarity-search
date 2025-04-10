/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import object.StringPoint;

/**
 *
 * @author ferna
 */
public class StringLoader {

    public static ArrayList<StringPoint> loadStrings(String file, int limit, int minWord, int maxWord, boolean shuffle) throws Exception {

        ArrayList<StringPoint> words = loadStringPoints(file, limit, minWord, maxWord);
        //ArrayList<StringPoint> words = we.buildNLetterWords(10,99999);

        removeDuplicates(words);

        if (shuffle) {
            Collections.shuffle(words);
        }

        System.out.println("words size " + words.size());
        return words;
    }

    public static ArrayList<StringPoint> loadStringPoints(String file, int limit, int minWord, int maxWord) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<StringPoint> words = new ArrayList<StringPoint>();
        HashSet<String> dic = new HashSet<>();
        int lineCount = -1;
        StringBuilder sb = new StringBuilder();
        String prevString = "";
        try {
            String line = br.readLine();

            while (line != null) {
                lineCount++;
                if (words.size() > limit) {
                    return words;
                }
                int indexOfTab = line.indexOf('\t');
                if (indexOfTab != -1) {
                    line = line.substring(0, indexOfTab);
                }
                int len = line.length();

                for (int x = 0; x < len; x++) {
                    sb.append(line.charAt(x));
//                    if (line.charAt(x) == ' ' || x==45 ) {
//                        wordCount++;
//                        //sb.append(System.lineSeparator());
//                        String aux = sb.toString().toLowerCase().replaceAll("[^a-zA-Z]+", "");
//                        if (!aux.equals(""))
//                            words.add(new StringPoint(aux));
//                        sb.delete(0, sb.length());
//                    }
                }
                if (sb.length() > 0) {

                    String aux = sb.toString().toLowerCase().replaceAll("[^a-zA-Z]+", "");
                    if (!aux.equals("") && !dic.contains(aux)) {
                        if (aux.length() >= minWord && aux.length() <= maxWord) {
                            words.add(new StringPoint(aux));
                        }
                        prevString = aux;
                        dic.add(aux);
                    }
                    sb.delete(0, sb.length());
                }
                line = br.readLine();
            }

        } finally {
            br.close();
        }

        System.out.println("processed " + lineCount + " lines");
        return words;
    }

    public ArrayList<String> loadStrings(String file, boolean includeOneTypo) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<String> words = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        try {
            String line = br.readLine();

            while (line != null) {
                int len = line.length();
                for (int x = 0; x < len; x++) {
                    sb.append(line.charAt(x));

                }
                String aux = sb.toString().toLowerCase().replaceAll("[^a-zA-Z]+", "");
                if (includeOneTypo){
                    int mid = aux.length()/2;
                    aux = aux.substring(0,mid) + "q"+ aux.substring(mid, aux.length());
                }
                words.add(aux);
                sb.delete(0, sb.length());
                line = br.readLine();
            }

        } finally {
            br.close();
        }

        return words;
    }

    public static void removeDuplicates(ArrayList words) throws Exception {

        Hashtable<String, Integer> dic = new Hashtable<String, Integer>();

        for (int x = words.size() - 1; x >= 0; x--) {
            String word = words.get(x).toString();
            if (dic.containsKey(word)) {
                words.remove(x);
                //System.out.println("duplicate "+word);
            } else {
                dic.put(word, x);
            }

        }

    }
}
