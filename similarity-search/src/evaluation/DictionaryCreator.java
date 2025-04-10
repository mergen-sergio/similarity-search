/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package evaluation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import object.StringPoint;
import util.StringHelper;

/**
 *
 * @author ferna
 */
public class DictionaryCreator {
    
    String directory;
    String extension;
    String fileNameWithoutExtension;
    
    
    public String getSuffixedFileName(String suffix){
        return directory + "\\" + fileNameWithoutExtension + "_" + suffix + "." + extension;
    }
    public String getDirectory(){
        return directory;
    }
    
    public String getExtension(){
        return fileNameWithoutExtension;
    }
    
    public String getFileNameWithoutExtension(){
        return fileNameWithoutExtension;
    }
    
    public void setFile(String filePath){
    // Criar um objeto File com o caminho do arquivo
        File file = new File(filePath);

        // Extrair o diretório
        directory = file.getParent();

        // Extrair o nome do arquivo sem a extensão
        fileNameWithoutExtension = file.getName().replaceFirst("[.][^.]+$", "");

        // Extrair a extensão do arquivo
        extension = "";
        int lastDotIndex = file.getName().lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = file.getName().substring(lastDotIndex + 1);
        }
    }
    
    public void prepareDictionaryFiles(String filePath, boolean shuffle, int d, int limit, int minWord, int maxWord) throws IOException, Exception {

        setFile(filePath);

        StringHelper we = new StringHelper();
        System.out.println("Reading file");
        ArrayList<StringPoint> words = we.createStringPoints(filePath, 999999999, minWord, maxWord);
        //ArrayList<StringPoint> words = we.buildNLetterWords(10,99999);
        StringHelper.removeDuplicates(words);

        if (shuffle) {
            Collections.shuffle(words);
        }

        System.out.println("generating files");
        ArrayList<String> data;
        int initLimit = limit;
        for (; limit <= initLimit * 10 && limit < words.size(); limit += initLimit) {
            data = new ArrayList();
            for (int i = 0; i < limit; i++) {
                StringPoint word = words.get(i);
                data.add(word.word);
            }
            //System.out.println(data.toString());
            String newFile = directory + "\\" + fileNameWithoutExtension + "_" + limit + "." + extension;
            System.out.println("File: " + newFile);
            we.writeFile(newFile, data);

        }

    }
}
