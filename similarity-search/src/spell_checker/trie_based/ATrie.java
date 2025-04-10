/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spell_checker.trie_based;

import io.gitlab.rxp90.jsymspell.api.DamerauLevenshteinOSA;
import query.result.RangeQueryResult;
import query.result.Result;
import query.result.TopKResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import metric.Levensthein;
import metric.UKonnen;
import spell_checker.SpellChecking;

/**
 *
 * @author Sergio
 */
public class ATrie implements SpellChecking {

    public TrieNode root;
    static Levensthein lev = new Levensthein();
    static UKonnen ukonnen = new UKonnen();
    static DamerauLevenshteinOSA dam = new DamerauLevenshteinOSA();
    
    int maxEditDist = 2;


    public static int NUM_NODES = 0;
    public static int NUM_ESCAPED_NODES = 0;
    public static int NUM_ACCESSED_NODES = 0;

    int prefixSize;

    public class TrieNode {

        public TrieWord word;
        public byte c;
        public int type;
        public TrieNode sibling;
        public TrieNode child;
        int deepestLevel = 0;
        int minLevelAhead = 500;
        String prefix = "";
        int level = -1;
        public TrieNode parent;

        public String getPath() {
            String path = "";
            TrieNode parent = this;
            while (parent != null) {
                path += TrieWord.getChar(parent.c);
                parent = parent.parent;
            }
            return path;
        }

        @Override
        public String toString() {
            if (word != null) {
                return word.toString();
            } else {
                return String.valueOf(c);
            }
        }

        public void addNode(TrieNode node) {
            node.parent = this;
        }
    }

    public ATrie(int prefixSize, int maxEditDist) {
        this.prefixSize = prefixSize;
        root = new TrieNode();
        //root.c = '?';
        root.type = 0;
        this.maxEditDist = maxEditDist;
    }

    @Override
    public String getName() {
        return "A-Trie";
    }

    @Override
    public void addWords(List<String> words) {
        ArrayList<TrieWord> list = new ArrayList<TrieWord>();

        for (int i = 0; i < words.size(); i++) {
            TrieWord tw = new TrieWord(words.get(i), prefixSize);
            list.add(tw);
        }
        //Collections.sort(list);

        for (int i = 0; i < list.size(); i++) {
            addWord(list.get(i));
        }
        //setMinimum();

    }

    public void addWord(TrieWord word) {
        addWord(root, root.child, word, 0);
    }

    private void updateDeepestLevel(TrieNode node, int level) {
        while (node!=null) {
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
            if (node.word != null) {
                level = 0;
            }
            level++;

            node = node.parent;
        }
    }

    private void addWord(TrieNode parent, TrieNode node, TrieWord word, int index) {

        if (node == null) {
            TrieNode newNode = new TrieNode();
            NUM_NODES++;

            newNode.c = word.permut[index];

            parent.child = newNode;
            newNode.parent = parent;
            newNode.level = parent.level + 1;
            newNode.prefix = parent.prefix + (char) (newNode.c & 0xFF);

            updateDeepestLevel(newNode, index);

            if (word.permut.length <= index + 1) {
                newNode.word = new TrieWord(word.words.get(0), prefixSize);
                updateMinLevel(newNode);
                return;
            } else {
                addWord(newNode, newNode.child, word, index + 1);
                return;
            }
        }

        TrieNode last = null;
        TrieNode sib = node;
        while (sib != null) {
            if (sib.c == word.permut[index]) {

                if (word.permut.length <= index + 1) {
                    if (sib.word == null) {
                        sib.word = new TrieWord(word.words.get(0), prefixSize);
                        updateMinLevel(sib);
                    } else {
                        sib.word.words.add(word.words.get(0));
                    }

                    return;
                }

                addWord(sib, sib.child, word, index + 1);
                return;
            }
            else  if (sib.c > word.permut[index]) {
                break;
            }
            last = sib;
            sib = sib.sibling;
        }

        TrieNode newNode = new TrieNode();
        NUM_NODES++;

        newNode.c = word.permut[index];
        newNode.prefix = parent.prefix + (char) (newNode.c & 0xFF);
        if (last != null) {
            last.sibling = newNode;
        } else {
            parent.child = newNode;
        }
        newNode.sibling = sib;
        parent.addNode(newNode);
        newNode.level = parent.level + 1;

        updateDeepestLevel(newNode, index);

        if (word.permut.length <= index + 1) {
            newNode.word = new TrieWord(word.words.get(0), prefixSize);
            updateMinLevel(newNode);
        } else {

            addWord(newNode, newNode.child, word, index + 1);

        }
    }

