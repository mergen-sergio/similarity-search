/*
 * FastSimilarSearch - A fast similarity search algorithm for large
 * dictionaries Copyright (C) 2006, 2007 University of Zurich, Thomas Bocek
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * ------------------------- FastSimilarSearch.java -------------------------
 * (C) Copyright 2006, 2007 by University of Zurich.
 * 
 * Original Author: Thomas Bocek
 */
package spell_checker.edit_based;
import trie.AnagramString;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import trie.Tester;

/**
 * This class compares several methods searching for similar words. All except
 * one methods are implemented in java. NRgrep is accessed using the
 * Runtime.getRuntime().exec method. This class has the following dependecies:
 * 
 * JFreeChart (http://www.jfree.org/jfreechart/), Batik 1.6
 * (http://xmlgraphics.apache.org/batik/) NR-grep,
 * (http://www.dcc.uchile.cl/~gnavarro/software/)
 * 
 * @author draft
 * 
 */
final public class FastSimilarSearchAnagram
{
	// access chars for generating neighbours. All words consist of lowercase
	// words.
	private final static char[] CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
			'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	// zero means no difference at all, 1 means either one delete, one update or
	// one insert, ...
	private static int levenshteinWordDistance = 0;
	// the block size is used to limit the growth of the precalc set, when the
	// wordlengh is growing
	private static int blockSize = 0;
	// we will look for a similar word to this
	private static String word;
	// size of index
	private static double sizeFastLD = 0;
	private static double sizeFastLD2 = 0;
	private static double sizeFastBlockLD = 0;
	private static double sizeCharecterMapLD = 0;
	private static double sizeNgram = 0;
	// time for precalculation
	private static double timeIndexLD = 0;
	private static double timeIndexFastLD = 0;
	private static double timeIndexFastLD2 = 0;
	private static double timeIndexFastBlockLD = 0;
	private static double timeIndexCharecterMapLD = 0;
	private static double timeIndexNgram = 0;
	private static int minChar = Integer.MAX_VALUE;
	private static int maxChar = Integer.MIN_VALUE;
	// time for lookup
	final static LinkedHashMap<String, LinkedHashMap<Double, List<Double>>> seriesTime = new LinkedHashMap<String, LinkedHashMap<Double, List<Double>>>();
	final static LinkedHashMap<String, LinkedHashMap<Double, List<Double>>> seriesSize = new LinkedHashMap<String, LinkedHashMap<Double, List<Double>>>();
	final static LinkedHashMap<String, LinkedHashMap<Double, List<Double>>> seriesCharSize = new LinkedHashMap<String, LinkedHashMap<Double, List<Double>>>();
	final static LinkedHashMap<String, LinkedHashMap<Double, List<Double>>> indexCreationTime = new LinkedHashMap<String, LinkedHashMap<Double, List<Double>>>();
	// the indexes
	final static Set<String> wordsKey = new HashSet<String>();
	final static CharacterNodeAnagram characterTree = new CharacterNodeAnagram();
	final static Map<String, Set<String>> precalcWord = new HashMap<String, Set<String>>();
	final static Map<String, Set<WordWithDeletePositionsAnagram>> precalcWord2 = new HashMap<String, Set<WordWithDeletePositionsAnagram>>();
	final static Map<String, Set<String>> precalcBlockSplit = new HashMap<String, Set<String>>();
	final static Map<String, Set<String>> precalcWordNgram2 = new HashMap<String, Set<String>>();
	final static Map<String, Set<String>> precalcWordNgram3 = new HashMap<String, Set<String>>();
	final static Map<String, Set<String>> precalcWordNgram4 = new HashMap<String, Set<String>>();
        
        final static Map<String, AnagramString> anagramDic = new HashMap<String, AnagramString>();
        
