/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spell_checker.trie_based;

import metric.EditDistance;
import query.result.RangeQueryResult;
import query.result.Result;
import query.result.TopKResult;
import io.gitlab.rxp90.jsymspell.api.DamerauLevenshteinOSA;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import spell_checker.SpellChecking;

/**
 * this is the same as Trie2, but uses array instead of a chained list
 * @author Sergio
 */
public class ATrie_ implements SpellChecking {

    public TrieNode root;
    //Levensthein lev = new Levensthein();
    //UKonnen lev = new UKonnen();
    DamerauLevenshteinOSA dam = new DamerauLevenshteinOSA();

    boolean debug = false;

    public static int NUM_NODES = 0;
    public static int NUM_ESCAPED_NODES = 0;
    public static int NUM_ACCESSED_NODES = 0;

    int prefixSize;

    public class TrieNode {

        public TrieWord word;
        public byte c;
        public int type;
        public TrieNode[] children = new TrieNode[5];

        int numberOfChildren = 0;
        int deepestLevel = 0;
        int minLevelAhead = 0;
        public TrieNode parent;

        public String getPath(){
            String path = "";
            TrieNode parent = this;
            while (parent!=null){
                path+=TrieWord.getChar(parent.c);
                parent = parent.parent;
            }
            return path;
        }

        public String toString() {
            if (word != null) {
                return word.toString();
            } else {
                return String.valueOf(c);
            }
        }
      public void addNode(TrieNode node) {
            if (numberOfChildren == children.length) {
                TrieNode[] copy = new TrieNode[children.length + 5];
                System.arraycopy(children, 0, copy, 0, children.length);
                children = copy;

            }
            children[numberOfChildren] = node;
            node.parent = this;
            numberOfChildren++;
        }
    }

    public ATrie_(int prefixSize) {
        this.prefixSize = prefixSize;
        root = new TrieNode();
        //root.c = '?';
        root.type = 0;
    }

    @Override
    public String getName() {
        return "Trie with Anagram 21";
    }

    @Override
    public void addWords(List<String> words) {
        ArrayList<TrieWord> list = new ArrayList<TrieWord>();

        for (int i = 0; i < words.size(); i++) {
            TrieWord tw = new TrieWord(words.get(i), prefixSize);
            list.add(tw);
        }
        Collections.sort(list);

        for (int i = 0; i < list.size(); i++) {
            addWord(list.get(i));
        }
        setMinimum();

    }

    public void addWord(TrieWord word) {
        addWord(root, word, 0);
    }

    private void updateDeepestLevel(TrieNode node, int level) {
        while (node.type != 0) {
            if (level > node.deepestLevel) {
                node.deepestLevel = level;
            }
            node = node.parent;
        }
    }

    private void addWord(TrieNode parent, TrieWord word, int index) {

        for (int i = 0; i < parent.numberOfChildren; i++) {
            TrieNode node = parent.children[i];
            if (node.c == word.permut[index]) {

                if (word.permut.length <= index + 1) {
                    if (node.word==null)
                        node.word = new TrieWord(word.words.get(0), prefixSize);
                    else node.word.words.add(word.words.get(0));
                } else {
                    addWord(node, word, index + 1);
                }
                return;
            }
        }

        TrieNode newNode = new TrieNode();
        NUM_NODES++;
        newNode.type = 1;

        newNode.c = word.permut[index];
        parent.addNode(newNode);

        updateDeepestLevel(newNode, index);

        if (word.permut.length <= index + 1) {
            newNode.word = new TrieWord(word.words.get(0), prefixSize);
            return;
        } else {

            addWord(newNode, word, index + 1);
            return;
        }

    }

