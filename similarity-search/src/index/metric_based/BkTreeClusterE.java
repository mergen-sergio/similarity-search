/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spell_checker.metric_based;

import distance.Levenshtein;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import trie.SpellChecking;
import query.result.RangeQueryResult;
import query.result.Result;
import query.result.TopKResult;

/**
 *
 * @author pccli
 */
public class BkTreeClusterE implements SpellChecking {

    private Node root;

    public static int ARRAY_SIZE = 50;

    private int MAX_LEAF_SIZE = 3;


    public int clusterSizeAcum = 0;
    public int numberOfClusters = 0;
    public int numberOfNodes = 0;
    public int maxLevel = 0;
    public int maxArity = 0;
    
    int maxDist = 99;

    public BkTreeClusterE(int maxLeaf, int maxDist) {
        MAX_LEAF_SIZE = maxLeaf;
        this.maxDist = maxDist;
    }
    
    @Override
    public void addWords(List<String> words) {
        for (int x = 0; x < words.size(); x++) {
            addNode((String) words.get(x));
        }
    }

    @Override
    public List<String> search(String query, int maxD) {
        RangeQueryResult result = new RangeQueryResult(maxD);
        search1(query, result);
        return result.toArray();
    }

    @Override
    public List<String> topKSearch(String query, int topK) {
        TopKResult result = new TopKResult(topK);
        result.maxDist = maxDist;
        search2(query, result);
        return result.toArray();
    }

    @Override
    public String getName() {
        return "BK-Tree";
    }

    public void addNode(String point) {

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

    

    public void search1(String point, Result result) {

        ArrayList<Node> toSearch = new ArrayList<Node>();

        if (root != null) {
            toSearch.add(root);
        }

        for (int x = 0; x < toSearch.size(); x++) {
            Node n = toSearch.get(x);
            n.search(point, result, toSearch);
        }

    }

    private void search2(String point, Result result) {

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
            int d = Levenshtein.distance(metaNode.point, point);
            //double d = metaNode.point.distance(point);
            result.addWord(metaNode.point, d);

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

        String point;
        ArrayList<String> points = new ArrayList<>();
        Node[] children;
        int maxL = -1;
        boolean opened = false;

        int maxDist = Integer.MAX_VALUE;
        int arity = 0;
        int level = 0;

        public double d;
        public int rank;

        public Node(String point) {
            this.point = point;
            children = new Node[ARRAY_SIZE];
        }

        public void addNode(String point) {
            if (!opened) {
                points.add(point);

                if (points.size() >= MAX_LEAF_SIZE) {
                    for (int x = points.size() - 1; x >= 0; x--) {
                        String p = points.remove(x);
                        addNode2(p);
                    }
                    opened = true;
                }

            } else {
                addNode2(point);
            }
        }

        public void addNode2(String point) {

            int dist2 = Levenshtein.distance(this.point, point);
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

        private void search(String point, Result result, ArrayList<Node> toSearch) {

            if (!points.isEmpty()) {
                clusterSearch(point, result);
                return;
            }

            int curDist = Levenshtein.distance(this.point, point);
            //int curDist = (int) this.point.distance(point);
            result.addWord(this.point, curDist);

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

        private void clusterSearch(String point, Result result) {
            {
                int curDistX = Levenshtein.distance(this.point,  point);
                result.addWord(this.point, curDistX);
            }

            for (int i = 0; i < points.size(); i++) {
                String p = (String) points.get(i);

                int dAQO = Levenshtein.distance(p, point);
                if (dAQO <= result.maxDist) {
                    result.addWord(p, dAQO);

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