	// indicate which metdod
	final static int KEYWORDMAP = 0;
	final static int LINEAR = 1;
	final static int NRGREP = 2;
	final static int FASTSS1 = 3;
	final static int FASTSS2 = 4;
	final static int FASTBLOCKSS = 5;
	final static int NEIGHBOURHOOD = 6;
	final static int NGRAM = 7;
	// tmp array to fetch an word when generating a random dictionary
	final static HashSet<String> seen = new HashSet<String>();
	// with this counter we count the levenshtein comparisons
	private int levCounter = 0;
	/**
	 * Default setup
	 */
	static
	{
		setLevenshteinWordDistance(2);
		setBlockSize(4);
	}
	
	
	BufferedReader randomDictionary(int wordlengthMin, int wordlengthMax, int amount)
			throws FileNotFoundException
	{
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
		seen.clear();
		for (int i = 0; i < amount;)
		{
			final StringBuilder sb = new StringBuilder();
			final int rand = (int) (Math.random() * (wordlengthMax - wordlengthMin));
			for (int j = 0; j < rand + wordlengthMin; j++)
			{
				char start = 'a';
				sb.append((char) (start + (char) (Math.random() * 26)));
			}
			if (!seen.contains(sb.toString()))
			{
				seen.add(sb.toString());
				printWriter.println(sb.toString());
				i++;
			}
		}
		printWriter.flush();
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				byteArrayOutputStream.toByteArray());
		final BufferedReader br = new BufferedReader(new InputStreamReader(byteArrayInputStream));
		return br;
	}
	BufferedReader openDictionary(String name) throws FileNotFoundException
	{
		return new BufferedReader(new FileReader(name));
	}
	
	/**
	 * Prepare the data, as we do a lookup of the LD distances, we need to
	 * prepare the data. The data storage for the block LD is not optimal
	 * because of the object oriented languang. The objects creates overhead
	 * 
	 * @param br
	 * @throws IOException
	 */
	public static int loadData(BufferedReader br, int skip, int type) throws IOException
	{
		
		// if (type == LINEAR)
		{
			wordsKey.clear();
		}
		// keep track of the words loaded.
		String line = null;
		long start = System.currentTimeMillis();
		for (int progressCounter = 0; (line = br.readLine()) != null && wordsKey.size() < skip; progressCounter++)
		{
			line = line.trim().toLowerCase();
			if (line.length() > maxChar)
				maxChar = line.length();
			if (line.length() < minChar)
				minChar = line.length();
			loadIndex(line);
			// loads the FastSS lookup table
			if (type == FASTSS1)
				loadFastSS(line);
			// build FastSS lookup table with delete positions.
			else if (type == FASTSS2)
				loadFastSS2(line);
			// build the block FastSS lookup table
			else if (type == FASTBLOCKSS)
				loadFastBlockSS(line);
			
			// now, make the lookup for fast Block LD
			if (progressCounter % 10000 == 0 && progressCounter != 0)
				System.out.print('\n');
			if (progressCounter % 1000 == 0)
				System.out.print('.');
		}
		long stop = System.currentTimeMillis();
                
                System.out.println("precalc words = "+precalcWord.size());
                System.out.println("precalc words 2 = "+precalcWord2.size());
                System.out.println("precalc words = "+precalcBlockSplit.size());
                
		System.out.print("(total words: " + wordsKey.size() + ") - Loading time: " + (stop - start)
				+ " ms");
		
		// sizeNRGrep = tmpFile.length();
		System.out.print(".\n");
		
		return wordsKey.size();
	}
	
	
	/**
	 * Lookuptable for the words. This list grows with n^m, where n is the
	 * wordlength and m is the number of deleted charecters. To be more precise,
	 * it grows with (n comb m) for every ed. For example test, with ed=2 takes
	 * (4 comb 0)+(4 comb 1)+(4 comb 2):
	 * 
	 * A way around this problem is to introduce blocks.
	 */
	private static void loadFastSS(String line)
	{
		long start = System.currentTimeMillis();
                
                String anagram = AnagramString.orderString(line);
                //AnagramString line_ = new AnagramString();
                AnagramString anagramString = anagramDic.get(anagram);
                if (anagramString==null){
                        anagramString = new AnagramString();
                        anagramString.anagram = anagram;
                        anagramDic.put(anagram, anagramString);
                            }
                anagramString.strings.add(line);
                
		List<WordWithDeletePositionsAnagram> normalizedResult = normalize(anagram);
		for (WordWithDeletePositionsAnagram deleteWord : normalizedResult)
		{
			String word = deleteWord.getWord();
			// word.setOriginal(line);
			Set<String> list;
			if (precalcWord.containsKey(word))
				list = precalcWord.get(word);
			else
			{
				list = new HashSet<String>();
				precalcWord.put(word, list);
			}
			list.add(anagram);
		}
		long stop = System.currentTimeMillis();
                
		timeIndexFastLD += stop - start;
	}
	private static void loadFastSS2(String word)
	{
		long start = System.currentTimeMillis();
                
                String anagram = AnagramString.orderString(word);
                //AnagramString line_ = new AnagramString();
                AnagramString anagramString = anagramDic.get(anagram);
                if (anagramString==null){
                        anagramString = new AnagramString();
                        anagramString.anagram = anagram;
                        anagramDic.put(anagram, anagramString);
                            }
                anagramString.strings.add(word);
                
                
                
		List<WordWithDeletePositionsAnagram> normalizedResult = normalize(anagram);
		for (WordWithDeletePositionsAnagram deleteWord : normalizedResult)
		{
			deleteWord.setOriginal(anagram);
			Set<WordWithDeletePositionsAnagram> list;
			if (precalcWord2.containsKey(deleteWord.getWord()))
				list = precalcWord2.get(deleteWord.getWord());
			else
			{
				list = new HashSet<WordWithDeletePositionsAnagram>();
				precalcWord2.put(deleteWord.getWord(), list);
			}
			list.add(deleteWord);
		}
		long stop = System.currentTimeMillis();
		timeIndexFastLD2 += stop - start;
	}
	private static void loadFastBlockSS(String word)
	{
		long start = System.currentTimeMillis();
                
                String anagram = AnagramString.orderString(word);
                //AnagramString line_ = new AnagramString();
                AnagramString anagramString = anagramDic.get(anagram);
                if (anagramString==null){
                        anagramString = new AnagramString();
                        anagramString.anagram = anagram;
                        anagramDic.put(anagram, anagramString);
                            }
                anagramString.strings.add(word);
                
		List<WordWithDeletePositionsAnagram> normalizedResult = normalize(anagram);
		for (WordWithDeletePositionsAnagram deleteWord : normalizedResult)
		{
			deleteWord.setOriginal(anagram);
			List<String> splits = splitWord(deleteWord.getWord());
			int splitIndex = 0;
			for (String split : splits)
			{
				Set<String> set;
				String index = createIndex(split, splitIndex, deleteWord.getWord().length());
				if (precalcBlockSplit.containsKey(index))
					set = precalcBlockSplit.get(index);
				else
				{
					set = new HashSet<String>();
					precalcBlockSplit.put(index, set);
				}
				if (!set.contains(deleteWord.getOriginal()))
					set.add(deleteWord.getOriginal());
				splitIndex++;
			}
		}
		long stop = System.currentTimeMillis();
                
		timeIndexFastBlockLD += stop - start;
	}
	
	private static void loadIndex(String word)
	{
		long start = System.currentTimeMillis();
		wordsKey.add(word);
		long stop = System.currentTimeMillis();
		timeIndexLD += stop - start;
	}
	
	
	static void clearIndexAndMeasurements()
	{
		precalcBlockSplit.clear();
		precalcWord.clear();
		precalcWord2.clear();
		characterTree.clear();
		precalcWordNgram2.clear();
		precalcWordNgram3.clear();
		precalcWordNgram4.clear();
		sizeFastLD = 0;
		sizeFastLD2 = 0;
		sizeFastBlockLD = 0;
		sizeCharecterMapLD = 0;
		sizeNgram = 0;
		timeIndexLD = 0;
		timeIndexFastLD = 0;
		timeIndexFastLD2 = 0;
		timeIndexFastBlockLD = 0;
		timeIndexCharecterMapLD = 0;
		timeIndexNgram = 0;
		minChar = Integer.MAX_VALUE;
		maxChar = Integer.MIN_VALUE;
	}
	static void clearAccumulatedMeasurements()
	{
		seriesTime.clear();
		seriesSize.clear();
		seriesCharSize.clear();
		indexCreationTime.clear();
	}
	
	static String createIndex(String split, int index, int origLength)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(index);
		sb.append("#");
		// faster but uses more space
		// sb.append(origLength);
		// sb.append("_");
		sb.append(split);
		return sb.toString();
	}
	
	
	
	public Set<String> fastLD(String word, String anagram_)
	{
            
            //String anagram_ = AnagramString.orderString(word);
            
		
		final Set<String> resultList = new TreeSet<String>();
		final Set<String> candidateList = xxx(anagram_);
                //System.out.println("candidate list A = "+candidateList.size());
                Tester.search_count+=candidateList.size(); 
		for (String candidate : candidateList){
                    AnagramString anagram = anagramDic.get(candidate);
                //if (AD(anagram.anagram, anagram_) <= getLevenshteinWordDistance())
                    for (String candidate_ : anagram.strings)
		{
                    
			if (LD(candidate_, word) <= getLevenshteinWordDistance())
				resultList.add(candidate_);
			levCounter++;
		}
                }
		return resultList;
	}
        
        
        public Set<String> xxx(String anagram_){
            final Set<String> candidateList = new HashSet<String>();
        final List<WordWithDeletePositionsAnagram> normalizedResult = normalize(anagram_);
		for (WordWithDeletePositionsAnagram deleteWord : normalizedResult)
		{
			if (precalcWord.containsKey(deleteWord.getWord()))
			{
				final Set<String> list = precalcWord.get(deleteWord.getWord());
				for (String original : list)
					candidateList.add(original);
			}
		}
                return candidateList;
        }
	public Set<String> fastLD2(String word)
	{
            String anagram_ = AnagramString.orderString(word);
            
		final Set<String> resultList = new TreeSet<String>();
		final List<WordWithDeletePositionsAnagram> normalizedResult = normalize(anagram_);
		for (WordWithDeletePositionsAnagram deleteWord : normalizedResult)
		{
			if (precalcWord2.containsKey(deleteWord.getWord()))
			{
				final Set<WordWithDeletePositionsAnagram> list = precalcWord2.get(deleteWord.getWord());
				for (WordWithDeletePositionsAnagram deleteWord2 : list)
				{
					if (deleteWord2.getWord().equals("alo")
							&& deleteWord2.getOriginal().equals("aglow"))
					{
						int x = 0;
						x++;
					}
					int dist = getDistanceFormDeletePositions(deleteWord.getPositons(), deleteWord
							.getArrayPositons(), deleteWord2.getPositons(), deleteWord2
							.getArrayPositons());
					if (dist <= getLevenshteinWordDistance())
					{
                                            AnagramString anagram = anagramDic.get(deleteWord2.getOriginal());
                                            for(String candidate: anagram.strings)
                                                if (LD(candidate, word) <= getLevenshteinWordDistance())
                                                    resultList.add(candidate);
						
					}
				}
			}
		}
		return resultList;
	}
	/**
	 * Note that we have 2 ways to do block LD. One way is to iterate over the
	 * candidates, and check for a matching in the original set. The second
	 * method involves a fetch of iterating over possible originals and match
	 * the candidates. It depends on the kind of dictionary which one is faster.
	 * 
	 * @param word The word to loopup
	 */
	public Set<String> fastBlockLD(String word)
	{
            String anagram_ = AnagramString.orderString(word);
            
		final List<WordWithDeletePositionsAnagram> normalizedResult = normalize(anagram_);
		final int min = anagram_.length() - getLevenshteinWordDistance();
		final int max = anagram_.length() + getLevenshteinWordDistance();
		Set<String> candidates = null;
		final Set<String> results = new TreeSet<String>();
		for (WordWithDeletePositionsAnagram deleteWord : normalizedResult)
		{
			final List<String> splits = splitWord(deleteWord.getWord());
			int splitIndex = 0;
			for (String split : splits)
			{
				Set<String> set = precalcBlockSplit.get(createIndex(split, splitIndex, deleteWord
						.getWord().length()));
				if (splitIndex == 0 && set != null)
					candidates = new HashSet<String>(set);
				else if (set != null)
				{
					if (candidates.size() < set.size())
					{
						Iterator<String> i = candidates.iterator();
						while (i.hasNext())
						{
							final String cand = i.next();
							if (!set.contains(cand)
									|| !(cand.length() >= min && cand.length() <= max))
								i.remove();
						}
					}
					else
					{
						Set<String> tmp = new HashSet<String>();
						Iterator<String> i = set.iterator();
						while (i.hasNext())
						{
							String cand = i.next();
							if (candidates.contains(cand)
									&& (cand.length() >= min && cand.length() <= max))
								tmp.add(cand);
						}
						candidates = tmp;
						tmp = null;
					}
				}
				else
				{
					if (candidates != null)
						candidates.clear();
					break;
				}
				splitIndex++;
			}
			if (candidates != null)
				results.addAll(candidates);
		}
		final Iterator<String> i = results.iterator();
                final Set<String> results2 = new TreeSet<String>();
		while (i.hasNext())
		{
			final String candidate = i.next();
                        AnagramString anagram = anagramDic.get(candidate);
                        if (AD(candidate, anagram_) <= getLevenshteinWordDistance())
                        for (String candidate_: anagram.strings){
                            if (LD(candidate_, word) <= getLevenshteinWordDistance())
				results2.add(candidate_);
                            levCounter++;
                        }
		}
		return results2;
	}
	
	
	
	public static List<String> splitWord(String word)
	{
		return splitWord(word, getBlockSize(), getBlockSize());
	}
	private static List<String> splitWord(String word, int blocksize, int distance)
	{
		final List<String> resultList = new ArrayList<String>();
		if (word.equals(""))
		{
			resultList.add("");
			return resultList;
		}
		else
		{
			final int len = word.length();
			int i = 0;
			int until = 0;
			for (; i + blocksize <= len; i += distance)
			{
				resultList.add(word.substring(i, blocksize + i > len ? len : blocksize + i));
				until = blocksize + i > len ? len : blocksize + i;
			}
			if (until < len)
			{
				resultList.add(word.substring(i, len));
			}
			return resultList;
		}
	}
	/**
	 * Performs a union in two sorted arrays. The outcome is the length of the
	 * union. For example [2][2][5] and [2][5] returns 3, [1][2] and [3][4]
	 * returns 4.
	 * 
	 * @param positons1 The first array
	 * @param length1 The length of the first array
	 * @param positons2 The second array
	 * @param length2 The length of the second array
	 * @return The length of a union array
	 */
	public static int getDistanceFormDeletePositions(int positons1[], int length1, int positons2[],
			int length2)
	{
		int updates = 0;
		for (int i = 0, j = 0; i < length1 && j < length2;)
		{
			if (positons1[i] == positons2[j])
			{
				updates++;
				j++;
				i++;
			}
			else if (positons1[i] < positons2[j])
				i++;
			else if (positons1[i] > positons2[j])
				j++;
		}
		return length1 + length2 - updates;
	}
	/**
	 * Plain good old Leveshtein distance. Code taken from
	 * http://en.wikipedia.org/wiki/Levenshtein
	 * 
	 * @param s The string1 to compare
	 * @param t The string2 to compare
	 * @return The Leveshtein distance
	 */
	private static int LD(String s, String t)
	{
		final int n = s.length();
		final int m = t.length();
		if (n == 0)
			return m;
		if (m == 0)
			return n;
		int[][] d = new int[n + 1][m + 1];
		for (int i = 0; i <= n; d[i][0] = i++)
			;
		for (int j = 1; j <= m; d[0][j] = j++)
			;
		for (int i = 1; i <= n; i++)
		{
			char sc = s.charAt(i - 1);
			for (int j = 1; j <= m; j++)
			{
				int v = d[i - 1][j - 1];
				if (t.charAt(j - 1) != sc)
					v++;
				d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), v);
			}
		}
		return d[n][m];
	}
	
        private static int AD(String s1, String s2){
        int s1_dif = 0;
        int s2_dif = 0;
        int i=0, j=0;
        while (i < s1.length() && j<s2.length()) {
            char c1 = s1.charAt(i);
                char c2 = s2.charAt(j);
                if (c1<c2){
                    s1_dif++;
                    i++;
                }
                else if (c2<c1){
                    s2_dif++;
                    j++;
                }
                else{
                    i++;
                    j++;
                }
        }
        while (i < s1.length()){
            i++;
            s1_dif++;
        }
        while (j < s2.length()){
            j++;
            s2_dif++;
        }
            
    return Math.max(s1_dif, s2_dif);
    }
	
	
	// generate deletions
	public static List<WordWithDeletePositionsAnagram> normalize(String word)
	{
		final List<WordWithDeletePositionsAnagram> normalizedWords = new ArrayList<WordWithDeletePositionsAnagram>();
		final WordWithDeletePositionsAnagram deleteWord = new WordWithDeletePositionsAnagram(word);
		normalizedWords.add(deleteWord);
		normalizeRecursive(normalizedWords, deleteWord, getLevenshteinWordDistance(), 0);
		return normalizedWords;
	}
	// recursive generation of deletions
	private static List<WordWithDeletePositionsAnagram> normalizeRecursive(
			List<WordWithDeletePositionsAnagram> normalizedResult, WordWithDeletePositionsAnagram deleteWord,
			int levenshteinWordDistance, int counter)
	{
		final int wordLenght = deleteWord.getWord().length();
		if (levenshteinWordDistance > 0)
		{
			for (int i = counter; i < wordLenght; i++)
			{
				final String nWord = deleteWord.getWord().substring(0, i).concat(
						deleteWord.getWord().substring(i + 1, wordLenght));
				final WordWithDeletePositionsAnagram deleteWord2 = deleteWord.clone();
				deleteWord2.setWord(nWord);
				deleteWord2.addDeletePosition(i);
				// + (FastSimilarSearch.levenshteinWordDistance -
				// levenshteinWordDistance));
				normalizedResult.add(deleteWord2);
				normalizeRecursive(normalizedResult, deleteWord2, levenshteinWordDistance - 1, i);
			}
		}
		return normalizedResult;
	}
	/**
	 * @param levenshteinWordDistance The levenshteinWordDistance to set.
	 */
	public static void setLevenshteinWordDistance(int levenshteinWordDistance)
	{
		FastSimilarSearchAnagram.levenshteinWordDistance = levenshteinWordDistance;
	}
	/**
	 * @return Returns the levenshteinWordDistance.
	 */
	public static int getLevenshteinWordDistance()
	{
		return levenshteinWordDistance;
	}
	/**
	 * @param blockSize The blockSize to set.
	 */
	public static void setBlockSize(int blockSize)
	{
		FastSimilarSearchAnagram.blockSize = blockSize;
	}
	/**
	 * @return Returns the blockSize.
	 */
	public static int getBlockSize()
	{
		return blockSize;
	}
	/**
	 * @param word The word to set.
	 */
	public static void setWord(String word)
	{
		FastSimilarSearchAnagram.word = word;
	}
	/**
	 * @return Returns the word.
	 */
	private static String getWord()
	{
		return word;
	}
}