    //@Override
    public List<String> search(String q, int d) {
        //System.out.println("printing");

//        TopKResult topK = new TopKResult(3);
//        topK.maxDist = 4;
////        RangeQueryResult topK = new RangeQueryResult(2);
//        topkSearch(q, topK);
//        return topK.toArray();
//        
        RangeQueryResult result = new RangeQueryResult(d);
        ArrayList<String> list = new ArrayList<>();
        TrieNode nodes[] = root.children;
        TrieWord query = new TrieWord(q, prefixSize);
        for (int i = 0; i < root.numberOfChildren; i++) {
            TrieNode node = nodes[i];
            //searchB(node, query, d, list, 0, 0, 0, 0, 0);
            searchB(node, query, result, 0, 0, 0, 0, 0);
        }
        //return list;
        return result.toArray();
    }

        public List<String> topKSearch(String query, int topK){
            TopKResult results = new TopKResult(topK);
            topkSearch(query, results);
        return results.toArray();
    
    }

    public void topkSearch(String q, TopKResult result) {
        //private void search2(TrieWord query, Result result) {
        TrieWord query = new TrieWord(q, prefixSize);
        Queue<QueueNode> nodes = new PriorityQueue<>();
        TrieNode nodes_[] = root.children;
        for (int i = 0; i < root.numberOfChildren; i++) {
            TrieNode trieNode = nodes_[i];
            QueueNode node = new QueueNode();
            //node.text+=TrieWord.getChar(trieNode.c);
            node.node = trieNode;
            node.difA = 0;
            node.difB = 0;
            node.hits = 0;
            node.index = 0;
            node.level = 0;
            nodes.add(node);
        }

        while (!nodes.isEmpty()) {

            QueueNode metaNode = nodes.poll();

            if (metaNode.index - metaNode.hits >= result.maxDist) {
                break;
            }

            int dist = Math.max(metaNode.index, metaNode.level) - metaNode.hits;
            if (dist >= result.maxDist) {
                break;
            }

            //if (level>d-hits)
            int dist3 = Math.max(metaNode.difA, metaNode.difB);
            if (dist3 >= result.maxDist) {
                break;
            }

//            if (debug) {
//                //System.out.println(metaNode + " => " + metaNode.text);
//            }

            metaNode.difB++;
            while (metaNode.index < query.permut.length && metaNode.node.c > query.permut[metaNode.index]) {
                metaNode.index++;
                metaNode.difA++;
            }

            if (metaNode.index < query.permut.length && metaNode.node.c == query.permut[metaNode.index]) {
                metaNode.index++;
                metaNode.hits++;
                metaNode.difB--;
            }
            dist = Math.max(metaNode.index, metaNode.level) - metaNode.hits;
            if (dist >= result.maxDist) {
                continue;
            }

            //if (level>d-hits)
            dist3 = Math.max(metaNode.difA, metaNode.difB);
            if (dist3 >= result.maxDist) {
                continue;
            }

            /*
            int dist2 = (query.permut.length - metaNode.index) - (metaNode.node.deepestLevel - metaNode.level);

            //int xxx = (query.permut.length()-index) - (node.deepestLevel - level);
            int xxx = (metaNode.node.minLevelAhead) - (query.permut.length - metaNode.index);
            if (xxx > 0) {
                int dif_ = metaNode.difB + xxx;
                //int dist4 = Math.max(difA, difB_);
                if (dif_ > result.maxDist) {
                    continue;
                }
            }

            xxx = (query.permut.length - metaNode.index) - (metaNode.node.deepestLevel - metaNode.level);
            if (xxx > 0) {
                int dif_ = metaNode.difA + xxx;
                //int dist4 = Math.max(difA, difB_);
                if (dif_ >= result.maxDist) {
                    continue;
                }
            }

            if (metaNode.level > metaNode.index) {
                dist2 = dist2 - (metaNode.level - metaNode.index);
            }

            if (dist + dist2 >= result.maxDist) {
                System.out.println("out 1");
                continue;
            }

            dist2 = (metaNode.node.minLevelAhead) - (query.permut.length - metaNode.index);

            if (metaNode.index > metaNode.level) {
                dist2 = dist2 - (metaNode.index - metaNode.level);
            }

            if (dist + dist2 >=result.maxDist) {
                //NUM_ESCAPED_NODES++;
                //System.out.println("----------------- escaped");
                System.out.println("out 2");
                continue;
            }
             */
            addResults(metaNode, metaNode.node, metaNode.difB, metaNode.hits, query, result);

            for (int i = 0; i < metaNode.node.numberOfChildren; i++) {
                TrieNode node = metaNode.node.children[i];
                QueueNode nodeX = new QueueNode();
                //nodeX.text = metaNode.text + TrieWord.getChar(node.c);
                nodeX.node = node;
                nodeX.difA = metaNode.difA;
                nodeX.difB = metaNode.difB;
                nodeX.hits = metaNode.hits;
                nodeX.index = metaNode.index;
                nodeX.level = metaNode.level + 1;
                nodes.add(nodeX);
                //searchB(node, query, d, list, hits, difA, difB, index, level + 1);
            }

        }
    }