    @Override
    public List<String> search(String q, int d) {
        //System.out.println("printing");
//        TopKResult result = new TopKResult(3);
//        //RangeQueryResult result = new RangeQueryResult(d + 1);
//        result.maxDist = 3;
        //topkSearch(q, result);

        RangeQueryResult results = new RangeQueryResult(d);
        rangeSearch(q, results);
        return results.toArray();
    }

    @Override
    public List<String> topKSearch(String query, int topK) {
        TopKResult results = new TopKResult(topK);
        results.maxDist = maxEditDist + 1;
        topkSearch(query, results);
        return results.toArray();

    }

    private void rangeSearch(String q, Result result) {
        TrieNode node = root.child;
        TrieWord query = new TrieWord(q, prefixSize);
        while (node!=null){
            rangeSearch(node, query, result, 0, 0, 0);
            node = node.sibling;
        }

    }

    private void topkSearch(String q, Result result) {
        TrieWord query = new TrieWord(q, prefixSize);
        //Queue<QueueNode> nodes = new PriorityQueue<>();
        DistanceQueue nodes = new DistanceQueue();

        QueueNode metaNode1 = new QueueNode();
        metaNode1.node = root;
        metaNode1.difA = 0;
        metaNode1.difB = 0;
        metaNode1.index = 0;

        processChildren(metaNode1, query, result, nodes);

        while (true) {

            QueueNode metaNode = nodes.poll();
            if (metaNode == null) {
                break;
            }
            if (metaNode.maxDist >= result.maxDist) {
                break;
            }

            addResults(metaNode, query, result);

            int xxx = (query.permut.length - metaNode.index) - (metaNode.node.deepestLevel - metaNode.node.level);
            if (xxx + metaNode.difA >= result.maxDist) {
                continue;
            }

            xxx = (metaNode.node.minLevelAhead) - (query.permut.length - metaNode.index);
            if (xxx + metaNode.difB >= result.maxDist) {
                continue;
            }

            processChildren(metaNode, query, result, nodes);

        }
    }

    private QueueNode createNode(QueueNode metaNode, TrieNode node) {
        QueueNode nodeX = new QueueNode();
        //nodeX.text = metaNode.text+TrieWord.getChar(node.c);
        nodeX.node = node;
        nodeX.maxDist = 0;
        nodeX.difB = metaNode.difB;

        return nodeX;
    }

    private void processChildren(QueueNode metaNode, TrieWord query, Result result, DistanceQueue nodes) {

        TrieNode node = metaNode.node.child;
        int index = metaNode.index;
        int difA = metaNode.difA;
        while (node != null) {
            COUNT++;

            QueueNode nodeX = createNode(metaNode, node);

            nodeX.difB++;
            boolean found = false;
            while (index < query.permut.length) {
                if (nodeX.node.c > query.permut[index]) {
                    index++;
                } else {
                    if (nodeX.node.c == query.permut[index]) {
                        index++;
                        found = true;
                    }
                    break;
                }
            }

            nodeX.difA = difA + (index - metaNode.index);
            if (found) {
                nodeX.difB--;
                nodeX.difA--;
            }

            nodeX.maxDist = Math.max(nodeX.difA, nodeX.difB);

            nodeX.index = index;

            nodes.add(nodeX);

            int xxx = (metaNode.node.minLevelAhead) - (query.permut.length - index);
            if (xxx + metaNode.difB >= result.maxDist) {
                break;
            }

            xxx = (query.permut.length - index) - (metaNode.node.deepestLevel - metaNode.node.level);
            if (xxx + difA >= result.maxDist) {
                break;
            }

            if (!found) {
                if (nodeX.difA >= result.maxDist) {
                    break;
                }
            } else {
                if (nodeX.difA + 1 >= result.maxDist) {
                    break;
                }
            }

            //searchB(node, query, d, list, hits, difA, difB, index, level + 1);
            node = node.sibling;
        }
    }

