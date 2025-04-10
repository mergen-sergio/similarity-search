/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trie.evaluation;

import trie.fastss.FastSimilarSearch;
import trie.result.RangeQueryResult;
import trie.result.TopKResult;

import trie.wrappers.SymSpellWrapper;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import trie.LinearSearch;
import trie.SpellChecking;
import trie.StringPoint;
import trie.Tester;
import trie.Trie2;
import trie.TrieWord;
import trie.WordExtractor;

/**
 *
 * @author pccli
 */
public class TrieTesterTopK {

    public static int search_count = 0;

    private int maxDist(List<String> results, String word) {
        int maxD = -1;
        for (int i = 0; i < results.size(); i++) {
            String result = results.get(i);
            int curDist = distance.Levenshtein.distance(result, word);
            if (curDist > maxD) {
                maxD = curDist;
            }

        }
        return maxD;
    }

    public void stringSearch(LinearSearch linearSearch, List<SpellChecking> spellCheckers, String word) throws Exception {

        RangeQueryResult range = new RangeQueryResult(1);

        //word = "abret";
        linearSearch.search(word, range);
        List<String> results = range.toArray();
        
        if (results.size() < 2) {
            return;
        }
        System.out.println("possible results");
        for (String result : results) {
            System.out.println(result);
        }

        int top = 3;

        TopKResult topkResults = new TopKResult(top);
        System.out.println("---- word: " + word);
        //word = "administation";
        Tester.search_count = 0;

        //word = "declassified";
        linearSearch.search(word, topkResults);
        results = topkResults.toArray();
        int maxD = maxDist(results, word);

        System.out.println("-- max dist found " + maxD + " k = "+ results.size() +" in LINEAR SEARCH");

        for (SpellChecking spellChecker : spellCheckers) {

            System.out.println("-- searching with " + spellChecker.getName());
            List<String> resultsX = spellChecker.topKSearch(word, top);
            int maxDX = maxDist(resultsX, word);

            if (maxDX != maxD ) {
                System.out.println("erro na disyancia maxima " + maxDX+" found "+maxD);
                if (maxD<=2)
                    throw new Exception("sss");
            }
            if (results.size()!=resultsX.size()){
                System.out.println("erro no numero de stings retornados " + results+" found "+resultsX.size());
                if (maxD<=2)
                    throw new Exception("sss");
            }

        }

    }

    public ArrayList<StringPoint> buildStringTree(LinearSearch linearSearch, List<SpellChecking> spellCheckers, String file, int d, int limit, int minWord, int maxWord) throws IOException, Exception {

        System.out.println("-- preparing dataset ");
        WordExtractor we = new WordExtractor();

        ArrayList<StringPoint> words = we.feedStringPointWords(file, limit, minWord, maxWord);

        WordExtractor.removeDuplicates2(words);

        List<String> words_ = words.stream().map(word -> new String(word.word))
                .collect(Collectors.toList());

        System.out.println("indexing "+words_.size()+" words");
        
        linearSearch.addWords(words_);

        for (SpellChecking spellChecker : spellCheckers) {
            System.out.println("adding word for " + spellChecker.getName());
            spellChecker.addWords(words_);
//            if (spellChecker instanceof LuceneWrapper) {
//                ((LuceneWrapper) spellChecker).addFromFile(file);
//            }

        }

        return words;
    }