    private void searchB(TrieNode node, TrieWord query, Result result, int hits, int difA, int difB, int index, int level) {

//        if (node == null) {
//            return;
//        }

        //NUM_ACCESSED_NODES++;
        

        difB++;
        while (index < query.permut.length) {

            if (node.c > query.permut[index]) {
                difA++;
            } else {
                if (node.c == query.permut[index]) {
                    difB--;
                    index++;
                    hits++;
                }
                
                break;
            }
            index++;
        }


//        int dist = Math.max(index, level) - hits;
//        if (dist > result.maxDist) {
//            return;
//        }

        //if (level>d-hits)
        int dist3 = Math.max(difA, difB);
        if (dist3 > result.maxDist) {
            return;
        }

        /*
        
        int dist2 = (query.permut.length-index) - (node.deepestLevel - level);
        
        //int xxx = (query.permut.length()-index) - (node.deepestLevel - level);
        int xxx = (node.minLevelAhead) - (query.permut.length-index);
        if (xxx>0){
            int dif_ = difB + xxx;
            //int dist4 = Math.max(difA, difB_);
            if (dif_>result.maxDist) return;
        }

        xxx = (query.permut.length-index) - (node.deepestLevel - level);
        if (xxx>0){
            int dif_ = difA + xxx;
            //int dist4 = Math.max(difA, difB_);
            if (dif_>result.maxDist) return;
        }
        
        if (level>index)
            dist2 = dist2 - (level-index);
        
        if (dist+dist2>result.maxDist){ 
            //NUM_ESCAPED_NODES++;
            return;
        }
        
        dist2 = (node.minLevelAhead) - (query.permut.length-index);
        
        if (index>level)
            dist2 = dist2 - (index-level);
        
        if (dist+dist2>result.maxDist){ 
            //NUM_ESCAPED_NODES++;
            //System.out.println("----------------- escaped");
            return;
        }
         */
        addResults(node, difB, hits, query, result);

        
        if (difA + 1 > result.maxDist && index < query.permut.length) {
            int end = findMatch(node, query, index);
           for (int i = 0; i < end; i++) {
                searchB1(node.children[i], query, result, hits, difA, difB, index, level + 1);
            }
            return;
        }
        for (int i = 0; i < node.numberOfChildren; i++) {
            searchB(node.children[i], query, result, hits, difA, difB, index, level + 1);
        }

    }
    
    private void searchB1(TrieNode node, TrieWord query, Result result, int hits, int difA, int difB, int index, int level) {


        if (node.c == query.permut[index]) {
            index++;
        } else {
            difB++;
            if (difB > result.maxDist) {
                return;
            }
        }

        addResults(node, difB, hits, query, result);

        if (difA + 1 > result.maxDist && index < query.permut.length) {
            int end = findMatch(node, query, index);
           for (int i = 0; i < end; i++) {
                searchB1(node.children[i], query, result, hits, difA, difB, index, level + 1);
            }
            return;
        }
        for (int i = 0; i < node.numberOfChildren; i++) {
            searchB(node.children[i], query, result, hits, difA, difB, index, level + 1);
        }

    }
    
