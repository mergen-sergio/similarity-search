/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package query.result;


/**
 *
 * @author pccli
 */
public class Word implements Comparable<Word>{
        public int dist;
        public String word;
        Word next;
        Word prev;
        public Word(String word, int dist){
            this.dist = dist;
            this.word = word;
        }
        
        public String toString(){
            String result = "";
            //if (prev!=null)
                //result +=prev.word+"<-";
            result +=word + "("+dist+")";
            //if (next!=null)
            //result += "->"+next.word;
            return result;
        }

    @Override
    public int compareTo(Word o) {
        return (dist-o.dist);
    }
    }
