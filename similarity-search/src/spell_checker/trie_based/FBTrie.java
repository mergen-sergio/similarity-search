/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spell_checker.trie_based;

import query.result.RangeQueryResult;
import query.result.Result;
import query.result.TopKResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import metric.UKonnen;
import query.result.Word;
import spell_checker.SpellChecking;

import java.util.Stack;

/**
 *
 * @author Sergio
 */
public class FBTrie implements SpellChecking {

    public TrieNode root1;
    public TrieNode root2;
    //Levensthein lev = new Levensthein();
    UKonnen lev = new UKonnen();

    public static int NUM_VISITED_NODES = 0;

    public static int NUM_NODES = 0;
    public static int NUM_NODES_WITH_WORDS = 0;
    public static int NUM_ESCAPED_NODES = 0;
    public static int NUM_ACCESSED_NODES = 0;
    public int prefixSize;

    int maxDist = 999;

    @Override
    public String getName() {
        return "FB-Trie";
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

        private String getPath(TrieNode node) {
            String path = "";
            TrieNode parent = node;
            while (parent != null) {
                path = parent.c + path;
                parent = parent.parent;
            }
            return path;

        }

        public void addWord(String word) {
            if (words == null) {
                words = new ArrayList();
            }
            words.add(word);
        }
    }

    public FBTrie(int prefixSize, int maxDist) {
        this.prefixSize = prefixSize;
        root1 = new TrieNode();
        //root.c = '?';
        root1.type = 0;
        root2 = new TrieNode();
        //root.c = '?';
        root2.type = 0;
        this.maxDist = maxDist;
    }

