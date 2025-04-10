/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trie.evaluation;

import java.util.ArrayList;
import java.util.List;
import trie.LinearSearch;
import trie.SpellChecking;
import trie.StringPoint;
import static trie.evaluation.SpellCheckerBuilder.*;

/**
 *
 * @author pccli
 */
public class TrieTester11 {

    public static int search_count = 0;

    public void run(int type) throws Exception {
        try {

            DictionaryCreator dicCreator = new DictionaryCreator();
            IndexBuilder indexBuilder = new IndexBuilder();
            QueryCreator queryCreator = new QueryCreator();
            RangeQueryEvaluator rangeQueryEval = new RangeQueryEvaluator();
            TopkQueryEvaluator topQueryEval = new TopkQueryEvaluator();
            IndexValidator indexValidator = new IndexValidator();
            StringLoader stringLoader = new StringLoader();

            LinearSearch linearSearch = new LinearSearch();

            List<Integer> checkerTypes = new ArrayList();
            checkerTypes.add(ATRIE);
            
            
//            checkerTypes.add(LUCENE_AUTOMATON);
//            checkerTypes.add(LEV_AUTOMATON);
            checkerTypes.add(ATRIE);
            checkerTypes.add(LTRIE);
//            checkerTypes.add(BKTREE);
//             checkerTypes.add(VPTREE);
//            checkerTypes.add(SYMSPELL);
//            checkerTypes.add(NGRAM2);
//            checkerTypes.add(NGRAM3);

//            checkerTypes.add(ATRIE);
//            checkerTypes.add(ATRIE_2);
//            checkerTypes.add(ATRIE_3);
//            checkerTypes.add(ATRIE_4);
//            checkerTypes.add(ATRIE_UNBOUNDED);
//            checkerTypes.add(SYMSPELL);
//            checkerTypes.add(NGRAM3);

            List<SpellChecking> checkers = SpellCheckerBuilder.createSpellCheckers(checkerTypes, 2, 30);
            //String dataFile = "C:\\teste\\trie\\googlebooks_cleaned.txt";
            String dataFile = "C:\\teste\\trie\\googlebooks_cleaned_200000.txt";
            //String dataFile = "C:\\teste\\trie\\teste.txt";
            //String queryFile = "C:\\teste\\trie\\query.txt";
            
            //String queryFile = "C:\\teste\\trie\\queries_200k_nonExisting_words.txt";
            //String queryFile = "C:\\teste\\trie\\queries_200k_small_words.txt";
            //String queryFile = "C:\\teste\\trie\\query001.txt";
            String queryFile = "C:\\teste\\trie\\queries_200k_medium_words.txt";
            //String queryFile = "C:\\teste\\trie\\queries_200k_large_words.txt";
            //String queryFile = "C:\\teste\\trie\\queries_200k_20.txt";
            //"C:\\teste\\google_ngrams.txt"
            int gap = 50000;
            int limit = 999999999;
            int minWord = 3;
            int maxWord = 49;
            int times = 100;
            boolean verbose = true;
            boolean includeOneTypo = true;

            int d = 3;
            switch (type) {
                case BUILD_ALL:
                    indexBuilder.evaluateBuilding(checkers, dataFile, d, gap);
                    break;
                case BUILD_ONE:
                    indexBuilder.evaluateBuilding(checkers, dataFile, d);
                    break;
                case PREPARE:
                    dicCreator.prepareDictionaryFiles(dataFile, false, d, gap, minWord, maxWord);
                    break;
                case QUERY:
                    ArrayList<StringPoint> points = StringLoader.loadStrings(dataFile, limit, minWord, maxWord, false);
                    //QueryCreator qc = new QueryCreator();
                    //qc.createQueryFile("c:\\teste\\trie\\query001.txt", points, 50, 5, 10);
                    indexBuilder.buildIndexes(points, linearSearch, checkers, dataFile, d, limit, minWord, maxWord);
                    if (1 == 0) {
                        System.out.println("return without querying");
                        break;
                    }
                    
                    //for (int i = 11; i <= 11; i++) 
                     {
                        //queryFile = "C:\\teste\\trie\\queries_200k_" + i + ".txt";
                        //System.out.println("SIZE = " + i);
                        rangeQueryEval.evaluateRangeQueries(linearSearch, checkers, dataFile, queryFile, d, points.size(), includeOneTypo);
                        System.out.println("");
                    }

                    break;
                case TOPK:
                    points = StringLoader.loadStrings(dataFile, limit, minWord, maxWord, false);
                    indexBuilder.buildIndexes(points, linearSearch, checkers, dataFile, d, limit, minWord, maxWord);
                    int topk = 1;
                    //for (int i = 16; i <= 20; i++)
                    //for (; topk <= 10; topk+=2) 
                     {
                        //queryFile = "C:\\teste\\trie\\queries_200k_" + i + ".txt";
                        //System.out.println("SIZE = " + i);
                        topQueryEval.evaluateTopkQueries(linearSearch, checkers, dataFile, queryFile, topk, points.size(), times, verbose, includeOneTypo);
                        System.out.println("");
                    }

                    break;
                case CHECK:
                    points = StringLoader.loadStrings(dataFile, 1000000, minWord, maxWord, false);
                    indexBuilder.buildIndexes(points, linearSearch, checkers, dataFile, d, 1000000, minWord, maxWord);
                    indexValidator.checkPrecision(linearSearch, checkers, dataFile, d);
                    break;
                default:
                    break;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static final int PREPARE = 1;
    public static final int BUILD_ALL = 2;
    public static final int QUERY = 3;
    public static final int CHECK = 4;
    public static final int BUILD_ONE = 5;
    public static final int TOPK = 6;

    public static void main(String[] args) {
        TrieTester11 t = new TrieTester11();
        try {
            t.run(TOPK);
        } catch (Exception ex) {
        }

    }

}
