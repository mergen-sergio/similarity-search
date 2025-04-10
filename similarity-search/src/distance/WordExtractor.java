package distance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;


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

    
    
    public static ArrayList<TwoDPoint> create2DPoints() throws Exception {
    
        Random random = new Random();
        int LEN = 300;
        ArrayList<TwoDPoint> points = new ArrayList<>();
        for (int x=0;x<LEN;x++){
        for (int y=0;y<LEN;y++){
            int val = random.nextInt(20);
            if (val==0)
                points.add(new TwoDPoint(x, y));
        }
        }
        
        return points;
        
    }
    
      
    public static void removeDuplicates2(ArrayList<StringPoint> words) throws Exception {
    
        Hashtable<String, Integer> dic = new Hashtable<String, Integer>();
        
        
        for (int x=words.size()-1;x>=0;x--){
            String word = words.get(x).toString();
            if (dic.containsKey(word))
                words.remove(x);
            else dic.put(word, x);
                
        }
        
    }
    
    
    public ArrayList<StringPoint> feedStringPointWords(String file, int limit) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(file));
        ArrayList<StringPoint> words = new ArrayList<StringPoint>();
        int wordCount = -1;
        StringBuilder sb = new StringBuilder();

        try {
            String line = br.readLine();

            while (line != null) {
                wordCount++;
                if (wordCount>limit) return words; 
                int len = line.length();

                for (int x = 0; x < len; x++) {
                    sb.append(line.charAt(x));
                    if (line.charAt(x) == ' ' || x==45 ) {
                        wordCount++;
                        //sb.append(System.lineSeparator());
                        String aux = sb.toString().toLowerCase().replaceAll("[^a-zA-Z]+", "");
                        if (!aux.equals(""))
                            words.add(new StringPoint(aux));
                        sb.delete(0, sb.length());
                    }
                }
                if (sb.length()>0){
                    
                    String aux = sb.toString().toLowerCase().replaceAll("[^a-zA-Z]+", "");
                        if (!aux.equals(""))
                            words.add(new StringPoint(aux));
                    sb.delete(0, sb.length());
                }
                line = br.readLine();
            }

        } finally {
            br.close();
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
                StringTokenizer tok = new StringTokenizer(line," ");
                wordCount++;
                if (wordCount>limit) return words; 
                
                words.add(new TwoDPoint(Double.valueOf(tok.nextToken()),Double.valueOf(tok.nextToken())));
                line = br.readLine();
            }

        } finally {
            br.close();
        }

        return words;
    }

    


    
    
}