    @Override
    public void addWords(List<String> words) {

        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            String prefix = word;
            if (prefix.length() > prefixSize) {
                prefix.substring(0, prefixSize);
            }
            addWord(root1, root1.child, word, prefix, 0);
            String reversed = new StringBuilder(word).reverse().toString();
            prefix = reversed;
            if (prefix.length() > prefixSize) {
                prefix.substring(0, prefixSize);
            }
            addWord(root2, root2.child, reversed, prefix, 0);
        }
        //setMinimum();

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
            if (node.words != null) {
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
            newNode.level = parent.level + 1;
            parent.child = newNode;
            newNode.parent = parent;

            updateDeepestLevel(newNode, index);

            if (prefix.length() <= index + 1) {
                newNode.addWord(word);
                NUM_NODES_WITH_WORDS++;
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
                    if (sib.words==null || sib.words.isEmpty())
                        NUM_NODES_WITH_WORDS++;
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
        newNode.level = parent.level + 1;;
        last.sibling = newNode;
        newNode.parent = parent;

        updateDeepestLevel(newNode, index);

        if (prefix.length() <= index + 1) {
        
            newNode.addWord(word);
            NUM_NODES_WITH_WORDS++;
            updateMinLevel(newNode);
        } else {
            addWord(newNode, newNode.child, word, prefix, index + 1);
        }
    }

    @Override
    public List<String> search(String query, int k) {

        int n = query.length();
        String q1 = query.substring(0, n / 2);
        String q2 = query.substring(n / 2);

        int k1 = (k - 1) / 2;
        //System.err.println("F " + k1);
        RangeQueryResult result = new RangeQueryResult(k);
        rangeSearch(root1, query, result, k1, q1);

        int k2 = k / 2;
        //System.err.println("B " + k2);
        String sbQuery = new StringBuilder(query).reverse().toString();
        String sbQ2 = new StringBuilder(q2).reverse().toString();
        RangeQueryResult bresult = new RangeQueryResult(k);
        rangeSearch(root2, sbQuery, bresult, k2, sbQ2);

        for (Word word : bresult.toWordArray()) {
            String reversedWord = new StringBuilder(word.word).reverse().toString();
            result.addWord(reversedWord, word.dist);
        }

        if (1==0){
        ArrayList<String> list = new ArrayList();
        Set<String> uniqueResults = new TreeSet<>(result.toArray());
        for (String uniqueResult : uniqueResults) {
            list.add(uniqueResult);
        }
        return list;
        }
        // 1. First, use a Map to keep only unique words 
        // (e.g., keeping the one with the smallest dist if duplicates exist)
        Map<String, Word> uniqueWordMap = new HashMap<>();

        for (Word w : result.toWordArray()) {
            // If the word isn't in the map yet, add it. 
            // If it is already there, you can choose whether to keep the existing one or update it.
            uniqueWordMap.putIfAbsent(w.word, w);

            // OR: If you want to keep the one with the minimum distance:
            // uniqueWordMap.merge(w.word, w, (existing, incoming) -> existing.dist < incoming.dist ? existing : incoming);
        }

        // 2. Put the unique Word objects into a List
        List<Word> uniqueResults = new ArrayList<>(uniqueWordMap.values());

        // 3. Sort the list by 'dist' (using your compareTo method which compares dist)
        Collections.sort(uniqueResults);

        // 4. Extract just the strings into a List<String>
        List<String> list = new ArrayList<>();
        for (Word w : uniqueResults) {
            list.add(w.word);
        }

        return list;

    }

    private void rangeSearch(TrieNode root, String word, Result result, int subk, String subq) {
        NUM_VISITED_NODES++;
        int iWordLength = word.length();
        int[] currentRow = new int[iWordLength + 1];

        for (int i = 0; i <= iWordLength; i++) {
            currentRow[i] = i;
        }

        TrieNode node = root.child;
        while (node != null) {
            rangeSearch(node, result, subk, subq, word, currentRow, 1);
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
    private void rangeSearch(TrieNode node, Result result, int subk, String subq, String query, int[] previousRow, int phase) {
        NUM_VISITED_NODES++;
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

        // CORREÇÃO: Na fase 1, usamos findMinimum simples para evitar prunings 
        // agressivos causados pela diferença de tamanho da query inteira.
        int minimumElement;
        if (phase == 1) {
            minimumElement = findMinimum(currentRow);
        } else {
            minimumElement = findMinimum1(currentRow, (node.deepestLevel - node.level), node.minLevelAhead);
        }

        int activeLimit = (phase == 1) ? subk : result.maxDist;
        if (minimumElement > activeLimit) {
            return;
        }

        node = node.child;
        while (node != null) {
            if (phase == 1 && subq.length() < currentRow.length && currentRow[subq.length()] <= subk) {
                rangeSearch(node, result, result.maxDist, subq, query, currentRow, 2);
                node = node.sibling;
                continue;
            }
            rangeSearch(node, result, subk, subq, query, currentRow, phase);
            node = node.sibling;
        }

    }

    private int minimum(int val1, int val2, int val3) {
        int min = Math.min(val1, val2);
        return Math.min(min, val3);

    }

    public static int findMinimum1(int[] arr, int maxWordLen, int minWordLen) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Array is empty or null.");
        }

        int min = arr[0];

        int diff1 = arr.length - (0 + 1) - maxWordLen; // Initialize min with the first element
        int diff2 = minWordLen - arr.length - (0 + 1);
        diff1 = (diff1 > diff2) ? diff1 : diff2;
        if (diff1 > 0) {
            min = min + diff1;
        }

        // Iterate through the array
        for (int i = 1; i < arr.length; i++) {
            int min_ = arr[i];
            diff1 = arr.length - (i + 1) - maxWordLen;
            diff2 = minWordLen - arr.length - (i + 1);
            diff1 = (diff1 > diff2) ? diff1 : diff2;
            if (diff1 > 0) {
                min_ = min_ + diff1;
            }
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

    //@Override
    public List<String> topKSearch(String query, int topK) {
        List<String> resultsX = new ArrayList<>();
        for (int i = 1; i < maxDist + 1; i++) {
            List<String> results = search(query, i);
            results.removeAll(resultsX);
            for (int j = 0; j < results.size(); j++) {
                resultsX.add(results.get(j));
                if (resultsX.size() == topK) {
                    return resultsX;
                }
            }
        }
        //System.out.println("chegou aqui!!");
        return resultsX;
    }

    //@Override
    public List<String> topKSearch1(String query, int topK) {
        int n = query.length();
        String q1 = query.substring(0, n / 2);
        String q2 = query.substring(n / 2);

        TopKResult result = new TopKResult(topK);
        result.maxDist = maxDist;
        topkSearch1(root1, query, q1, result, false);
        //System.err.println("F " + k1);

        //System.err.println("B " + k2);
        String sbQuery = new StringBuilder(query).reverse().toString();
        String sbQ2 = new StringBuilder(q2).reverse().toString();

        TopKResult bresult = new TopKResult(topK);
        bresult.maxDist = maxDist;
        topkSearch1(root2, sbQuery, sbQ2, bresult, true);

        Set<String> uniqueResults = new TreeSet<>(result.toArray());

        for (Word word : bresult.toWordArray()) {
            String reversedWord = new StringBuilder(word.word).reverse().toString();
            if (!(uniqueResults.contains(reversedWord))) {
                result.addWord(reversedWord, word.dist);
            }
        }

        ArrayList<String> list = new ArrayList();
        for (String uniqueResult : uniqueResults) {
            list.add(uniqueResult);
        }

        return list;
    }

    public void topkSearch1(TrieNode root, String q, String subq, TopKResult result, boolean reverse) {
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
            node.reverse = reverse;
            node.totalDist = false;
            node.phase = 1;
            node.subq = subq;
            nodes.add(node);
            trieNode = trieNode.sibling;
        }

        //while (!nodes.isEmpty()) {
        while (true) {

            QueueNode metaNode = nodes.poll();
            if (metaNode == null) {
                break;
            }

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
            // CORREÇÃO: Na fase 1, usamos findMinimum simples para evitar prunings 
            // agressivos causados pela diferença de tamanho da query inteira.
            int minimumElement;
            if (metaNode.phase == 1) {
                minimumElement = findMinimum(currentRow);
            } else {
                minimumElement = findMinimum1(currentRow, (metaNode.node.deepestLevel - metaNode.node.level), metaNode.node.minLevelAhead);
            }
            //minimumElement = findMinimum(currentRow);

            int activeLimit = (metaNode.phase == 1) ? metaNode.subK(result) : result.maxDist;
            if (minimumElement > activeLimit) {
                continue;
            }

            TrieNode node = metaNode.node.child;
            while (node != null) {
                QueueNode nodeX = new QueueNode();
                //nodeX.text = metaNode.text+TrieWord.getChar(node.c);
                nodeX.node = node;
                nodeX.previousRow = currentRow;
                nodeX.minD = minimumElement;

                if (metaNode.phase == 1 && metaNode.subq.length() < currentRow.length && currentRow[metaNode.subq.length()] <= metaNode.subK(result)) {
                    nodeX.totalDist = true;
                    nodeX.subq = metaNode.subq;
                    nodeX.phase = 2;
                    nodeX.reverse = reverse;

                } else {
                    nodeX.totalDist = false;
                    nodeX.subq = metaNode.subq;
                    nodeX.phase = metaNode.phase;
                    nodeX.reverse = reverse;
                }

                nodes.add(nodeX);
                //searchB(node, query, d, list, hits, difA, difB, index, level + 1);
                node = node.sibling;
            }

        }
    }

    private class DistanceQueue {

        int maxD = 50;
        Stack<QueueNode> stacks[] = new Stack[maxD];
        int currentD = 0;

        public DistanceQueue() {
            for (int i = 0; i < maxD; i++) {
                stacks[i] = new Stack();
            }
        }

        public void add(QueueNode node) {
            //System.out.println("adding "+d+" "+node.node.prefix);
            stacks[node.minD].push(node);
        }

        public QueueNode poll() {

            int i = currentD;
            for (; i < maxD; i++) {
                if (stacks[i].empty()) {
                    continue;
                }
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
        boolean totalDist = true;
        boolean reverse;
        String subq;
        int phase;

        public int subK(TopKResult result) {
            if (totalDist) {
                return result.maxDist;
            }

            if (reverse) {
                return (result.maxDist) / 2;
            } else {
                return (result.maxDist - 1) / 2;
            }
        }

        @Override
        public int compareTo(QueueNode o) {
            return minD - (o.minD);
        }

    }

    public void print() {
        System.out.println("printing");
        print(root1, 0);
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
        setMinimum(root1);
        setMinimum(root2);
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

    public static void main(String[] args) {
        FBTrie fb = new FBTrie(999, 3);
        List<String> words = new ArrayList();
        words.add("ba");
        //words.add("carro");
        fb.addWords(words);

        List<String> results = fb.search("b", 1);
        for (String result : results) {
            System.out.println(result);
        }

    }
}