    /**
     * FORMA 1
     */
    private int rangeSearch(TrieNode node, TrieWord query, Result result, int difA, int difB, int index) {

        
        //Trie2.COUNT++;
        difB++;
        boolean found = false;
        int index0 = index;
        while (index < query.permut.length) {

            if (node.c > query.permut[index]) {
                index++;
            } else {

                if (node.c == query.permut[index]) {
                    index++;
                    found = true;
                }

                break;
            }
        }
        difA += (index - index0);
        if (found) {
            difA--;
            difB--;

        }

        if (difA > result.maxDist) {
            return index;
        }

        if (difB > result.maxDist) {
            return index;
        }

        int xxx = (query.permut.length - index) - (node.deepestLevel - node.level);
        if (xxx + difA > result.maxDist) {
            return index;
        }

        addResults(node, difA, difB, query, result);

        xxx = (node.minLevelAhead) - (query.permut.length - index);
        if (xxx + difB > result.maxDist) {
            return index;
        }

        TrieNode child = node.child;

        int index_ = index;
        int index2;
        while (child != null) {
            //COUNT++;
            index2 = rangeSearch(child, query, result, difA, difB, index_);
            if (index2 > index_) {
                difA += (index2 - index_);
                if (difA > result.maxDist) {
                    return index;
                }

                index_ = index2;
                xxx = (query.permut.length - index_) - (node.deepestLevel - node.level);
                if (xxx + difA > result.maxDist) {
                    return index;
                }
                xxx = (node.minLevelAhead) - (query.permut.length - index_);
                if (xxx + difB > result.maxDist) {
                    return index;
                }

            }
            child = child.sibling;

        }
        return index;
    }

    

    private QueueNode createQNode(QueueNode qn1, TrieNode node) {
        QueueNode qn = new QueueNode();
        qn.node = node;
        qn.difA = qn1.difA;
        qn.difB = qn1.difB;
        return qn;
    }

