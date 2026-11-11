/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package query.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author pccli
 */
public abstract class Result {

    protected Word first;
    protected Word last;

    public int curSize = 0;
    public int maxDist = Integer.MAX_VALUE / 2;

    public int lenght() {
        return curSize;
    }

    public void clear() {
        first = null;
        last = null;
        curSize = 0;
    }

    public abstract boolean needsOrder();

    public abstract boolean addWord(String word, int dist);

    public List<String> toArray() {
        ArrayList<String> list = new ArrayList<String>();
        Word f = first;
        while (f != null) {
            list.add(f.word);
            f = f.next;
        }
        return list;
    }

    public List<String> toSortedArray() {
        // 1. Collect all Word objects from the linked list
        List<Word> wordObjects = new ArrayList<>();
        Word f = first;
        while (f != null) {
            wordObjects.add(f);
            f = f.next;
        }

        // 2. Sort the Word objects using their natural ordering (Comparable)
        Collections.sort(wordObjects);

        // 3. Extract the sorted strings into the final list
        List<String> result = new ArrayList<>();
        for (Word w : wordObjects) {
            result.add(w.word); // Assuming 'word' is the String field inside the Word class
        }

        return result;
    }

    public List<Word> toWordArray() {
        ArrayList<Word> list = new ArrayList<Word>();
        Word f = first;
        while (f != null) {
            list.add(f);
            f = f.next;
        }
        return list;
    }

    public static boolean equal(ArrayList<String> points1, ArrayList<String> points2) {
        if (points1.size() != points2.size()) {
            return false;
        }

        Collections.sort(points1);
        Collections.sort(points2);
        for (int i = 0; i < points1.size(); i++) {
            if (!(points1.get(i).equals(points2.get(i)))) {
                return false;
            }
        }
        return true;
    }

}
