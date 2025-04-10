/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation;

import java.util.ArrayList;
import java.util.List;
import spell_checker.LinearSearch;
import spell_checker.SpellChecking;
import object.StringPoint;

/**
 *
 * @author pccli
 */
public class Evaluator {

    public static final int BUILD = 1;
    public static final int RANGE_QUERY = 2;
    public static final int TOPK_QUERY = 3;

//    public static final int CHECK = 4;
//    public static final int PREPARE = 5;
//    public static final int BUILD_ALL = 6;

    public void run() throws Exception {
        try {

            //provide building structure for all indexnig methods
            IndexBuilder indexBuilder = new IndexBuilder();
            
            //brute-force method used for comparisons
            LinearSearch linearSearch = new LinearSearch();

            //action to be executed
            int type = Evaluator.TOPK_QUERY;

            //max edit distance for those methods that need it for index construction
            int maxEditDist = 2;

            //indexing methods
            List<Integer> checkerTypes = new ArrayList();
            checkerTypes.add(SpellCheckerBuilder.ATRIE);
            checkerTypes.add(SpellCheckerBuilder.LTRIE);
            //checkerTypes.add(LTRIE);
            List<SpellChecking> checkers = SpellCheckerBuilder.createSpellCheckers(checkerTypes, maxEditDist, 30);

            //dataset
            String dataFile = "googlebooks_cleaned.txt";
            //size of the dataset to process
            int limit = 999999999;
            //minimal and maximal word sizes
            int minWord = 3;
            int maxWord = 49;

            //queryset
            String queryFile = "queries_large_words.txt";
            //whether to add a typo in each word of the queryset
            boolean includeOneTypo = true;

            //number of executions
            int times = 100;

            //max edit distance for range queries
            int d = 2;

            //max k for top-k queries
            int topk = 5;

            switch (type) {
//                case BUILD_ALL:
//                    indexBuilder.evaluateBuilding(checkers, dataFile, d, 50000);
//                    break;
                case BUILD:
                    indexBuilder.evaluateBuilding(checkers, dataFile, maxEditDist);
                    break;
//                case PREPARE:
//                    DictionaryCreator dicCreator = new DictionaryCreator();
//                    dicCreator.prepareDictionaryFiles(dataFile, false, d, 50000, minWord, maxWord);
//                    break;
                case RANGE_QUERY:
                    System.out.println("-- preparing dataset ");
                    ArrayList<StringPoint> points = StringLoader.loadStrings(dataFile, limit, minWord, maxWord, false);
                    indexBuilder.buildIndexes(points, linearSearch, checkers, dataFile, d, limit, minWord, maxWord);
                    System.out.println("");
                    RangeQueryEvaluator rangeQueryEval = new RangeQueryEvaluator();
                    rangeQueryEval.evaluateRangeQueries(linearSearch, checkers, dataFile, queryFile, d, points.size(), times, includeOneTypo);
                    System.out.println("");

                    break;
                case TOPK_QUERY:
                    System.out.println("-- preparing dataset ");
                    points = StringLoader.loadStrings(dataFile, limit, minWord, maxWord, false);
                    indexBuilder.buildIndexes(points, linearSearch, checkers, dataFile, d, limit, minWord, maxWord);
                    System.out.println("");
                    TopkQueryEvaluator topQueryEval = new TopkQueryEvaluator();
                    topQueryEval.evaluateTopkQueries(linearSearch, checkers, dataFile, queryFile, topk, points.size(), times, includeOneTypo);
                    System.out.println("");

                    break;
//                case CHECK:
//                    points = StringLoader.loadStrings(dataFile, limit, minWord, maxWord, false);
//                    indexBuilder.buildIndexes(points, linearSearch, checkers, dataFile, d, 1000000, minWord, maxWord);
//                    IndexValidator indexValidator = new IndexValidator();
//                    indexValidator.checkPrecision(linearSearch, checkers, dataFile, d);
//                    break;
                default:
                    break;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Evaluator t = new Evaluator();
        try {
            t.run();
        } catch (Exception ex) {
        }

    }

}
