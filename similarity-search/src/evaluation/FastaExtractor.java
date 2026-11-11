/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FastaExtractor {

    /**
     * Processes a .fna file in streaming mode. Removes 'N', splits into words
     * of length wordLength, writes directly to output. Stops after maxWords are
     * written.
     */
    public static void processFnaFile(String inputPath, int wordLength, String outputPath, long maxWords) throws IOException {

        Set<String> uniqueWordsRegistry = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputPath)); BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {

            String line;
            StringBuilder sequenceBuffer = new StringBuilder();
            long wordCount = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith(">")) {
                    // Process leftover sequence before new header
                    wordCount = processSequence(uniqueWordsRegistry, sequenceBuffer, wordLength, bw, wordCount, maxWords);
                    sequenceBuffer.setLength(0); // reset buffer
                    if (wordCount >= maxWords) {
                        break;
                    }
                } else {
                    // Append cleaned sequence (remove N/n and spaces)
                    sequenceBuffer.append(line.replaceAll("[Nn\\s]", "").toLowerCase());
                }
            }

            // Process last sequence
            if (wordCount < maxWords) {
                processSequence(uniqueWordsRegistry, sequenceBuffer, wordLength, bw, wordCount, maxWords);
            }
        }
    }

    /**
     * Splits sequence into words and writes them to output. Uses sliding window
     * approach.
     */
    private static long processSequence(Set<String> uniqueWordsRegistry, StringBuilder sequence, int wordLength, BufferedWriter bw, long wordCount, long maxWords) throws IOException {
        for (int i = 0; i <= sequence.length() - wordLength; i++) {
            if (wordCount >= maxWords) {
                break;
            }
            String cleanToken = sequence.substring(i, i + wordLength);
            if (uniqueWordsRegistry.add(cleanToken)) {
                bw.write(cleanToken);
                bw.newLine();
                wordCount++;
                if (wordCount % 50000 == 0) {
                    System.out.println(" -> " + wordCount + " palavras purificadas indexadas...");
                }
            }
        }
        return wordCount;
    }

    public static void main(String[] args) {

        String inputFile = "C:\\Users\\ferna\\Downloads\\ncbi_dataset\\ncbi_dataset\\data\\GCA_000001405.29\\GCA_000001405.29_GRCh38.p14_genomic.fna";

        int wordLength = 30;
        long maxWords = 2000000;
        String outputFile = "c:\\teste\\humanDNA30.txt";

        try {
            processFnaFile(inputFile, wordLength, outputFile, maxWords);
            System.out.println("Processing complete. Output saved to " + outputFile);
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }
}
