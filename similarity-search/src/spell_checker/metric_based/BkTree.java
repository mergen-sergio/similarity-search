/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package spell_checker.metric_based;



import java.util.ArrayList;
import object.Point;
import object.StringPoint;

/**
 *
 * @author pccli
 * @param <T>
 */
public class BkTree implements MetricDistanceSearchTree {

    
    private Node root;
    public static int count = 0;

    public static int cur_level = 0;
    public static int max_level = 0;
    
    @Override
    public String getStrategy() {
        return "BkTree";
    }
    
    public void addNode(Point word)
    {
        cur_level = 0;
        if (root==null){
            Node node = new Node(word);
            root = node;
        }
        else root.addNode(word);
        
        if (cur_level>max_level)
            max_level = cur_level;
    }
    
    @Override
    public ArrayList<Point> search(Point point, double distance) {
        ArrayList<Point> points = new ArrayList<Point>();
        search(point, distance, points);
        return points;
    }
    
    private void search(Point point, double d, ArrayList<Point> points){
    
        
        count = 0;
        if (root!=null){
            root.search(point, d, points);
        }
        
	
}

    @Override
    public void addNodes(ArrayList list) {
        for (int x=0;x<list.size();x++){
            addNode((Point)list.get(x));
        }
    }

    



    

    

class Node
{
	Point point;
	Node[] children;


	public Node(Point point)
	{
		this.point = point;
                children = new Node[50];
	}

	public void addNode(Point point)
	{
                cur_level++;
		//word = word.toLowerCase();

		int dist = (int)this.point.distance(point);
		if (children[dist]==null){
                    Node node = new Node(point);
                    children[dist] = node;
                }
                else children[dist].addNode(point);
	}
        
        private void search(Point point, double d, ArrayList<Point> points )
	{
		int curDist = (int)this.point.distance(point);
                BkTree.count++;
		int minDist = curDist - (int)d;
                if (minDist<0)
                    minDist = 0;
		int maxDist = curDist + (int)d;
                
                if (curDist<=d)
                    points.add(this.point);
                    
                for (int x=minDist;x<=maxDist;x++){
                    if (children[x]!=null){
                        children[x].search(point, d, points);
                    }
                }

                
	}
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MetricDistanceSearchTree<StringPoint> tree = new BkTree();
        tree.addNode(new StringPoint("aaaaa"));
        tree.addNode(new StringPoint("aaaab"));
        tree.addNode(new StringPoint("bbbaa"));
        tree.addNode(new StringPoint("ccccc"));
        tree.addNode(new StringPoint("babab"));
        tree.addNode(new StringPoint("aabab"));
        tree.addNode(new StringPoint("baaaa"));
        
        ArrayList<StringPoint> result = tree.search(new StringPoint("aaaaa"), 0);
        for (int x=0;x<result.size();x++){
            System.out.println(result.get(x));
        }
        
    }
    
}
