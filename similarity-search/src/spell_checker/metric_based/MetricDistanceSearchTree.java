/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spell_checker.metric_based;


import java.util.ArrayList;
import object.Point;

/**
 *
 * @author Sergio
 */
public interface MetricDistanceSearchTree<T extends Point<T>> {
    
    public void addNodes(ArrayList<T> list);
    public void addNode(T point);
    public ArrayList<T> search(T point, double distance);
    public String getStrategy();
    
}
