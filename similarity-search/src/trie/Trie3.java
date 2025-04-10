/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trie;

import trie.result.RangeQueryResult;
import trie.result.Result;
import trie.result.TopKResult;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import wd.UKonnen;

/**
 *
 * @author Sergio
 */
public class Trie3 implements SpellChecking {

    public TrieNode root;
    //Levensthein lev = new Levensthein();
    UKonnen lev = new UKonnen();

    public static int NUM_NODES = 0;
    public static int NUM_ESCAPED_NODES = 0;
    public static int NUM_ACCESSED_NODES = 0;
    public int prefixSize;
    
    int maxDist = 999;

    @Override
    public String getName() {
        return "L-Trie";
    }

    @Override
    public List<String> topKSearch(String query, int topK) {
        TopKResult results = new TopKResult(topK);
        results.maxDist = maxDist;
        topkSearch(query, results);
        return results.toArray();
    }

    public class TrieNode {

        public List<String> words;
        public char c;
        public int type;
        public TrieNode sibling;
        public TrieNode child;
        int deepestLevel = 0;
        int minLevelAhead = 500;
        public TrieNode parent;
        
        int level = -1;

        public String toString() {

            /*if (word!=null)
            return "["+word.toString()+"]"; 
        else return String.valueOf(c);
             */
            return getPath(this);
        }

        public void addWord(String word) {
            if (words == null) {
                words = new ArrayList();
            }
            words.add(word);
        }
    }

    public Trie3(int prefixSize, int maxDist) {
        this.prefixSize = prefixSize;
        root = new TrieNode();
        //root.c = '?';
        root.type = 0;
        this.maxDist = maxDist;
    }