    public ArrayList<String> createQueryFile(String file, ArrayList<StringPoint> points, int size) {
        Writer writer = null;

        ArrayList<String> result = new ArrayList<String>();

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "utf-8"));

            int offset = points.size() / size;
            Random random = new Random();
            StringPoint sp = points.get(random.nextInt(points.size()));
            int x = 0;
            while (x < size) {
                //sp = points.get(random.nextInt(points.size()));
                sp = points.get(x * offset);
//                while (sp.word.length() < 5) {
//                    sp = points.get(random.nextInt(points.size()));
//                }
                String word = sp.word;
//                String t1 = word.substring(0, 1);
//                char c1 = word.charAt(1);
//                char c2 = word.charAt(2);
//                char c3 = word.charAt(2);
//                char c4 = word.charAt(4);
//                String t2 = word.substring(4, word.length());
                //word = t1 + c2 + c1 + c4 + c3 + t2;

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


    public ArrayList<StringPoint> trieWord2StringPoint(ArrayList<TrieWord> list) {

        ArrayList<StringPoint> result = new ArrayList<StringPoint>();
        for (int i = 0; i < list.size(); i++) {
            result.add(new StringPoint(list.get(i).words.get(0)));
        }

        return result;
    }

    public void run() throws Exception {
        try {

            LinearSearch linearSearch = new LinearSearch();
            FastSimilarSearch fastss = new FastSimilarSearch();

            //BkTree1 vpTree = new BkTree1();

            List<SpellChecking> checkers = new ArrayList();

            checkers.add(new Trie2(20, 2));
            //checkers.add(new Trie2(30));
            //checkers.add(new FastSSWrapper());
            checkers.add(new SymSpellWrapper(2, 30));
            //checkers.add(new BkTreeClusterE(1));
            //checkers.add(new VpTreeE(1));
            //checkers.add(new SymSpellXWrapper());
            //checkers.add(new LuceneWrapper());
            //checkers.add(new LevAutomatonWrapper());

            int d = 1;
            //ArrayList<StringPoint> points = buildStringTree(trie, trie2, fastss,vpTree,  "C:\\teste\\catching_fire_sep.txt", d);
            //ArrayList<StringPoint> points = buildStringTree(trie, trie2, fastss,vpTree,  "C:\\teste\\compressao\\large_\\proj_gutemberg_sep.txt", d);
            //ArrayList<StringPoint> points = buildStringTree(trie, trie2, fastss,vpTree,  "C:\\teste\\compressao\\english.50MB\\very_large_sep.txt", d);
            //ArrayList<StringPoint> points = buildStringTree(linearSearch, checkers, "C:\\teste\\\\mwords\\354984si.ngl", d);
            //ArrayList<StringPoint> points = buildStringTree(linearSearch, checkers, "C:\\teste\\\\words.txt", d);
            ArrayList<StringPoint> points = buildStringTree(linearSearch, checkers, "C:\\teste\\google_ngrams.txt", d, 100000, 3, 49);
            //ArrayList<StringPoint> points = we.feedWords("C:\\teste\\mwords\\354984si.ngl", 99);
            //ArrayList<StringPoint> points2 = we.feedWords("C:\\teste\\mwords\\256772co.mpo", 99);
            d = 2;

              ArrayList<String> queries = createQueryFile("C:\\teste\\query1.txt", points, 300);
              
//            System.out.println("run-time test");
//            for (int x = 0; x < 10; x++) {
//
//                for (SpellChecking checker : checkers) {
//                    long start = System.currentTimeMillis();
//                    for (int i = 1; i < queries.size(); i++) {
//                        //System.out.println("query "+i);
//                        checker.topKSearch(queries.get(i), 3);
//                    }
//                    long end = System.currentTimeMillis();
//                    System.out.println(checker.getName() + " time " + (end - start));
//
//                }
//            }

            //if (1==1) return;
            Random random = new Random();

            StringPoint sp;
            d = 2;
            //for (int i = queries.size()-1;i>=0; i--) {
            for (int i = 0; i < points.size(); i++) {
            //while (true) {
                //sp = new StringPoint(queries.get(i));
                sp = points.get(i);
                //sp = points.get(random.nextInt(points.size()));
                //while (sp.word.length()<10)
                //sp = points.get(random.nextInt(points.size()));
                //sp = new StringPoint("bei");
                for (int x = 1; x <= d; x++) {
                    stringSearch(linearSearch, checkers, sp.word);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TrieTesterTopK t = new TrieTesterTopK();
        try {
            t.run();
//            Byte array[] = new Byte[5];
//            array[0] = new Byte("1");
//            array[1] = new Byte("26");
//            array[2] = new Byte("43");
//            array[3] = new Byte("6");
//            array[4] = new Byte("8");
//            Arrays.sort(array);
//            for (int i = 0; i < array.length; i++) {
//                System.out.println(array[i]);
//            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
