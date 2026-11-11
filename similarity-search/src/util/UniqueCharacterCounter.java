/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import evaluation.StringLoader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UniqueCharacterCounter {
    public static void main(String[] args) {
        String dataFile = "c:\\teste\\humandna.txt";
        StringLoader loader = new StringLoader();
        try {
            List<String> words = loader.loadStrings(dataFile, 2000000, false, 1000);

        long uniqueCharsCount = words.stream()
                .filter(str -> str != null)          // Safe guard against nulls
                .flatMapToInt(String::chars)         // Flatten all strings into a stream of characters (int values)
                .distinct()                          // Keep only unique characters
                .count();                            // Count them

        System.out.println("Number of different characters: " + uniqueCharsCount);
        } catch (Exception ex) {
            Logger.getLogger(StringLengthFrequencyLoop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}