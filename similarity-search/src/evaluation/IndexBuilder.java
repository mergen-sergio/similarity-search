/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package evaluation;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import spell_checker.LinearSearch;
import spell_checker.SpellChecking;
import object.StringPoint;

/**
 *
 * @author ferna
 */
public class IndexBuilder {

    private void measureMemory1() {
        // Compute memory usage
        System.gc();
        Runtime runtime1 = Runtime.getRuntime();
        long totalMemory1 = runtime1.totalMemory(); // Total allocated memory
        long freeMemory1 = runtime1.freeMemory();   // Unused allocated memory
        long usedMemory1 = totalMemory1 - freeMemory1; // Actual used memory

        System.out.println("Total Memory: " + totalMemory1 / (1024 * 1024) + " MB");
        System.out.println("Free Memory: " + freeMemory1 / (1024 * 1024) + " MB");
        System.out.println("Used Memory: " + usedMemory1 / (1024 * 1024) + " MB");
    }

    public void buildIndexes(List<StringPoint> words, LinearSearch linearSearch, List<SpellChecking> spellCheckers, String file, int d, int limit, int minWord, int maxWord) throws IOException, Exception {

        List<String> words_ = words.stream().map(word -> new String(word.word))
                .collect(Collectors.toList());

        System.out.println("");

        System.out.println("-- building indexes");
        linearSearch.addWords(words_);

        for (SpellChecking spellChecker : spellCheckers) {
            System.gc();
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage beforeHeapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
            long start = System.currentTimeMillis();
            System.out.println("building " + spellChecker.getName());
            spellChecker.addWords(words_);

            long end = System.currentTimeMillis();
            long time = end - start;
            System.out.println("time to build(ms) :" + time);

            System.gc();
            MemoryUsage afterHeapMemoryUsage = memoryMXBean.getHeapMemoryUsage();

            long usedMemoryBefore = beforeHeapMemoryUsage.getUsed();
            long usedMemoryAfter = afterHeapMemoryUsage.getUsed();
            System.out.println("Used Memory: " + ((usedMemoryAfter - usedMemoryBefore) / 1024) + " kb");
            System.out.println("");

        }

    }

    public void evaluateBuilding(List<SpellChecking> spellCheckers, String filePath, int d, int gap) throws IOException, Exception {

        DictionaryCreator dicCreator = new DictionaryCreator();
        dicCreator.setFile(filePath);

        List<List<Object>> allMeasures = new ArrayList();

        List<Object> oneMeasure = new ArrayList();
        oneMeasure.add("size");
        for (SpellChecking spellChecker : spellCheckers) {
            oneMeasure.add(spellChecker.getName());
        }
        allMeasures.add(oneMeasure);
        System.out.println(oneMeasure);

        int initGap = gap;
        int startGap;
        for (startGap = initGap * 1; startGap <= initGap * 4; startGap += initGap) {
            //for (startGap = gap * 4; startGap >= gap * 1; startGap -= initGap) {
            String newFile = dicCreator.getSuffixedFileName(String.valueOf(startGap));
            System.out.println("-- reading file " + newFile);
            ArrayList<StringPoint> words = StringLoader.loadStringPoints(newFile, Integer.MAX_VALUE, 0, 49);

            List<String> words_ = words.stream().map(word -> new String(word.word))
                    .collect(Collectors.toList());

            oneMeasure = new ArrayList();
            oneMeasure.add(startGap);
            for (SpellChecking spellChecker : spellCheckers) {
                long minTime = Long.MAX_VALUE;
                for (int i = 0; i < 5; i++) {
                    long start = System.currentTimeMillis();
                    System.out.println("adding word for " + spellChecker.getName());

                    spellChecker.addWords(words_);
                    long end = System.currentTimeMillis();
                    long time = end - start;
                    if (time < minTime) {
                        minTime = time;
                    }
                }
                oneMeasure.add(minTime);

            }
            allMeasures.add(oneMeasure);
            System.out.println(oneMeasure);
            //if (1==1) break;
        }

        for (int i = allMeasures.size() - 1; i >= 0; i--) {
            List<Object> oneMeasure_ = allMeasures.get(i);
            boolean first = true;
            System.out.println("");
            for (Object object : oneMeasure_) {
                if (!first) {
                    System.out.print(", ");
                }
                System.out.print(object);
                first = false;
            }
        }
    }

    public void evaluateBuilding(List<SpellChecking> spellCheckers, String filePath, int d) throws IOException, Exception {

        List<List<Object>> allMeasures = new ArrayList();

        List<Object> oneMeasure = new ArrayList();
        oneMeasure.add("size");
        for (SpellChecking spellChecker : spellCheckers) {
            oneMeasure.add(spellChecker.getName());
        }
        allMeasures.add(oneMeasure);
        System.out.println(oneMeasure);

        System.out.println("-- reading file " + filePath);
        ArrayList<StringPoint> words = StringLoader.loadStringPoints(filePath, Integer.MAX_VALUE, 0, 49);

        List<String> words_ = words.stream().map(word -> new String(word.word))
                .collect(Collectors.toList());

        oneMeasure = new ArrayList();
        oneMeasure.add(words.size());
        for (SpellChecking spellChecker : spellCheckers) {
            long start = System.currentTimeMillis();
            System.out.println("adding word for " + spellChecker.getName());

            spellChecker.addWords(words_);
            long end = System.currentTimeMillis();
            long time = end - start;
            oneMeasure.add(time);

        }
        allMeasures.add(oneMeasure);
        System.out.println(oneMeasure);
        //if (1==1) break;

        for (int i = allMeasures.size() - 1; i >= 0; i--) {
            List<Object> oneMeasure_ = allMeasures.get(i);
            boolean first = true;
            System.out.println("");
            for (Object object : oneMeasure_) {
                if (!first) {
                    System.out.print(", ");
                }
                System.out.print(object);
                first = false;
            }
        }
    }
}
