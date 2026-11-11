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
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Word other = (Word) obj;
        
        // Considered equal if they have the same String word
        if (this.word == null) {
            return other.word == null;
        }
        return this.word.equals(other.word);
    }

    @Override
    public int hashCode() {
        return word != null ? word.hashCode() : 0;
    }
    }
