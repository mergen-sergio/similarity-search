/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import evaluation.StringLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class StringLengthFrequencyLoop {
    public static void main(String[] args) {
        //String dataFile = "c:\\teste\\palavras_sujas_wikipedia_range2.txt";
        String dataFile = "googlebooks_cleaned.txt";
        StringLoader loader = new StringLoader();
        try {
            List<String> words = loader.loadStrings(dataFile, 2000000, false, 1000);
        
        // Compute frequency of string lengths
        Map<Integer, Long> lengthFrequency = words.stream()
                .filter(str -> str != null) // Avoid NullPointerException
                .collect(Collectors.groupingBy(
                        String::length, 
                        Collectors.counting()
                ));

        // Print each result on a new line in the format: size, quantity
        lengthFrequency.forEach((size, quantity) -> 
                System.out.println(size + ", " + quantity)
        );
        
        } catch (Exception ex) {
            Logger.getLogger(StringLengthFrequencyLoop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}