    @Override
    public void addWords(List<String> words) {

        System.out.println("--order ");
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            String prefix = word;
            if (prefix.length() > prefixSize) {
                prefix.substring(0, prefixSize);
            }
            addWord(word, prefix);
        }
        //setMinimum();

    }

    public void addWord(String word, String prefix) {
        addWord(root, root.child, word, prefix, 0);
    }

    private void updateDeepestLevel(TrieNode node, int level) {
        while (node.type != 0) {
            if (level > node.deepestLevel) {
                node.deepestLevel = level;
            }
            node = node.parent;
        }
    }
    
    private void updateMinLevel(TrieNode node) {
        int level = 500;
        while (node != null) {

            if (level < node.minLevelAhead) {
                node.minLevelAhead = level;
            }
            if (node.words!=null) {
                level = 0;
            }
            level++;

            node = node.parent;
        }
    }

    private void addWord(TrieNode parent, TrieNode node, String word, String prefix, int index) {

        if (node == null) {
            TrieNode newNode = new TrieNode();
            NUM_NODES++;
            newNode.type = 1;

            newNode.c = prefix.charAt(index);
            newNode.level = parent.level+1;
            parent.child = newNode;
            newNode.parent = parent;

            updateDeepestLevel(newNode, index);

            if (prefix.length() <= index + 1) {
                newNode.addWord(word);
                updateMinLevel(newNode);
                return;
            } else {

                addWord(newNode, newNode.child, word, prefix, index + 1);
                return;
            }
        }

        TrieNode last = null;
        TrieNode sib = node;
        while (sib != null) {
            if (sib.c == prefix.charAt(index)) {

                if (prefix.length() <= index + 1) {
                    sib.addWord(word);
                    updateMinLevel(sib);
                    return;
                }

                addWord(sib, sib.child, word, prefix, index + 1);
                return;
            }
            //if (node.c > prefix.charAt(index))
            //break;
            last = sib;
            sib = sib.sibling;
        }

        TrieNode newNode = new TrieNode();
        NUM_NODES++;
        newNode.type = 1;

        newNode.c = prefix.charAt(index);
        newNode.level = parent.level+1;;
        last.sibling = newNode;
        newNode.parent = parent;

        updateDeepestLevel(newNode, index);

        if (prefix.length() <= index + 1) {
            newNode.addWord(word);
            updateMinLevel(newNode);
        } else {
            addWord(newNode, newNode.child, word, prefix, index + 1);
        }
    }

    /**
     * Computes the minimum Levenshtein Distance between the given word
     * (represented as an array of Characters) and the words stored in the Trie.
     * This algorithm is modeled after Steve Hanov's blog article "Fast and Easy
     * Levenshtein distance using a Trie" and Murilo Vasconcelo's revised
     * version in C++.
     *
     * http://stevehanov.ca/blog/index.php?id=114
     * http://murilo.wordpress.com/2011/02/01/fast-and-easy-levenshtein-distance-using-a-trie-in-c/
     *
     * @param word - the characters of an input word as an array representation
     * @param d the maximum allowed distance
     * @return the list of strings found
     */
    @Override
    public List<String> search(String word, int d) {

//        TopKResult result = new TopKResult(3);
//        result.maxDist = 4;
//        topkSearch(word, result);

        RangeQueryResult result = new RangeQueryResult(d);
        rangeSearch(word, result);
        
        return result.toArray();

    }

    private void rangeSearch(String word, Result result){
    int iWordLength = word.length();
        int[] currentRow = new int[iWordLength + 1];

        for (int i = 0; i <= iWordLength; i++) {
            currentRow[i] = i;
        }

        TrieNode node = root.child;
        while (node != null) {
            rangeSearch(node, result, word, currentRow);
            node = node.sibling;
        }
    }
    
    /**
     * Recursive helper function. Traverses theTrie in search of the minimum
     * Levenshtein Distance.
     *
     * @param TrieNode node - the current TrieNode
     * @param result - stores the results found
     * @param query - the query string
     * @param int[] previousRow - a row in the Levenshtein Distance matrix
     */
    private void rangeSearch(TrieNode node, Result result, String query, int[] previousRow) {

        int size = previousRow.length;
        int[] currentRow = new int[size];
        currentRow[0] = previousRow[0] + 1;

        
        int insertCost, deleteCost, replaceCost;

        for (int i = 1; i < size; i++) {

            insertCost = currentRow[i - 1] + 1;
            deleteCost = previousRow[i] + 1;

            if (query.charAt(i - 1) == node.c) {
                replaceCost = previousRow[i - 1];
            } else {
                replaceCost = previousRow[i - 1] + 1;
            }

            currentRow[i] = minimum(insertCost, deleteCost, replaceCost);

//            if (currentRow[i] < minimumElement) {
//                minimumElement = currentRow[i];
//            }
        }

        if (currentRow[size - 1] <= result.maxDist && node.words != null) {
            for (String word : node.words) {
                result.addWord(word, currentRow[size - 1]);
            }

        }

        int minimumElement = findMinimum1(currentRow, (node.deepestLevel-node.level), node.minLevelAhead);

        //int minimumElement = findMinimum(currentRow);

        
        if (minimumElement > result.maxDist) {
            return;
        }
        node = node.child;
        while (node != null) {
            rangeSearch(node, result, query, currentRow);
            node = node.sibling;
        }
    }

    public static int findMinimum1(int[] arr, int maxWordLen, int minWordLen) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Array is empty or null.");
        }

        int min = arr[0];
        
        int diff1 = arr.length-(0+1) - maxWordLen; // Initialize min with the first element
        int diff2 = minWordLen - arr.length-(0+1);
        diff1 = (diff1>diff2)?diff1:diff2;
        if (diff1>0)
            min = min + diff1;
        
        // Iterate through the array
        for (int i = 1; i < arr.length; i++) {
            int min_ = arr[i];
            diff1 = arr.length-(i+1) - maxWordLen;
            diff2 = minWordLen - arr.length-(i+1);
            diff1 = (diff1>diff2)?diff1:diff2;
            if (diff1>0)
                min_ = min_ + diff1;
            if (min_ < min) {
                min = min_; // Update min if a smaller element is found
            }
        }

        return min;
    }
    
    public static int findMinimum(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Array is empty or null.");
        }

        int min = arr[0]; // Initialize min with the first element

        // Iterate through the array
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i]; // Update min if a smaller element is found
            }
        }

        return min;
    }

    public void topkSearch(String q, TopKResult result) {
        //Queue<QueueNode> nodes = new PriorityQueue<>();
        DistanceQueue nodes = new DistanceQueue();

        int iWordLength = q.length();
        int[] currentRow = new int[iWordLength + 1];

        for (int i = 0; i <= iWordLength; i++) {
            currentRow[i] = i;
        }

        TrieNode trieNode = root.child;
        while (trieNode != null) {
            QueueNode node = new QueueNode();
            //node.text+=TrieWord.getChar(trieNode.c);
            node.node = trieNode;
            node.previousRow = currentRow;
            nodes.add(node);
            trieNode = trieNode.sibling;
        }

        //while (!nodes.isEmpty()) {
        while (true) {

            QueueNode metaNode = nodes.poll();
            if (metaNode==null) break;

            if (metaNode.minD > result.maxDist) {
                break;
            }

            int size = metaNode.previousRow.length;
            currentRow = new int[size];
            currentRow[0] = metaNode.previousRow[0] + 1;

            
            int insertCost, deleteCost, replaceCost;

            for (int i = 1; i < size; i++) {

                insertCost = currentRow[i - 1] + 1;
                deleteCost = metaNode.previousRow[i] + 1;

                if (q.charAt(i - 1) == metaNode.node.c) {
                    replaceCost = metaNode.previousRow[i - 1];
                } else {
                    replaceCost = metaNode.previousRow[i - 1] + 1;
                }

                currentRow[i] = minimum(insertCost, deleteCost, replaceCost);

//            if (currentRow[i] < minimumElement) {
//                minimumElement = currentRow[i];
//            }
            }

            if (currentRow[size - 1] <= result.maxDist && metaNode.node.words != null) {
                for (String word : metaNode.node.words) {
                    result.addWord(word, currentRow[size - 1]);
                }
            }

            //int minimumElement = findMinimum1(currentRow);
            //int minimumElement = findMinimum(currentRow);
            
            int minimumElement = findMinimum1(currentRow, (metaNode.node.deepestLevel-metaNode.node.level), metaNode.node.minLevelAhead);


            if (minimumElement > result.maxDist) {
                continue;
            }

            TrieNode node = metaNode.node.child;
            while (node != null) {
                QueueNode nodeX = new QueueNode();
                //nodeX.text = metaNode.text+TrieWord.getChar(node.c);
                nodeX.node = node;
                nodeX.previousRow = currentRow;
                nodeX.minD = minimumElement;
                nodes.add(nodeX);
                //searchB(node, query, d, list, hits, difA, difB, index, level + 1);
                node = node.sibling;
            }

        }
    }
    
     private class DistanceQueue{
        int maxD = 50;
        Stack<QueueNode> stacks[] = new Stack[maxD];
        int currentD = 0;
        public DistanceQueue(){
            for (int i = 0; i < maxD; i++) {
                stacks[i] = new Stack();
            }
        }
        
        public void add(QueueNode node){
            //System.out.println("adding "+d+" "+node.node.prefix);
            stacks[node.minD].push(node);
        }
        
        public QueueNode poll(){
            
            int i = currentD;
            for (; i < maxD; i++) {
                if (stacks[i].empty()) continue;
                currentD = i;
                return stacks[i].pop();
                
            }
            
            return null;
            
        }
    
    
    }

    public class QueueNode implements Comparable<QueueNode> {

        TrieNode node;
        int[] previousRow;
        int minD = 0;

        @Override
        public int compareTo(QueueNode o) {
            return  minD - (o.minD);
        }

    }

    private int minimum(int val1, int val2, int val3) {
        int min = Math.min(val1, val2);
        return Math.min(min, val3);

    }

    public void print() {
        System.out.println("printing");
        print(root, 0);
    }

    private void print(TrieNode node, int endent) {

        if (node == null) {
            return;
        }
        for (int i = 0; i < endent; i++) {
            System.out.print(" ");
        }
        System.out.println("char " + node.c + " level ahead " + node.minLevelAhead + " depest level" + node.deepestLevel);

        if (node.words != null) {
            System.out.println("string = " + node.words.toString());
            System.out.println("path = " + getPath(node));
        }

        endent += 3;
        node = node.child;
        while (node != null) {
            print(node, endent);
            node = node.sibling;
        }

    }

    private String getPath(TrieNode node) {
        String path = "";
        TrieNode parent = node;
        while (parent != null) {
            path = parent.c + path;
            parent = parent.parent;
        }
        return path;

    }

    public void setMinimum() {
        setMinimum(root);
    }

    private void setMinimum(TrieNode node) {

        if (node == null) {
            return;
        }

        TrieNode sib = node.child;
        while (sib != null) {
            setMinimum(sib);
            sib = sib.sibling;
        }

        if (node.words == null) {
            if (node.child != null) {
                int min = Integer.MAX_VALUE;
                sib = node.child;
                while (sib != null) {
                    if (sib.minLevelAhead < min) {
                        min = sib.minLevelAhead;
                    }
                    sib = sib.sibling;
                }

                node.minLevelAhead = min + 1;
            }
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
                    TrieWord tw = new TrieWord(aux, prefixSize);
                    list.add(tw);
                }
            }
        }
        return list;
    }

    public static void main(String[] args) {
        Trie3 trie = new Trie3(10, 99);

        ArrayList<String> list = new ArrayList();
        list.add("aaa");
        list.add("aab");
        list.add("bab");
        list.add("bbb");
        list.add("aba");
        list.add("ccccccc");
        list.add("cccccccc");
        //list.add(new TrieWord("bdf"));

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
            System.out.println("adding word " + list.get(i));
            trie.addWord(list.get(i), list.get(i));
        }

        trie.setMinimum();

        List<String> result = trie.search("bbb", 1);
        System.out.println("-- result");
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i));
        }

        trie.print();

    }

}
