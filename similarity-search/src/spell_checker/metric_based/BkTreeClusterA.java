/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spell_checker.metric_based;

import metric.EditDistance;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import metric.AnagramDistance;
import spell_checker.SpellChecking;
import query.result.RangeQueryResult;
import query.result.Result;
import query.result.TopKResult;
import spell_checker.trie_based.TrieWord;

/**
 *
 * @author pccli
 */
public class BkTreeClusterA implements SpellChecking {

    private Node root;

    public static int ARRAY_SIZE = 50;

    private int MAX_LEAF_SIZE = 3;


    public int clusterSizeAcum = 0;
    public int numberOfClusters = 0;
    public int numberOfNodes = 0;
    public int maxLevel = 0;
    public int maxArity = 0;
    
    int maxDist = 99;
    
    public BkTreeClusterA(int maxLeaf, int maxDist) {
        MAX_LEAF_SIZE = maxLeaf;
        this.maxDist = maxDist;
    }
    
    
    @Override
    public void addWords(List<String> words) {
        ArrayList<AnagWord> list = new ArrayList<AnagWord>();

        for (int i = 0; i < words.size(); i++) {
            AnagWord tw = new AnagWord(words.get(i), 100);
            list.add(tw);
        }
        //Collections.sort(list);

        for (int i = 0; i < list.size(); i++) {
            addNode(list.get(i));
        }
        //setMinimum();

    }
    
    

    @Override
    public List<String> search(String query, int maxD) {
        RangeQueryResult result = new RangeQueryResult(maxD);
        AnagWord anagWord = new AnagWord(query, 100);
        search1(anagWord, result);
        return result.toArray();
    }

    //@Override
    public List<String> topKSearch(String query, int topK) {
        TopKResult result = new TopKResult(topK);
        result.maxDist = maxDist;
        AnagWord anagWord = new AnagWord(query, 100);
        search2(anagWord, result);
        return result.toArray();
    }
    