    private void rangeSearchX(String q, Result result) {

        TrieWord query = new TrieWord(q, prefixSize);
        //System.out.println(node.c);
        Stack<QueueNode> stack = new Stack();
        QueueNode rootNode = new QueueNode();
        rootNode.node = root;
        rootNode.difA = 0;
        rootNode.difB = 0;
        rootNode.index = 0;
        stack.add(rootNode);

        while (!stack.empty()) {
            COUNT++;
            QueueNode qn1 = stack.pop();

            if (qn1.maxDist > result.maxDist) {
                return;
            }

            addResults(qn1.node, qn1.difA, qn1.difB, query, result);

            TrieNode node = qn1.node.child;

            int index = qn1.index;
            //System.out.println("down");
            while (node != null) {
                QueueNode qn = createQNode(qn1, node);

                qn.difB++;
                boolean found = false;
                while (index < query.permut.length) {

                    if (qn.node.c > query.permut[index]) {
                        //difA++;
                        index++;
                    } else {
                        if (qn.node.c == query.permut[index]) {
                            //difB--;
                            index++;
                            found = true;
                        }

                        break;
                    }
                    //index++;
                }
                qn.index = index;
                qn.difA += (qn.index - index);
                if (found) {
                    qn.difA--;
                    qn.difB--;

                }

                int dist3 = Math.max(qn.difA, qn.difB);
                if (qn.difA > result.maxDist) {
                    break;
                }
                if (dist3 > result.maxDist) {
                    if (found) {
                        break;
                    }
                } else {
                    stack.add(qn);
                }
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
            stacks[node.maxDist].push(node);
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

        int difA;
        int difB;
        int index;
        TrieNode node;
        int maxDist = 0;
        //String text = "";

        @Override
        public int compareTo(QueueNode o) {
            return maxDist - o.maxDist;
        }

        @Override
        public String toString() {
            return "index " + index + " level " + node.level;
        }

    }

    private static void addResults(TrieNode node, int difA, int difB, TrieWord query, Result result) {
        if (node.word != null) {
            int dif = 0;
            if (query.permut.length > node.word.permut.length) {
                dif = difB + (query.permut.length - node.word.permut.length);
            } else {
                dif = difA + (node.word.permut.length - query.permut.length);
            }
            if (dif <= result.maxDist) {

                String q = query.words.get(0);
                for (int i = 0; i < node.word.words.size(); i++) {

                    String word = node.word.words.get(i);

                    //int d1 = Levenshtein.distance(q, word);
                    //int d1 = lev.wordDistance(q, word);
                    int d1 = ukonnen.ukkonen(q, word, result.maxDist + 1);
                    //int d1 = Levenshtein.distance(q, word);
                    //int d1 = dam.distanceWithEarlyStop(q, word, result.maxDist);
                    //Tester.search_count++;
                    if (d1 <= result.maxDist) 
                    {
                        result.addWord(word, d1);
                    }
                }

            }
        }
    }

    public static int COUNT = 0;

    private static void addResults(QueueNode qn, TrieWord query, Result result) {

        if (qn.node.word != null) {
            int dif = 0;
            if (query.permut.length > qn.node.word.permut.length) {
                dif = qn.difB + (query.permut.length - qn.node.word.permut.length);
            } else {
                dif = qn.difA + (qn.node.word.permut.length - query.permut.length);
            }

            if (dif < result.maxDist) 
            {
                String q = query.words.get(0);
                for (int i = 0; i < qn.node.word.words.size(); i++) {
                    String word = qn.node.word.words.get(i);
                    int d1 = lev.wordDistance(q, qn.node.word.words.get(i));
                    //int d1 = lev.ukkonen(q, qn.node.word.words.get(i), result.maxDist+1);
                    //int d1 = ukkonen.ukkonen(q, word, result.maxDist + 1);
                    //int d1 = dam.distanceWithEarlyStop(q, qn.node.word.words.get(i), result.maxDist);
                    //int d1 = Levenshtein.distance(q, qn.node.word.words.get(i), result.maxDist);
                    if (d1 < result.maxDist) {
                        result.addWord(word, d1);
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

        node = node.child;
        while (node != null) {
            print(node);
            node = node.sibling;
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


    public static void main(String[] args) {
        int prefixSize = 10;
        ATrie trie = new ATrie(prefixSize, 2);

        ArrayList<TrieWord> list = new ArrayList<TrieWord>();
        list.add(new TrieWord("ab", prefixSize));
        list.add(new TrieWord("aa", prefixSize));
        list.add(new TrieWord("ac", prefixSize));
        list.add(new TrieWord("ae", prefixSize));
        list.add(new TrieWord("ad", prefixSize));
        //list.add(new TrieWord("bdf"));

        //ArrayList<TrieWord> list = trie.createTrieWords();
        //Collections.sort(list);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
            System.out.println("adding word" + list.get(i).permut);
            trie.addWord(list.get(i));
        }

        List<String> result = trie.search("bdx", 2);
        System.out.println("-- result");
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i));
        }

        trie.print();
    }

}
