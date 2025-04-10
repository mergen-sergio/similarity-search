/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package evaluation;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import object.StringPoint;
import spell_checker.trie_based.ATrie;

/**
 *
 * @author ferna
 */
public class QueryCreator {

    public ArrayList<String> createQueryFile(String file, ArrayList<StringPoint> points, int size, int minWord, int maxWord) {
        Writer writer = null;

        ArrayList<String> result = new ArrayList<String>();

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "utf-8"));

            int offset = points.size() / size;
            Random random = new Random();
            int x = 0;
            while (x < size) {
                //StringPoint sp = points.get(x * offset);
                StringPoint sp = points.get(random.nextInt(points.size()));
                String word = sp.word;
                while (word.length() < minWord || word.length() > maxWord || result.contains(word)) {
                    sp = points.get(random.nextInt(points.size()));
                    word = sp.word;
                }

                writer.write(word);
                writer.write(System.lineSeparator());
                result.add((word));
                x++;
            }

        } catch (IOException ex) {
            // report
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {/*ignore*/
            }
        }
        return result;
    }

    public ArrayList<String> createQueryFileWithNonExistentWords(String file, ArrayList<StringPoint> points, int size) {
        Writer writer = null;
        List<String> words_ = points.stream().map(word -> word.word)
                .collect(Collectors.toList());
        ArrayList<String> result = new ArrayList<>();
        ATrie trieSearch = new ATrie(50, 2);
        trieSearch.addWords(words_);
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "utf-8"));

            Random random = new Random();
            int x = 0;

            while (x < size) {
                int attempts = 0;
                StringPoint sp = points.get(random.nextInt(points.size()));
                String word = sp.word;
                while (!trieSearch.search(word, 2).isEmpty()) {
                    if (attempts < 3) {
                        word = modifyString(word, random);
                        attempts++;
                    } else {
                        sp = points.get(random.nextInt(points.size()));
                        word = sp.word;
                        attempts = 0;
                    }
                }

                writer.write(word);
                writer.write(System.lineSeparator());
                result.add((word));
                x++;
            }

        } catch (IOException ex) {
            // report
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {/*ignore*/
            }
        }
        return result;
    }

    public static String modifyString(String str, Random random) {
        char[] chars = str.toCharArray();
        int indexToModify = random.nextInt(chars.length);
        chars[indexToModify] = (char) ('a' + random.nextInt(26));
        return new String(chars);
    }
    
    public static void main(String[] args) {
        try {
            QueryCreator queryCreator = new QueryCreator();
            StringLoader stringLoader = new StringLoader();
            int c = 11;
            String dataFile = "C:\\googlebooks_cleaned_200000.txt";
            String queryFile = "C:\\queries_200k_"+c+".txt";
            ArrayList<StringPoint> points = stringLoader.loadStrings(dataFile, 999999999, 3, 49, false);
            queryCreator.createQueryFile(queryFile, points, 50, c, c);
            //queryCreator.createQueryFileWithNonExistentWords(queryFile, points, 100);
        } catch (Exception ex) {
            System.out.println("ERROR:"+ex.getMessage());
        }
    }

}