/**
 * Class that stores information for FastSS considereing deletepositons. The
 * delete postinios are stored in an array
 * 
 * @author draft
 * 
 */
final class WordWithDeletePositionsAnagram implements Comparable, Serializable
{
	private int[] deletePosition;
	private int arrayPos = 0;
	private String original;
	private String word;
	/**
	 * @return Returns the word.
	 */
	public String getWord()
	{
		return word;
	}
	/**
	 * @param original The original to set.
	 */
	public void setOriginal(String original)
	{
		this.original = original;
	}
	/**
	 * @return Returns the original.
	 */
	public String getOriginal()
	{
		return original;
	}
	public WordWithDeletePositionsAnagram(String word)
	{
		setWord(word);
	}
	public void addDeletePosition(int pos)
	{
		deletePosition[arrayPos] = pos;
		arrayPos++;
	}
	// we need tho clone method to generate neighbors
	public WordWithDeletePositionsAnagram clone()
	{
		final WordWithDeletePositionsAnagram clone = new WordWithDeletePositionsAnagram(getWord());
		clone.setOriginal(getOriginal());
		for (int i = 0; i < getArrayPositons(); i++)
			clone.addDeletePosition(deletePosition[i]);
		return clone;
	}
	/**
	 * @param word The word to set.
	 */
	public void setWord(String word)
	{
		this.word = word;
		if (deletePosition == null)
			deletePosition = new int[FastSimilarSearchAnagram.getLevenshteinWordDistance()];
	}
	public int[] getPositons()
	{
		return deletePosition;
	}
	public int getArrayPositons()
	{
		return arrayPos;
	}
	public int compareTo(Object o)
	{
		final WordWithDeletePositionsAnagram wordWithDeletePositions = (WordWithDeletePositionsAnagram) o;
		final int d = wordWithDeletePositions.getWord().compareTo(getWord());
		return d == 0 ? Arrays.equals(wordWithDeletePositions.getPositons(), getPositons()) ? 0 : 1
				: d;
	}
}

