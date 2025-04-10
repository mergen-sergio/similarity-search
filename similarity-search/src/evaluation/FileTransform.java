package evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
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
public class FileTransform {

    public static int TAM = 256;
    

public ArrayList<String> feedWords(String file) throws Exception{
BufferedReader br = new BufferedReader(new FileReader(file));

ArrayList<String> words = new ArrayList<String>();
StringBuilder sb = new StringBuilder();

try {
    String line = br.readLine();
    
    int wordSize = -1;
    while (line != null) {
        
        int len = line.length();
        Random random = new Random();
        int maxSize = random.nextInt(200);
        for(int x=0;x<len;x++){
            wordSize++;
            
            sb.append(line.charAt(x));
            if (line.charAt(x) == ' '){
            //if (wordSize>maxSize){
                wordSize = 0;
                maxSize = random.nextInt(200);
                String aux = sb.toString().replaceAll("[^a-zA-Z]+", "");
                aux = aux.toLowerCase();
                if (aux.length()>0)
                words.add(aux);
                sb.delete(0, sb.length());
            }
        }
        if (sb.length()>0){
                wordSize = 0;
                String aux = sb.toString().replaceAll("[^a-zA-Z]+", "");
                aux = aux.toLowerCase();
                if (aux.length()>0)
                    words.add(aux);
                sb.delete(0, sb.length());
            }
        
        line = br.readLine();
    }
    
    

    
} finally {
    br.close();
}

return words;    
}



public void writeFile(String file, ArrayList<String> list, int max){
    Writer writer = null;

try {
    writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(file), "utf-8"));
    for (int x=0;x<list.size();x++){
        writer.write(list.get(x));
        writer.write(System.lineSeparator());
        if (x>max) break;
    }
    
        
} catch (IOException ex) {
  // report
} finally {
   try {writer.close();} catch (Exception ex) {/*ignore*/}
}
}


public static void removeDuplicates2(ArrayList<String> words) throws Exception {
    
        Hashtable<String, Integer> dic = new Hashtable<String, Integer>();
        
        
        for (int x=words.size()-1;x>=0;x--){
            String word = words.get(x).toString();
            if (dic.containsKey(word))
                words.remove(x);
            else dic.put(word, x);
                
        }
        
    }
    public static void main(String[] args) {
        FileTransform ws = new FileTransform();
    try {
        //String input = "C:\\teste\\catching_fire.txt";
        //String output = "C:\\teste\\catching_fire_sep.txt";
        
        //String input = "C:\\teste\\compressao\\large_\\proj_gutemberg.txt";
        //String output = "C:\\teste\\compressao\\large_\\proj_gutemberg_sep.txt";
        
        String input = "C:\\teste\\compressao\\english.50MB\\very_large.txt";
        String output = "C:\\teste\\compressao\\english.50MB\\very_large_sep.txt";
        
        
        
        
        ArrayList<String> finalList = ws.feedWords(input);
        //Collections.sort(finalList);
        
        removeDuplicates2(finalList);
        
        ws.writeFile(output, finalList, 999999999);
        System.out.println(finalList.size());
        
        
    } catch (Exception ex) {
        Logger.getLogger(FileTransform.class.getName()).log(Level.SEVERE, null, ex);
    }
    }

}