    public List<String> topKSearch1(String query, int topK) {
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


    @Override
    public String getName() {
        return "BK-TreeA";
    }

    public void addNode(AnagWord point) {

        if (root == null) {
            Node node = new Node(point);
            root = node;
            root.level = 0;
        } else {
            root.addNode(point);
        }

    }


    public double getMedianClusterSize() {

        clusterSizeAcum = 0;
        numberOfClusters = 0;
        root.getMedianClusterSize();
        return clusterSizeAcum / numberOfClusters;

    }

    

    public void search1(AnagWord point, Result result) {

        ArrayList<Node> toSearch = new ArrayList<Node>();
        
        
        if (root != null) {
            toSearch.add(root);
        }

        for (int x = 0; x < toSearch.size(); x++) {
            Node n = toSearch.get(x);
            n.search(point, result, toSearch);
        }

    }

    private void search2(AnagWord point, Result result) {

        Queue<Node> nodes = new PriorityQueue<>();
        root.d = 0;
        root.rank = 0;
        nodes.add(root);

        while (!nodes.isEmpty()) {

            Node metaNode = nodes.poll();

            if (!metaNode.points.isEmpty()){
                metaNode.clusterSearch(point, result);
                continue;
            }
            int d = AnagramDistance.getDistance(metaNode.point.anag, point.anag);
            if (d<=result.maxDist){
                int d1 = EditDistance.distance(metaNode.point.word, point.word);
                //double d = metaNode.point.distance(point);
                result.addWord(metaNode.point.word, d1);
            }

            //for (int x = minDist; x <= maxDist; x++) {
            for (int x = 0; x <= metaNode.children.length - 1; x++) {
                if (x < d - result.maxDist || x > d + result.maxDist) {
                    continue;
                }

                Node node = metaNode.children[x];

                if (node == null) {
                    continue;
                }

                //if (d - node.maxInternalDistanceToPivot <= result.maxDist) 
                {
                    node.rank = Math.abs(d - x);
                    node.d = d;
                    nodes.add(node);
                }

            }

        }

    }

    public class Node implements Comparable<Node> {

        AnagWord point;
        ArrayList<AnagWord> points = new ArrayList<>();
        Node[] children;
        int maxL = -1;
        boolean opened = false;

        int maxDist = Integer.MAX_VALUE;
        int arity = 0;
        int level = 0;

        public double d;
        public int rank;

        public Node(AnagWord point) {
            this.point = point;
            children = new Node[ARRAY_SIZE];
        }

        public void addNode(AnagWord point) {
            if (!opened) {
                points.add(point);

                if (points.size() >= MAX_LEAF_SIZE) {
                    for (int x = points.size() - 1; x >= 0; x--) {
                        AnagWord p = points.remove(x);
                        addNode2(p);
                    }
                    opened = true;
                }

            } else {
                addNode2(point);
            }
        }

        public void addNode2(AnagWord point) {

            int dist2 = AnagramDistance.getDistance(this.point.anag, point.anag);
            //int dist2 = EditDistance.distance(this.point.word, point.word);
            //double dist = this.point.distance(point);
            //int dist2 = (int) Math.floor(dist);
            if (dist2 > maxL) {
                maxL = dist2;
            }
            if (children[dist2] == null) {
                Node node = new Node(point);
                node.maxDist = dist2;
                children[dist2] = node;
                arity++;
                maxArity++;
                if (arity == 1) {
                    numberOfNodes++;
                }

                node.level = this.level + 1;
                if (node.level > maxLevel) {
                    maxLevel = node.level;
                }
            } else {
                children[dist2].addNode(point);
            }
        }

        private void search(AnagWord point, Result result, ArrayList<Node> toSearch) {

            if (!points.isEmpty()) {
                clusterSearch(point, result);
                return;
            }

            int curDist = AnagramDistance.getDistance(this.point.anag, point.anag);
            //int curDist = EditDistance.distance(this.point.word, point.word);
            if (curDist<=result.maxDist)
            {
                int curDist_ = EditDistance.distance(this.point.word, point.word);
                result.addWord(this.point.word, curDist_);
            }

            int cur1 = curDist;
            int cur2 = curDist + 1;
            while (cur1 >= curDist - result.maxDist && cur1 >= 0 && cur2 <= curDist + result.maxDist && cur2 <= this.maxL) {

                if (children[cur1] != null) {
                    toSearch.add(children[cur1]);//.search(point, result);
                }
                cur1--;

                if (children[cur2] != null) {
                    toSearch.add(children[cur2]);//.search(point, result);
                }
                cur2++;

            }
            if (cur1 >= curDist - result.maxDist && cur1 >= 0) {
                while (cur1 >= curDist - result.maxDist && cur1 >= 0) {
                    if (children[cur1] != null) {
                        toSearch.add(children[cur1]);//.search(point, result);
                    }
                    cur1--;
                }

            } else if (cur2 <= curDist + result.maxDist && cur2 <= this.maxL) {
                while (cur2 <= curDist + result.maxDist && cur2 <= this.maxL) {
                    if (children[cur2] != null) {
                        toSearch.add(children[cur2]);//.search(point, result);
                    }
                    cur2++;
                }

            }

        }

        private void clusterSearch(AnagWord point, Result result) {
            {
                int curDistX = AnagramDistance.getDistance(this.point.anag,  point.anag);
                if (curDistX<=result.maxDist)
                {
                    int curDistX_ = EditDistance.distance(this.point.word,  point.word);
                    result.addWord(this.point.word, curDistX_);
                }
            }

            for (int i = 0; i < points.size(); i++) {
                AnagWord p = (AnagWord) points.get(i);

                int dAQO = AnagramDistance.getDistance(p.anag, point.anag);
                if (dAQO<=result.maxDist)
                {
                    int dAQO_ = EditDistance.distance(p.word, point.word);
                    if (dAQO_ <= result.maxDist) {
                        result.addWord(p.word, dAQO_);
                    }

                }

            }

        }

        public void getMedianClusterSize() {

            int size = this.points.size();
            if (size > 0) {
                clusterSizeAcum += size;
                numberOfClusters++;
            }
            for (int i = 0; i < children.length; i++) {
                Node n = children[i];
                if (n == null) {
                    continue;
                }
                n.getMedianClusterSize();
            }

        }

        @Override
        public int compareTo(Node o) {

            return Integer.compare(rank, o.rank);

            /*
            int comp = Double.compare(rank, o.rank);
            if (comp != 0) {
                return comp;
            }

            return Double.compare(node.factor, (o.node.factor));
             */
        }

        
    }

}
