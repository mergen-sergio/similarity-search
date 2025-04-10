package metric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Levensthein {
	
    
    public static int distance(String a, String b) {
        //a = a.toLowerCase();
        //b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
    
    public int wordDistance(String s1, String s2) {
	        
                int m[][] = wordDistanceMatrix(s1, s2);
		int distance = m[s1.length()][s2.length()];
		return distance;
	}

	
	public int[][] wordDistanceMatrix(String s1, String s2) {
	        
		int m[][] = new int[s1.length() + 1][s2.length() + 1];
	
		m[0][0] = 0;
		for(int len1 = 1; len1 <= s1.length(); ++len1) {
			m[len1][0] = len1;
		}
		for(int len2 = 1; len2 <= s2.length(); ++len2) {
			m[0][len2] = len2;
		}
	
		for(int len1 = 1; len1 <= s1.length(); ++len1) {
			for(int len2 = 1; len2 <= s2.length(); ++len2) {
				if(s1.charAt(len1-1) == s2.charAt(len2-1)) {
					m[len1][len2] = m[len1-1][len2-1];
				} else {
					m[len1][len2] = 1 + Math.min(Math.min(
							m[len1-1][len2-1],
							m[len1  ][len2-1]),
							m[len1-1][len2  ]
					);
				}
			}
		}
		
                return m;
	}

        public static List<int[]> computeBacktrace(int[][] d) {
        List<int[]> backtrace = new ArrayList<int[]>();
        int i=d.length-1;
        int j=d[0].length-1;
         
        while (true) {
            backtrace.add(new int[] {i, j});
            //if (i==0 || j==0) break;
            if (i==1 && j==1) break;
            {
                if (d[i-1][j] < d[i-1][j-1]) {
                    i--;
                } else if (d[i][j-1] < d[i-1][j-1]) {
                    j--;
                } else {
                    i--;
                    j--;
                }
                if (i<1) i = 1;
                if (j<1) j = 1;
            }
        }
        Collections.reverse(backtrace);
        //System.out.println("trace "+backtrace.size());
        return backtrace;
    }
        
        
       public static List<int[]> computeBacktrace1(int[][] d) {
        List<int[]> backtrace = new ArrayList<int[]>();
        int i=d.length-1;
        int j=d[0].length-1;
         
        while (true) {
            backtrace.add(new int[] {i, j});
            if (i==1 && j==1) break;
            else if (i==1 && j > 1) j--;
            else if (j==1 && i > 1) i--;
            else {
                if (d[i-1][j] < d[i-1][j-1]) {
                    i--;
                } else if (d[i][j-1] < d[i-1][j-1]) {
                    j--;
                } else {
                    i--;
                    j--;
                }
            }
        }
        Collections.reverse(backtrace);
        //System.out.println("trace "+backtrace.size());
        return backtrace;
    }
        
        
        
        
        public Character[] computeEditScript(String s1, String s2, List<int[]> backtrace, int[][] d){
        
            Character codes[] = new Character[backtrace.size()];;
            
           for (int x = backtrace.size()-1;x>=0; x--) {
                int i = backtrace.get(x)[0];
                int j = backtrace.get(x)[1];
                
                if (x==0){
                    if (i==1 && j==1){
                        if (s1.charAt(i-1) == s2.charAt(j-1))
                        codes[x] = s1.charAt(i-1);
                    else codes[x] = '.';
                    }
                    else if (i==0)
                        codes[x] = '+';
                    else codes[x] = '-';
                    
                }
                else {
                int i1 = backtrace.get(x-1)[0];
                int j1 = backtrace.get(x-1)[1];
                //if (d[i][j]==d[i1][j1]){
                if (i==i1+1 && j==j1+1){
                    if (s1.charAt(i-1) == s2.charAt(j-1))
                        codes[x] = s1.charAt(i-1);
                    else codes[x] = '.';
                }
                else {
                    if (i1==i-1)
                        {
                        codes[x] = '-';
                        }
                    else codes[x] = '+';
                
                    
                }
                
            }
           }
            
        return codes;
        }
        
        public Character[] computeEditScript1(String s1, String s2, List<int[]> backtrace){
        
            Character codes[] = new Character[backtrace.size()];;
            
            for (int x = 0; x < backtrace.size(); x++) {
                int i = backtrace.get(x)[0];
                int j = backtrace.get(x)[1];
                
                
                if (s1.charAt(i-1)==s2.charAt(j-1))
                    codes[x] = s1.charAt(i-1);
                else {
                    
                    if (x==backtrace.size()-1){
                    int i_ = backtrace.get(x-1)[0];
                    int j_ = backtrace.get(x-1)[1];
                    
                    if (i==i_+1 && j==j_+1)
                    {
                    codes[x] = '.';
                    }
                    else if (i==i_+1)
                        codes[x] = '-';
                    else codes[x] = '+';
                
                }
                else {
                    int i_ = backtrace.get(x+1)[0];
                    int j_ = backtrace.get(x+1)[1];
                    
                    if (i==i_-1 && j==j_-1)
                    {
                    if (s1.charAt(i-1)==s2.charAt(j-1))
                        codes[x] = s1.charAt(i-1);
                    else codes[x] = '.';
                    }
                    else if (i==i_-1)
                        codes[x] = '-';
                    else codes[x] = '+';
                }
                
                }
                
                
            }
            
            
        return codes;
        }
        
	
}