    private int findMatch2(TrieNode node, TrieWord query, int index) {
        for (int i = 0; i < node.numberOfChildren; i++) {
            TrieNode child = node.children[i];
            if (child.c > query.permut[index]) {
                return i;
            }
        }
        return node.numberOfChildren;
    }
    
    static int findMatch(TrieNode node, TrieWord query, int index) {
        int left = 0;
        int right = node.numberOfChildren - 1;
        int result = node.numberOfChildren; // Initialize result to -1

        byte target = query.permut[index];
        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (node.children[mid].c <= target) {
                
                left = mid + 1; // Search in the right half
            } else {
                result = mid; // Update result to the current index
                right = mid - 1; // Search in the left half
            }
        }
        

        return result;
    }

    public class QueueNode implements Comparable<QueueNode> {

        int hits;
        int difA;
        int difB;
        int index;
        int level;
        TrieNode node;
        //String text = "";

        @Override
        public int compareTo(QueueNode o) {
            //int res = (hits-index) - (o.hits-o.index);
            int max1 = Math.max(index, level);
            int max2 = Math.max(o.index, o.level);
            int res = (max1 - hits) - (max2 - o.hits);
            if (res != 0) {
                return res;
            }
            res = (o.index) - (index);
            //res = (max1) - (max2);
            return res;
        }

        public String toString() {
            return "hits " + hits + " index " + index+" level "+level;
        }

    }

    private void addResults(TrieNode node, int difB, int hits, TrieWord query, int d, ArrayList<String> list) {
        if (node.word != null) {
            int difB_ = difB;

            if (query.permut.length > node.word.permut.length) {
                difB_ = difB + (query.permut.length - node.word.permut.length);

            }
            int dist = Math.max(query.permut.length, node.word.permut.length) - hits;
            if (dist <= d && difB_ <= d) 
            {
            String q = query.words.get(0);
                for (int i = 0; i < node.word.words.size(); i++) {
                	String word = node.word.words.get(i);
                    //int d1 = lev.wordDistance(q, word);
                    //int d1 = lev.ukkonen(q, word, d+1);
                    int d1 = dam.distanceWithEarlyStop(q, word, d);
                    //int d1 = Levenshtein.distance(q, word);
                    //if (d1 <= d) {
                    if (d1 != -1) {
                        list.add(word);
                        //System.out.println(node.word.words.get(i));
                    }
                }

            }
        }
    }

    private void addResults(TrieNode node, int difB, int hits, TrieWord query, Result result) {
        if (node.word != null) {
            int difB_ = difB;

            if (query.permut.length > node.word.permut.length) {
                difB_ = difB + (query.permut.length - node.word.permut.length);

            }
            //int dist = Math.max(query.permut.length, node.word.permut.length) - hits;
            
            //if (dist <= result.maxDist && difB_ <= result.maxDist) 
            if (difB_ <= result.maxDist) 
            {
            	String q = query.words.get(0);
                for (int i = 0; i < node.word.words.size(); i++) {
                	String word = node.word.words.get(i);
                    //int d1 = lev.wordDistance(q, word);
                    //int d1 = lev.ukkonen(q, word, d+1);
                    int d1 = EditDistance.distance(q, word);
                    if (d1 <= result.maxDist) {
                        result.addWord(word, d1);
                        //System.out.println(node.word.words.get(i));
                    }
                }

            }
        }
    }

    private void addResults(QueueNode qn, TrieNode node, int difB, int hits, TrieWord query, Result result) {
        if (node.word != null) {
            int difB_ = difB;

            if (query.permut.length > node.word.permut.length) {
                difB_ = difB + (query.permut.length - node.word.permut.length);

            }
            int dist = Math.max(query.permut.length, node.word.permut.length) - hits;

            if (dist < result.maxDist && difB_ < result.maxDist) {

                for (int i = 0; i < node.word.words.size(); i++) {
                    //int d1 = lev.wordDistance(query.words.get(0), node.word.words.get(i));
                    //int d1 = lev.ukkonen(query.words.get(0), node.word.words.get(i), result.maxDist+1);

                    //if (dist >= result.maxDist || difB_ >= result.maxDist) break;
                    int d1 = EditDistance.distance(query.words.get(0), node.word.words.get(i));
                    if (d1 < result.maxDist) {
//                        if (debug){
//                        System.out.println("max dist "+d1+" word "+node.word.words.get(i));
//                        System.out.println("path is => "+qn.text);
//                        }
                        result.addWord(node.word.words.get(i), d1);
                        //System.out.println(node.word.words.get(i));
                    }
                }

            }
        }
    }

    public void print() {
        System.out.println("printing");
        print(root);
    }

    private void print(TrieNode node) {

        if (node == null) {
            return;
        }

        System.out.println("char " + node.c + "level ahead " + node.minLevelAhead);

        if (node.word != null) {
            System.out.println(node.word);
            for (int i = 0; i < node.word.words.size(); i++) {
                //        System.out.println("- "+node.word.words.get(i));
            }
        }

        for (int i = 0; i < node.numberOfChildren; i++) {
            print(node.children[i]);
        }

    }

    public void setMinimum() {
        setMinimum(root);
    }

    private void setMinimum(TrieNode node) {

        if (node == null) {
            return;
        }

        for (int i = 0; i < node.numberOfChildren; i++) {
            setMinimum(node.children[i]);
        }

        if (node.word == null) {

            int min = Integer.MAX_VALUE;
            for (int i = 0; i < node.numberOfChildren; i++) {

                if (node.children[i].minLevelAhead < min) {
                    min = node.children[i].minLevelAhead;
                }
            }
            node.minLevelAhead = min + 1;

        }
    }

    private String getChar(int index) {
        switch (index) {
            case 0:
                return "a";
            case 1:
                return "b";
            case 2:
                return "c";

        }
        return "-";
    }

    private ArrayList<TrieWord> createTrieWords() {

        ArrayList<TrieWord> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    String aux = new String();
                    aux = aux + getChar(i);
                    aux = aux + getChar(j);
                    aux = aux + getChar(k);
                    TrieWord tw = new TrieWord(aux,prefixSize);
                    list.add(tw);
                }
            }
        }
        return list;
    }

    public static void main(String[] args) {
        int prefixSize = 10;
        ATrie_ trie = new ATrie_(prefixSize);

//        ArrayList<TrieWord> list = new ArrayList<TrieWord>();
//        list.add(new TrieWord("aaa",prefixSize));
//        list.add(new TrieWord("aabc",prefixSize));
//        list.add(new TrieWord("bcdef",prefixSize));
//        list.add(new TrieWord("bce",prefixSize));
//        list.add(new TrieWord("abdefgh",prefixSize));
//        //list.add(new TrieWord("bdf"));
//
//        //ArrayList<TrieWord> list = trie.createTrieWords();
//        Collections.sort(list);
//
//        for (int i = 0; i < list.size(); i++) {
//            System.out.println(list.get(i));
//            System.out.println("adding word" + list.get(i));
//            trie.addWord(list.get(i));
//        }
//
//        List<String> result = trie.search("bdx", 2);
//        System.out.println("-- result");
//        for (int i = 0; i < result.size(); i++) {
//            System.out.println(result.get(i));
//        }
//
//        trie.print();
int[] arr = {1, 3, 5, 7, 9, 11, 13};
        int target = 13;

        int resultIndex = findLargestSmallerElement(arr, target);

        if (resultIndex != arr.length) {
            System.out.println("The largest element smaller than " + target + " is " + arr[resultIndex]);
        } else {
            System.out.println("No element in the array is smaller than " + target);
        }
    }

     static int findLargestSmallerElement(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;
        int result = arr.length; // Initialize result to -1

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (arr[mid] < target) {
                result = mid; // Update result to the current index
                left = mid + 1; // Search in the right half
            } else {
                right = mid - 1; // Search in the left half
            }
        }
        
        if (result+1<arr.length ){
            if (arr[result+1]!=target)
                result = arr.length;
        }

        return result;
    }

    
}
