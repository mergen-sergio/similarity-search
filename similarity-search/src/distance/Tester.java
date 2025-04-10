/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distance;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author pccli
 */
public class Tester {

    

    public void stringSearch(ArrayList<MetricDistanceSearchTree<StringPoint>> strategies,
            StringPoint point, int dist) throws Exception {

        System.out.println("---- word: " + point + " dist: " + dist);

        for (int x = 0; x < strategies.size(); x++) {

            Levenshtein.search_count = 0;
            System.out.println("-- " + strategies.get(x).getStrategy());
            ArrayList<StringPoint> results2 = strategies.get(x).search(point, dist);
            System.out.println("-- found " + results2.size() + " results in " + Levenshtein.search_count + " nodes");

        }

    }

    public void buildIndex(ArrayList<MetricDistanceSearchTree<StringPoint>> strategies,
            ArrayList<StringPoint> words) {
        int size = words.size();

        for (int y = 0; y < strategies.size(); y++) {

            Levenshtein.search_count = 0;
            System.out.println("-- " + strategies.get(y).getStrategy() + " creation");
            strategies.get(y).addNodes(words);
            System.out.println("--  distances computed " + Levenshtein.search_count);

        }

    }

    public void run() {
        try {
            
            ArrayList<MetricDistanceSearchTree<StringPoint>> strategies = new ArrayList<MetricDistanceSearchTree<StringPoint>>();
            BkTree bkTree = new BkTree();
            VpTree vpTree = new VpTree();
            strategies.add(bkTree);
            strategies.add(vpTree);

            System.out.println("-- preparing dataset ");
            WordExtractor we = new WordExtractor();
            ArrayList<StringPoint> points = we.feedStringPointWords("C:\\teste\\mwords\\354984si.ngl", 99999999);
            WordExtractor.removeDuplicates2(points);
            buildIndex(strategies, points);

            System.out.println("-- querying ");
            int max = 5;
            Random random = new Random();
            StringPoint sp = points.get(random.nextInt(points.size()));
            while (true) {
                sp = points.get(random.nextInt(points.size()));
                while (sp.word.length() > 3) {
                    sp = points.get(random.nextInt(points.size()));
                }
                for (int x = 0; x <= max; x++) {
                    stringSearch(strategies, sp, x);
                }
            }
            
        } catch (Exception ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        Tester t = new Tester();
        t.run();

    }

}
