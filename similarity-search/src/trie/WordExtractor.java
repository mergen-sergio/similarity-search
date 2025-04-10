package trie;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.Writer;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Sergio
 */
public class WordExtractor {

    public static int TAM = 32;


    public void writeStringFile(String file, ArrayList<String> list) {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "utf-8"));
            for (int x = 0; x < list.size(); x++) {
                writer.write(list.get(x));
                writer.write(System.lineSeparator());
            }

        } catch (IOException ex) {
            // report
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {/*ignore*/
            }
        }
    }

    public int readWordCount(String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        int wordCount = 0;
        try {
            String line = br.readLine();

            int wordSize = 0;
            while (line != null) {

                int len = line.length();

                for (int x = 0; x < len; x++) {
                    wordSize++;
                    if (line.charAt(x) == ' ') {
                        wordCount++;
                        wordSize = 0;
                    }
                }
                line = br.readLine();
            }

        } finally {
            br.close();
        }
        return wordCount;

    }

    public static void removeDuplicates(ArrayList<String> words) throws Exception {

        Collections.sort(words);
        String prevWord = words.get(words.size() - 1);
        for (int x = words.size() - 2; x >= 0; x--) {
            String word = words.get(x);
            if (word.equals(prevWord)) {
                words.remove(x);
            } else {
                prevWord = word;
            }

        }

    }

    public static ArrayList<TwoDPoint> create2DPoints() throws Exception {

        Random random = new Random();
        int LEN = 300;
        ArrayList<TwoDPoint> points = new ArrayList<>();
        for (int x = 0; x < LEN; x++) {
            for (int y = 0; y < LEN; y++) {
                int val = random.nextInt(20);
                if (val == 0) {
                    points.add(new TwoDPoint(x, y));
                }
            }
        }

        return points;

    }

    public static int findLongest(ArrayList<StringPoint> words) throws Exception {

        int max = -1;
        for (int x = 0; x < words.size(); x++) {
            StringPoint word = words.get(x);
            if (word.toString().length() > max) {
                max = word.toString().length();
            }

        }
        return max;
    }

    public static void removeDuplicates2(ArrayList words) throws Exception {

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

    public static void createPermutations1(ArrayList<Character> chars, ArrayList<String> words) {
        for (int x = 0; x < chars.size(); x++) {
            StringBuffer str = new StringBuffer();
            str.append(chars.get(x));
            words.add(str.toString());
            str.deleteCharAt(str.length() - 1);
        }
    }

    public static void createPermutations2(ArrayList<Character> chars, ArrayList<String> words) {
        for (int x1 = 0; x1 < chars.size(); x1++) {
            StringBuffer str = new StringBuffer();
            str.append(chars.get(x1));
            for (int x2 = 0; x2 < chars.size(); x2++) {
                str.append(chars.get(x2));
                words.add(str.toString());
                str.deleteCharAt(str.length() - 1);
            }
            str.deleteCharAt(str.length() - 1);
        }
    }

    public static void createPermutations3(ArrayList<Character> chars, ArrayList<String> words) {
        for (int x1 = 0; x1 < chars.size(); x1++) {
            StringBuffer str = new StringBuffer();
            str.append(chars.get(x1));
            for (int x2 = 0; x2 < chars.size(); x2++) {
                str.append(chars.get(x2));
                for (int x3 = 0; x3 < chars.size(); x3++) {
                    str.append(chars.get(x3));
                    words.add(str.toString());
                    str.deleteCharAt(str.length() - 1);
                }
                str.deleteCharAt(str.length() - 1);
            }
            str.deleteCharAt(str.length() - 1);
        }
    }

    public static ArrayList<String> createPermutations() {
        ArrayList<Character> chars = new ArrayList<Character>();
        chars.add('a');
        chars.add('b');
        chars.add('c');
        chars.add('d');
        chars.add('e');
        ArrayList<String> words = new ArrayList<String>();
        //createPermutations1(chars, words);
        //createPermutations1(chars, words);
        //createPermutations1(chars, words);
        //createPermutations1(chars, words);
        //createPermutations2(chars, words);
        //createPermutations2(chars, words);
        //createPermutations2(chars, words);
        //createPermutations2(chars, words);
        //createPermutations3(chars, words);
        //createPermutations3(chars, words);
        //createPermutations3(chars, words);
        createPermutations3(chars, words);
        return words;
    }

    public void copyWords(String input, String output, int limit) throws Exception {

        Writer writer = null;
        //writer = new BufferedWriter(new OutputStreamWriter(
          //      new FileOutputStream(output), "utf-8"));
        writer = new BufferedWriter(new FileWriter(output, true));

        BufferedReader br = new BufferedReader(new FileReader(input));
        int lineCount = -1;
        int wordCount = 0;
        HashSet<String> dic = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        String prevString = "";
        try {
            String line = br.readLine();
            String o_line = line;
            while (line != null) {
                lineCount++;
                if (lineCount%100000 == 0)
                    System.out.println("\rProcessed " + lineCount + " lines");
                
                if (wordCount > limit) {
                    System.out.println("\rProcessed " + lineCount + " lines");
                    System.out.println("");
                    return;

                }
                int indexOfTab = line.indexOf('\t');
                if (indexOfTab != -1) {
                    line = line.substring(0, indexOfTab);
                }
                int indexOfUnderScore = line.indexOf('_');
                if (indexOfUnderScore != -1) {
                    line = line.substring(0, indexOfUnderScore);
                }
                
                int len = line.length();

                for (int x = 0; x < len; x++) {
                    sb.append(line.charAt(x));
                }
                if (sb.length() > 0) {

                    String aux = sb.toString().toLowerCase().replaceAll("[^a-zA-Z]+", "");
                    if (!aux.equals("") && !dic.contains(aux)) {
                        dic.add(aux);
                        writer.write(aux);
                        writer.write(System.lineSeparator());
                        prevString = aux;
                        wordCount++;
                        //System.out.println(aux);
                    }
                    sb.delete(0, sb.length());
                }
                line = br.readLine();
                o_line = line;
            }

        } 
        catch(Exception e){
            System.out.println("erro");
        }
        finally {
            br.close();
            writer.close();
        }
        System.out.println("\rEnd of file. Processed " + lineCount + " lines");
        System.out.println("");
    }

    public ArrayList<StringPoint> feedStringPointWords(String file, int limit, int minWord, int maxWord) throws Exception {

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
                        if (aux.length()>=minWord && aux.length() <= maxWord)
                            words.add(new StringPoint(aux));
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

    public ArrayList<StringPoint> buildNLetterWords(int n, int size) throws Exception {

        ArrayList<StringPoint> words = new ArrayList<>();
        Random r = new Random();
        int i = 0;
        while (i < size) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append((char) ('a' + r.nextInt(26)));
            }
            words.add(new StringPoint(sb.toString()));
            i++;

        }
        return words;
    }


    public ArrayList<String> feedStringWords1(String file, int limit) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<String> words = new ArrayList();
        int wordCount = -1;
        StringBuilder sb = new StringBuilder();

        try {
            String line = br.readLine();

            while (line != null) {
                wordCount++;
                if (wordCount > limit) {
                    return words;
                }
                int len = line.length();

                for (int x = 0; x < len; x++) {
                    sb.append(line.charAt(x));
                    if (line.charAt(x) == ' ' || x == 45) {
                        wordCount++;
                        //sb.append(System.lineSeparator());
                        String aux = sb.toString().toLowerCase().replaceAll("[^a-zA-Z]+", "");
                        if (!aux.equals("")) {
                            words.add(aux);
                        }
                        sb.delete(0, sb.length());
                    }
                }
                if (sb.length() > 0) {

                    String aux = sb.toString().toLowerCase().replaceAll("[^a-zA-Z]+", "");
                    if (!aux.equals("")) {
                        words.add(aux);
                    }
                    sb.delete(0, sb.length());
                }
                line = br.readLine();
            }

        } finally {
            br.close();
        }

        return words;
    }

    public static ArrayList<String> feedStringWords(String file, int limit) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<String> words = new ArrayList<String>();
        int wordCount = -1;
        StringBuilder sb = new StringBuilder();

        try {
            String line = br.readLine();

            while (line != null) {
                wordCount++;
                if (wordCount > limit) {
                    return words;
                }
                int len = line.length();

                for (int x = 0; x < len; x++) {
                    sb.append(line.charAt(x));
//                    if (line.charAt(x) == ' ' || x==45 ) {
//                        wordCount++;
//                        //sb.append(System.lineSeparator());
//                        String word = sb.toString().toLowerCase();
//                        words.add(word);
//                        sb.delete(0, sb.length());
//                    }
                }
                if (sb.length() > 0) {
                    String word = sb.toString().toLowerCase();
                    words.add(word);
                    sb.delete(0, sb.length());
                }
                line = br.readLine();
            }

        } finally {
            br.close();
        }

        removeDuplicates(words);
        return words;
    }


    public static ArrayList<StringPoint> strings2StringPoints(ArrayList<String> list) throws Exception {

        ArrayList<StringPoint> words = new ArrayList<StringPoint>();
        for (int i = 0; i < list.size(); i++) {
            words.add(new StringPoint(list.get(i)));
        }
        return words;
    }

    public static ArrayList<TwoDPoint> feed2dPoints(String file, int limit) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<TwoDPoint> words = new ArrayList<TwoDPoint>();
        int wordCount = -1;

        try {
            String line = br.readLine();
            line = br.readLine();
            while (line != null) {
                StringTokenizer tok = new StringTokenizer(line, " ");
                wordCount++;
                if (wordCount > limit) {
                    return words;
                }

                words.add(new TwoDPoint(Double.valueOf(tok.nextToken()), Double.valueOf(tok.nextToken())));
                line = br.readLine();
            }

        } finally {
            br.close();
        }

        return words;
    }

    public static ArrayList<ThreeDPoint> feed3dPoints(String file, int limit) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<ThreeDPoint> words = new ArrayList<ThreeDPoint>();
        int wordCount = -1;

        try {
            String line = br.readLine();
            line = br.readLine();
            while (line != null) {
                StringTokenizer tok = new StringTokenizer(line, " ");
                wordCount++;
                if (wordCount > limit) {
                    return words;
                }

                words.add(new ThreeDPoint(Double.valueOf(tok.nextToken()), Double.valueOf(tok.nextToken()), Double.valueOf(tok.nextToken())));
                line = br.readLine();
            }

        } finally {
            br.close();
        }

        return words;
    }

    public ArrayList<StringPoint> feedArtificialWords() throws Exception {

        ArrayList<String> strings = createPermutations();
        ArrayList<StringPoint> words = new ArrayList<StringPoint>();

        for (int x = 0; x < strings.size(); x++) {
            words.add(new StringPoint(strings.get(x)));
        }

        return words;
    }
    
    public static void main(String[] args) {
        WordExtractor we = new WordExtractor();
        String file = "googlebooks-eng-all-1gram-20120701-"+"z";
        try {
            String file_ = "C:\\teste\\google\\"+file+"\\"+file;
            we.copyWords(file_,"C:\\teste\\google_ngrams.txt", 999999999);
        } catch (Exception ex) {
            System.out.println("erro");
            Logger.getLogger(WordExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
