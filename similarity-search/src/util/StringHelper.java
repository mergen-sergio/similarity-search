package util;

import object.StringPoint;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.Writer;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Sergio
 */
public class StringHelper {


    public void writeFile(String file, ArrayList<String> list) {
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

    public ArrayList<StringPoint> createStringPoints(String file, int limit, int minWord, int maxWord) throws Exception {

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
    
    public static String replaceRandomCharacters(String input, int numberOfChars, String replacement) {
    if (input == null || input.isEmpty() || numberOfChars <= 0) {
        return input;
    }

    // Cap the number of replacements at the maximum length of the string
    int actualReplacements = Math.min(numberOfChars, input.length());

    // 1. Create a list of all valid index positions in the string
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < input.length(); i++) {
        indices.add(i);
    }

    // 2. Shuffle the indices to randomize the selection
    Collections.shuffle(indices, new Random());

    // 3. Take the first X unique indices and sort them in reverse order
    // Sorting in reverse (descending) is crucial so that replacing a character
    // at a later index doesn't shift the positions of the earlier indices.
    List<Integer> targetIndices = indices.subList(0, actualReplacements);
    targetIndices.sort(Collections.reverseOrder());

    // 4. Perform the substitutions using a StringBuilder
    StringBuilder sb = new StringBuilder(input);
    for (int index : targetIndices) {
        sb.replace(index, index + 1, replacement);
    }

    return sb.toString();
}
    
    public static void main(String[] args) {
        System.out.println(StringHelper.replaceRandomCharacters("arvore", 3, "?"));
    }

}
