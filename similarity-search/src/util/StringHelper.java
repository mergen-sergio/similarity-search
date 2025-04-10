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
import java.util.HashSet;
import java.util.Hashtable;

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

}