/**
 * A character Node. With this class we build a charecter tree that can be
 * searched using dynamic programming. Only interesiting path are evaluated
 * 
 * @author draft
 * 
 */
final class CharacterNodeAnagram implements Serializable
{
	private char character;
	private boolean isWordEnd = false;
	final private Map<Character, CharacterNodeAnagram> childern = new HashMap<Character, CharacterNodeAnagram>();
	/**
	 * The root node has no character
	 * 
	 */
	CharacterNodeAnagram()
	{
		this((char) 0);
	}
	CharacterNodeAnagram addCharacter(char character)
	{
		if (childern.containsKey(character))
			return childern.get(character);
		else
		{
			CharacterNodeAnagram characterNode = new CharacterNodeAnagram(character);
			childern.put(character, characterNode);
			return characterNode;
		}
	}
	/**
	 * Nodes other than root have a charecter
	 * 
	 * @param character
	 */
	CharacterNodeAnagram(char character)
	{
		this.character = character;
	}
	/**
	 * Clear the node and its children
	 * 
	 */
	void clear()
	{
		childern.clear();
	}
	/**
	 * Returns the character represente by this class
	 * 
	 * @return
	 */
	char getCharacter()
	{
		return character;
	}
	/**
	 * Check if this node is a leaf
	 * 
	 * @return True if this node is a leaf
	 */
	boolean hasLink()
	{
		return childern.size() != 0;
	}
	/**
	 * get all children
	 * 
	 * @return all children
	 */
	Collection<CharacterNodeAnagram> getChildren()
	{
		return childern.values();
	}
	/**
	 * Sets a flag that indicates if this node represents an end of a word ->
	 * test and tests are on the same path, but test is also a word
	 * 
	 * @param isWordEnd
	 */
	void setWordEnd(boolean isWordEnd)
	{
		this.isWordEnd = isWordEnd;
	}
	/**
	 * Checks if this charecter represents an end
	 * 
	 * @return True if this character is an end of the word. This does not means
	 *         that there are no childeren anymore
	 */
	boolean isWordEnd()
	{
		return isWordEnd;
	}
}