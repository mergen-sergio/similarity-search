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
package trie.fastss;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
final public class FastSimilarSearch
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
	final static CharacterNode characterTree = new CharacterNode();
	final static Map<String, Set<String>> precalcWord = new HashMap<String, Set<String>>();
	final static Map<String, Set<WordWithDeletePositions>> precalcWord2 = new HashMap<String, Set<WordWithDeletePositions>>();
	final static Map<String, Set<String>> precalcBlockSplit = new HashMap<String, Set<String>>();
	final static Map<String, Set<String>> precalcWordNgram2 = new HashMap<String, Set<String>>();
	final static Map<String, Set<String>> precalcWordNgram3 = new HashMap<String, Set<String>>();
	final static Map<String, Set<String>> precalcWordNgram4 = new HashMap<String, Set<String>>();
	// indicate which metdod
	final static int KEYWORDMAP = 0;
	final static int LINEAR = 1;
	final static int NRGREP = 2;
	final static int FASTSS1 = 3;
	final static int FASTSS2 = 4;
	public final static int FASTBLOCKSS = 5;
	final static int NEIGHBOURHOOD = 6;
	final static int NGRAM = 7;
        
        public static int type = 0;
        
	// tmp array to fetch an word when generating a random dictionary
	final static HashSet<String> seen = new HashSet<String>();
	// with this counter we count the levenshtein comparisons
	private int levCounter = 0;
	// neigborhood generation stores the neighbors in this array
	private static char[] values;
	private static File tmpFile;
	// count neigbors generated in the neighborhood generation
	private static int wordCounter = 0;
	/**
	 * Default setup
	 */
	static
	{
		setLevenshteinWordDistance(2);
		setBlockSize(4);
	}
	/**
	 * The main method. To perform a lookup: use java FastSimilarSearch test 2.
	 * To perform the graphs, use the MakeGraph class.
	 * 
	 * @param args An array
	 * @throws IOException We load a dictionary from the filesystem
	 */
	public static void main(String[] args) throws IOException
	{
		FastSimilarSearch fastSimilarSearch = null;
		if (args.length == 2)
		{
			fastSimilarSearch = new FastSimilarSearch();
			FastSimilarSearch.setWord(args[0]);
			FastSimilarSearch.setLevenshteinWordDistance(Integer.parseInt(args[1]));
			// do all
			int size = FastSimilarSearch.loadData(fastSimilarSearch.openDictionary("english.0"),
					Integer.MAX_VALUE, LINEAR);
			System.gc();
			fastSimilarSearch.test(size, LINEAR);
			size = FastSimilarSearch.loadData(fastSimilarSearch.openDictionary("english.0"),
					Integer.MAX_VALUE, NEIGHBOURHOOD);
			System.gc();
			fastSimilarSearch.test(size, NEIGHBOURHOOD);
			size = FastSimilarSearch.loadData(fastSimilarSearch.openDictionary("english.0"),
					Integer.MAX_VALUE, KEYWORDMAP);
			fastSimilarSearch.test(size, KEYWORDMAP);
			System.gc();
			size = FastSimilarSearch.loadData(fastSimilarSearch.openDictionary("english.0"),
					Integer.MAX_VALUE, NRGREP);
			System.gc();
			fastSimilarSearch.test(size, NRGREP);
			size = FastSimilarSearch.loadData(fastSimilarSearch.openDictionary("english.0"),
					Integer.MAX_VALUE, FASTSS1);
			System.gc();
			fastSimilarSearch.test(size, FASTSS1);
			size = FastSimilarSearch.loadData(fastSimilarSearch.openDictionary("english.0"),
					Integer.MAX_VALUE, FASTSS2);
			System.gc();
			fastSimilarSearch.test(size, FASTSS2);
			size = FastSimilarSearch.loadData(fastSimilarSearch.openDictionary("english.0"),
					Integer.MAX_VALUE, FASTBLOCKSS);
			System.gc();
			fastSimilarSearch.test(size, FASTBLOCKSS);
			size = FastSimilarSearch.loadData(fastSimilarSearch.openDictionary("english.0"),
					Integer.MAX_VALUE, NGRAM);
			System.gc();
			fastSimilarSearch.test(size, NGRAM);
		}
		else if (args.length == 3)
		{
			/* write a random dictionary */
			writeRandomWords("random.0", Integer.parseInt(args[0]), Integer.parseInt(args[1]),
					Integer.parseInt(args[2]));
		}
		else
			System.out.println("usage: FastSimilaritySearch [search this word] [edit distance]");
	}
	private static void writeRandomWords(String filename, int min, int max, int amount)
			throws IOException
	{
		FastSimilarSearch fastSimilarSearch = new FastSimilarSearch();
		FileWriter fw = new FileWriter(filename);
		BufferedReader br = fastSimilarSearch.randomDictionary(min, max, amount);
		String r = null;
		while ((r = br.readLine()) != null)
			fw.write(r + "\n");
		fw.close();
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
	public BufferedReader openDictionary(String name) throws FileNotFoundException
	{
		return new BufferedReader(new FileReader(name));
	}
	private void printList(String key, Set<String> list, double time, double size, int words,
			double indexTime)
	{
		System.out.print("Result for word ");
		System.out.print(getWord());
		System.out.print(" :");
		for (String s : list)
			System.out.print(" [" + s + ']');
		System.out.print(". Lookup time: " + time + " ms");
		System.out.print(". Index creation time: " + indexTime + " ms");
		System.out.print(". Used ");
		System.out.print(levCounter);
		System.out.print(" Levenshtein Distances");
		System.out.print(". Method: " + key);
		System.out.print(". Space used: " + size);
		System.out.print(". Nr of words: " + words);
		System.out.println(". Char lenght range: " + minChar + " - " + maxChar);
		/* time */
		LinkedHashMap<Double, List<Double>> serie = seriesTime.get(key);
		if (serie == null)
		{
			serie = new LinkedHashMap<Double, List<Double>>();
			seriesTime.put(key, serie);
		}
		List<Double> test = serie.get((double) words);
		if (test == null)
			test = new ArrayList<Double>();
		test.add(time);
		serie.put((double) words, test);
		/* size */
		serie = seriesSize.get(key);
		if (serie == null)
		{
			serie = new LinkedHashMap<Double, List<Double>>();
			seriesSize.put(key, serie);
		}
		test = serie.get((double) words);
		if (test == null)
			test = new ArrayList<Double>();
		// in MB
		test.add(size / (1024 * 1024));
		serie.put((double) words, test);
		/* charsize */
		serie = seriesCharSize.get(key);
		if (serie == null)
		{
			serie = new LinkedHashMap<Double, List<Double>>();
			seriesCharSize.put(key, serie);
		}
		// in MB
		test = serie.get(size / (1024 * 1024));
		if (test == null)
			test = new ArrayList<Double>();
		test.add((double) getWord().length());
		serie.put(size / (1024 * 1024), test);
		/* index time */
		serie = indexCreationTime.get(key);
		if (serie == null)
		{
			serie = new LinkedHashMap<Double, List<Double>>();
			indexCreationTime.put(key, serie);
		}
		test = serie.get((double) words);
		if (test == null)
			test = new ArrayList<Double>();
		test.add(indexTime);
		serie.put((double) words, test);
	}
	/**
	 * Prepare the data, as we do a lookup of the LD distances, we need to
	 * prepare the data. The data storage for the block LD is not optimal
	 * because of the object oriented languang. The objects creates overhead
	 * 
	 * @param br
	 * @throws IOException
	 */
	public static int loadData(BufferedReader br, int skip, int type_) throws IOException
	{
                type = type;
		BufferedOutputStream bos = null;
		if (type == NRGREP)
		{
			if (tmpFile != null)
				tmpFile.delete();
			tmpFile = File.createTempFile("tmp", "dic");
			bos = new BufferedOutputStream(new FileOutputStream(tmpFile));
		}
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
			
                        // build character tree
                        loadIndex(line);
			
			
			// now, make the lookup for fast Block LD
			if (progressCounter % 10000 == 0 && progressCounter != 0)
				System.out.print('\n');
			if (progressCounter % 1000 == 0)
				System.out.print('.');
		}
		long stop = System.currentTimeMillis();
		System.out.print("(total words: " + wordsKey.size() + ") - Loading time: " + (stop - start)
				+ " ms");
                
                System.out.println("precalc words = "+precalcWord.size());
                System.out.println("precalc words 2 = "+precalcWord2.size());
                System.out.println("precalc words = "+precalcBlockSplit.size());
                
                
		// sizeLD = serializeIndex("index", wordsKey);
		sizeFastLD = serializeIndex("fastSS", precalcWord);
		sizeFastLD2 = serializeIndex("fastSS2", precalcWord2);
		sizeFastBlockLD = serializeIndex("fastBlockSS", precalcBlockSplit);
		sizeCharecterMapLD = serializeIndex("characterMap", characterTree);
		sizeNgram = serializeIndex("ngram2", precalcWordNgram2)
				+ serializeIndex("ngram3", precalcWordNgram3)
				+ serializeIndex("ngram4", precalcWordNgram4);
		// sizeNRGrep = tmpFile.length();
		System.out.print(".\n");
		if (type == NRGREP)
		{
			bos.flush();
			bos.close();
		}
		if (type == NEIGHBOURHOOD)
		{
			// this can grow really big, use with care
			values = new char[(int) (Math.pow(maxChar, getLevenshteinWordDistance())
					* Math.pow(26, getLevenshteinWordDistance() + 1) * 3)];
		}
		return wordsKey.size();
	}
        
        /**
	 * Prepare the data, as we do a lookup of the LD distances, we need to
	 * prepare the data. The data storage for the block LD is not optimal
	 * because of the object oriented languang. The objects creates overhead
	 * 
	 * @param br
	 * @throws IOException
	 */
	public static int loadData(List<String> words) throws IOException
	{
                type = type;
		BufferedOutputStream bos = null;
		if (type == NRGREP)
		{
			if (tmpFile != null)
				tmpFile.delete();
			tmpFile = File.createTempFile("tmp", "dic");
			bos = new BufferedOutputStream(new FileOutputStream(tmpFile));
		}
		// if (type == LINEAR)
		{
			wordsKey.clear();
		}
		long start = System.currentTimeMillis();
		for (int progressCounter = 0; progressCounter<words.size();progressCounter++)
		{
			String word = words.get(progressCounter);
			
                        // build character tree
                        loadIndex(word);
			
			
			// now, make the lookup for fast Block LD
			if (progressCounter % 10000 == 0 && progressCounter != 0)
				System.out.print('\n');
			if (progressCounter % 1000 == 0)
				System.out.print('.');
		}
		long stop = System.currentTimeMillis();
		System.out.print("(total words: " + wordsKey.size() + ") - Loading time: " + (stop - start)
				+ " ms");
                
                System.out.println("precalc words = "+precalcWord.size());
                System.out.println("precalc words 2 = "+precalcWord2.size());
                System.out.println("precalc words = "+precalcBlockSplit.size());
                
                
		// sizeLD = serializeIndex("index", wordsKey);
		sizeFastLD = serializeIndex("fastSS", precalcWord);
		sizeFastLD2 = serializeIndex("fastSS2", precalcWord2);
		sizeFastBlockLD = serializeIndex("fastBlockSS", precalcBlockSplit);
		sizeCharecterMapLD = serializeIndex("characterMap", characterTree);
		sizeNgram = serializeIndex("ngram2", precalcWordNgram2)
				+ serializeIndex("ngram3", precalcWordNgram3)
				+ serializeIndex("ngram4", precalcWordNgram4);
		// sizeNRGrep = tmpFile.length();
		System.out.print(".\n");
		if (type == NRGREP)
		{
			bos.flush();
			bos.close();
		}
		if (type == NEIGHBOURHOOD)
		{
			// this can grow really big, use with care
			values = new char[(int) (Math.pow(maxChar, getLevenshteinWordDistance())
					* Math.pow(26, getLevenshteinWordDistance() + 1) * 3)];
		}
		return wordsKey.size();
	}
        
        
	private static void loadNgram(Map<String, Set<String>> pre, String gram, String word)
	{
		Set<String> original = null;
		if (!pre.containsKey(gram))
		{
			original = new HashSet<String>();
			pre.put(gram, original);
		}
		else
			original = pre.get(gram);
		original.add(word);
	}
	private static void loadNgram(int ngram, String word)
	{
		long start = System.currentTimeMillis();
		List<String> ngrams = splitWord(word, ngram, 1);
		int pos = 0;
		for (String gram : ngrams)
		{
			String index = createIndex(gram, pos, 0);
			switch (ngram)
			{
			case 2:
				loadNgram(precalcWordNgram2, index, word);
				break;
			case 3:
				loadNgram(precalcWordNgram3, index, word);
				break;
			case 4:
				loadNgram(precalcWordNgram4, index, word);
				break;
			default:
				throw new RuntimeException("error in index creating ngram");
			}
			pos++;
		}
		long stop = System.currentTimeMillis();
		timeIndexNgram += stop - start;
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
		List<WordWithDeletePositions> normalizedResult = normalize(line);
		for (WordWithDeletePositions deleteWord : normalizedResult)
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
			list.add(line);
		}
		long stop = System.currentTimeMillis();
		timeIndexFastLD += stop - start;
	}
	private static void loadFastSS2(String word)
	{
		long start = System.currentTimeMillis();
		List<WordWithDeletePositions> normalizedResult = normalize(word);
		for (WordWithDeletePositions deleteWord : normalizedResult)
		{
			deleteWord.setOriginal(word);
			Set<WordWithDeletePositions> list;
			if (precalcWord2.containsKey(deleteWord.getWord()))
				list = precalcWord2.get(deleteWord.getWord());
			else
			{
				list = new HashSet<WordWithDeletePositions>();
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
		List<WordWithDeletePositions> normalizedResult = normalize(word);
		for (WordWithDeletePositions deleteWord : normalizedResult)
		{
			deleteWord.setOriginal(word);
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
	private static void loadNRGrepDictionary(BufferedOutputStream bos, String word)
			throws IOException
	{
		bos.write(word.getBytes());
	}
	private static void loadIndex(String word) throws IOException
	{
		long start = System.currentTimeMillis();
		wordsKey.add(word);
		long stop = System.currentTimeMillis();
		timeIndexLD += stop - start;
                
                if (type == KEYWORDMAP)
				loadCharacterTree(word);
			// build index for linear search
			// else if (type == LINEAR)
			// loadIndex(line);
			// loads the FastSS lookup table
			else if (type == FASTSS1)
				loadFastSS(word);
			// build FastSS lookup table with delete positions.
			else if (type == FASTSS2)
				loadFastSS2(word);
			// build the block FastSS lookup table
			else if (type == FASTBLOCKSS)
				loadFastBlockSS(word);
			else if (type == NGRAM)
			{
				loadNgram(2, word);
				loadNgram(3, word);
				loadNgram(4, word);
			}
                
	}
	private static void loadCharacterTree(String word)
	{
		long start = System.currentTimeMillis();
		final int len = word.length();
		CharacterNode linkedCharacter = characterTree;
		for (int i = 0; i < len; i++)
		{
			linkedCharacter = linkedCharacter.addCharacter(word.charAt(i));
			linkedCharacter.setWordEnd(linkedCharacter.isWordEnd() ? true : i == len - 1);
		}
		long stop = System.currentTimeMillis();
		timeIndexCharecterMapLD += stop - start;
	}
	private static int serializeIndex(String name, Object map) throws IOException
	{
                //mergen
                if (1==1) return 1;
		final File file = File.createTempFile(name, "serialized");
		FileOutputStream fis = new FileOutputStream(file);
		BufferedOutputStream bio = new BufferedOutputStream(fis);
		ObjectOutputStream oos = new ObjectOutputStream(bio);
		oos.writeObject(map);
		oos.close();
		bio.close();
		fis.close();
		int length = (int) file.length();
		file.delete();
		return length;
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
	static void printCharacterTree(String print, CharacterNode linkedCharacter)
	{
		if (linkedCharacter == null)
			linkedCharacter = characterTree;
		if (print == null)
			print = "";
		Iterator<CharacterNode> i = linkedCharacter.getChildren().iterator();
		while (i.hasNext())
		{
			CharacterNode linkedCharacter2 = i.next();
			if (!linkedCharacter2.hasLink())
				System.err.println(print + linkedCharacter2.getCharacter());
			else
				printCharacterTree(print + linkedCharacter2.getCharacter(), linkedCharacter2);
		}
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
	public void test(int dicSize, int type)
	{
		Set<String> similar = null;
		long starttime = 0;
		long stoptime = 0;
		// run test
		if (type == NRGREP)
		{
			System.gc();
			starttime = (new Date()).getTime();
			levCounter = 0;
			similar = similarNGrep(getWord(), 1000);
			stoptime = (new Date()).getTime();
			printList("NRGrep", similar, (int) (stoptime - starttime), 0, dicSize, 0);
		}
		// run test2
		else if (type == FASTSS1)
		{
			System.gc();
			starttime = (new Date()).getTime();
			for (int i = 0; i < 1000; i++)
			{
				levCounter = 0;
				similar = fastLD(getWord());
			}
			stoptime = (new Date()).getTime();
			printList("FastSSwC", similar, (int) (stoptime - starttime), sizeFastLD, dicSize,
					timeIndexFastLD);
		}
		// run test3
		else if (type == FASTSS2)
		{
			System.gc();
			levCounter = 0;
			starttime = (new Date()).getTime();
			for (int i = 0; i < 1000; i++)
			{
				similar = fastLD2(getWord());
			}
			stoptime = (new Date()).getTime();
			printList("FastSS", similar, (int) (stoptime - starttime), sizeFastLD2, dicSize,
					timeIndexFastLD2);
		}
		// run test4
		else if (type == FASTBLOCKSS)
		{
			System.gc();
			levCounter = 0;
			starttime = (new Date()).getTime();
			for (int i = 0; i < 1000; i++)
			{
				levCounter = 0;
				fastBlockLD(getWord());
			}
			stoptime = (new Date()).getTime();
			printList("FastBlockSS", similar, (int) (stoptime - starttime), sizeFastBlockLD,
					dicSize, timeIndexFastBlockLD);
		}
		// run test5
		else if (type == NEIGHBOURHOOD)
		{
			System.gc();
			levCounter = 0;
			starttime = (new Date()).getTime();
			for (int i = 0; i < 100; i++)
			{
				levCounter = 0;
				similar = neighbourhoodLD(getWord());
			}
			stoptime = (new Date()).getTime();
			printList("Neighborhood Generation", similar, (int) (stoptime - starttime) * 10, 0,
					dicSize, 0);
		}
		// run test6
		else if (type == LINEAR)
		{
			System.gc();
			levCounter = 0;
			starttime = (new Date()).getTime();
			for (int i = 0; i < 10; i++)
			{
				levCounter = 0;
				similar = similarLD(getWord());
			}
			stoptime = (new Date()).getTime();
			printList("Linear Search", similar, (int) (stoptime - starttime) * 100, 0, dicSize, 0);
		}
		// run test7
		else if (type == KEYWORDMAP)
		{
			System.gc();
			levCounter = 0;
			similar = new TreeSet<String>();
			starttime = (new Date()).getTime();
			for (int i = 0; i < 1000; i++)
			{
				computeLevenshteinDistance(initMatrix(getWord()), getWord(), "", 1, 0,
						characterTree, similar);
			}
			stoptime = (new Date()).getTime();
			printList("Keyword Map", similar, (int) (stoptime - starttime), sizeCharecterMapLD,
					dicSize, timeIndexCharecterMapLD);
		}
		else if (type == NGRAM)
		{
			System.gc();
			levCounter = 0;
			starttime = (new Date()).getTime();
			for (int i = 0; i < 1000; i++)
			{
				levCounter = 0;
				similar = searchNgram(getWord());
			}
			stoptime = (new Date()).getTime();
			printList("N-grams Search", similar, (int) (stoptime - starttime), sizeNgram, dicSize,
					timeIndexNgram);
		}
		clearIndexAndMeasurements();
	}
	private static int minGramsToFind(int ns, int wordlength)
	{
		return wordlength - ns + 1 - (getLevenshteinWordDistance() * ns);
	}
	/*
	 * private static int maxGramsToFind(int ns, int wordlength) { return
	 * wordlength - ns + 1 - getLevenshteinWordDistance(); }
	 */
	private Set<String> searchNgram(String word, int ns, int findGrams)
	{
		HashMap<String, Integer> matches = new HashMap<String, Integer>();
		Set<String> candidateList = new HashSet<String>();
		List<String> ngram = splitWord(word, ns, 1);
		int pos = 0;
		for (String gram : ngram)
		{
			Set<String> cand = new HashSet<String>();
			for (int k = pos - 2 < 0 ? 0 : pos - 2; k <= pos + getLevenshteinWordDistance(); k++)
			{
				String index = createIndex(gram, k, 0);
				//System.err.println("search "+index);
				switch (ns)
				{
				case 4:
					if(precalcWordNgram4.containsKey(index))
						cand.addAll(precalcWordNgram4.get(index));
					break;
				case 3:
					if(precalcWordNgram3.containsKey(index))
						cand.addAll(precalcWordNgram3.get(index));
					break;
				case 2:
					if(precalcWordNgram2.containsKey(index))
						cand.addAll(precalcWordNgram2.get(index));
					break;
				default:
					throw new RuntimeException("error in ngram");
				}
			}
			for (String c : cand)
			{
				int nr = -1;
				if (matches.containsKey(c))
					nr = matches.get(c) + 1;
				else
					nr = 1;
				if (nr >= findGrams)
					candidateList.add(c);
				matches.put(c, nr);
			}
			pos++;
		}
		return candidateList;
	}
	private Set<String> searchNgram(String word)
	{
		Set<String> candidateList = new HashSet<String>();
		final Set<String> resultList = new TreeSet<String>();
		int mingtf = minGramsToFind(4, word.length());
		// int maxgtf = maxGramsToFind(4, word.length());
		if (mingtf > 0)
			candidateList = searchNgram(word, 4, mingtf);
		else
		{
			mingtf = minGramsToFind(3, word.length());
			// maxgtf = maxGramsToFind(3, word.length());
			if (mingtf > 0)
				candidateList = searchNgram(word, 3, mingtf);
			else
			{
				mingtf = minGramsToFind(2, word.length());
				// maxgtf = maxGramsToFind(2, word.length());
				if (mingtf > 0)
					candidateList = searchNgram(word, 2, mingtf);
				else
					candidateList = wordsKey;
			}
		}
		for (String candidate : candidateList)
		{
			if (Math.abs(candidate.length() - word.length()) <= getLevenshteinWordDistance())
			{
				// int counter = candidateList.get(candidate);
				// if (counter < maxgtf)
				// {
				if (LD(candidate, word) <= getLevenshteinWordDistance())
					resultList.add(candidate);
				levCounter++;
				// }
				// else
				// resultList.add(candidate);
			}
		}
		return resultList;
	}
	public Set<String> similarNGrep(String word, int loop)
	{
		final Set<String> resultList = new TreeSet<String>();
		try
		{
			Process p = Runtime.getRuntime().exec(
					"nrgrep/test.sh " + loop + " " + word + " " + tmpFile + " "
							+ getLevenshteinWordDistance());
			InputStreamReader is = new InputStreamReader(p.getInputStream());
			BufferedReader br = new BufferedReader(is);
			InputStreamReader is2 = new InputStreamReader(p.getErrorStream());
			BufferedReader br2 = new BufferedReader(is);
			String line;
			while ((line = br.readLine()) != null)
			{
				resultList.add(line);
			}
			while ((line = br2.readLine()) != null)
			{
				System.err.println(line);
			}
			p.getInputStream().close();
			p.getErrorStream().close();
			p.getOutputStream().close();
			is.close();
			br.close();
			is2.close();
			br2.close();
			p.waitFor();
			p.destroy();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		if (resultList.size() == 0)
		{
			System.err.println("word " + word + " not found");
			System.exit(0);
		}
		return resultList;
	}
	public Set<String> similarLD(String word)
	{
		final Set<String> resultList = new TreeSet<String>();
		for (String candidate : wordsKey)
		{
			if (LD(candidate, word) <= FastSimilarSearch.getLevenshteinWordDistance())
				resultList.add(candidate);
			levCounter++;
		}
		return resultList;
	}
	public Set<String> fastLD(String word)
	{
		final Set<String> resultList = new TreeSet<String>();
                final Set<String> candidateList = xxx(word);
                //System.out.println("candidate list  = "+candidateList.size());
                Tester.search_count+=candidateList.size(); 
		for (String candidate : candidateList)
		{
			if (LD(candidate, word) <= getLevenshteinWordDistance())
				resultList.add(candidate);
			levCounter++;
		}
		return resultList;
	}
        
        public Set<String> xxx(String word){
        final Set<String> candidateList = new HashSet<String>();
		final List<WordWithDeletePositions> normalizedResult = normalize(word);
		for (WordWithDeletePositions deleteWord : normalizedResult)
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
		final Set<String> resultList = new TreeSet<String>();
		final List<WordWithDeletePositions> normalizedResult = normalize(word);
		for (WordWithDeletePositions deleteWord : normalizedResult)
		{
			if (precalcWord2.containsKey(deleteWord.getWord()))
			{
				final Set<WordWithDeletePositions> list = precalcWord2.get(deleteWord.getWord());
				for (WordWithDeletePositions deleteWord2 : list)
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
						resultList.add(deleteWord2.getOriginal());
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
	public List<String> fastBlockLD(String word)
	{
		final List<WordWithDeletePositions> normalizedResult = normalize(word);
		final int min = word.length() - getLevenshteinWordDistance();
		final int max = word.length() + getLevenshteinWordDistance();
		Set<String> candidates = null;
		final List<String> results = new ArrayList<String>();
		for (WordWithDeletePositions deleteWord : normalizedResult)
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
		while (i.hasNext())
		{
			final String candidate = i.next();
			if (LD(candidate, word) > getLevenshteinWordDistance())
				i.remove();
			levCounter++;
		}
		return results;
	}
	public Set<String> neighbourhoodLD(String word)
	{
		int n = generateNeighbours(word, values);
		Set<String> result = new TreeSet<String>();
		wordCounter = 0;
		for (int i = 0, j = 0; i < n; i++)
		{
			if (values[i] == '$' || i == n - 1)
			{
				String neighbour = new String(values, j, i - j);
				if (wordsKey.contains(neighbour))
					result.add(neighbour);
				j = i + 1;
				wordCounter++;
			}
		}
		return result;
	}
	public static int generateNeighbours(String text, char[] values)
	{
		int position = text.length();
		text.concat("$").getChars(0, position + 1, values, 0);
		position = generateNeighbours(0, position, values, getLevenshteinWordDistance(), 0,
				position + 1);
		return position;
	}
	public static int generateNeighbours(final int start, final int length, final char[] values,
			final int distance, final int mutationPosition, int position)
	{
		if (distance > 0)
		{
			for (int i = mutationPosition; i < length; i++)
			{
				position = deleteWord(start, length, i, values, position, distance);
				position = updateWord(start, length, i, values, position, distance);
			}
			for (int i = mutationPosition; i <= length; i++)
			{
				position = insertWord(start, length, i, values, position, distance);
			}
		}
		return position;
	}
	private static int updateWord(final int start, final int length, int pos, final char[] values,
			int position, final int distance)
	{
		for (int i = 0; i < CHARS.length; i++)
		{
			System.arraycopy(values, start, values, position, length);
			values[position + pos] = CHARS[i];
			values[position + length] = '$';
			position = generateNeighbours(position, length, values, distance - 1, pos, position
					+ length + 1);
		}
		return position;
	}
	private static int insertWord(final int start, final int length, int pos, final char[] values,
			int position, final int distance)
	{
		for (int i = 0; i < CHARS.length; i++)
		{
			System.arraycopy(values, start, values, position, pos);
			values[position + pos] = CHARS[i];
			System.arraycopy(values, start + pos, values, position + pos + 1, length - pos);
			values[position + length + 1] = '$';
			position = generateNeighbours(position, length + 1, values, distance - 1, pos, position
					+ length + 2);
		}
		return position;
	}
	private static int deleteWord(final int start, final int length, int pos, final char[] values,
			int position, final int distance)
	{
		System.arraycopy(values, start, values, position, pos);
		System.arraycopy(values, start + pos + 1, values, position + pos, length - pos - 1);
		values[position + length - 1] = '$';
		return generateNeighbours(position, length - 1, values, distance - 1, pos, position
				+ length);
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
	private static int[][] initMatrix(String s)
	{
		int matrix[][] = new int[s.length() + 1][];
		for (int i = 0; i <= s.length(); i++)
		{
			matrix[i] = new int[maxChar + 1];
			matrix[i][0] = i;
		}
		return matrix;
	}
	/**
	 * Keyword map, needs the character tree.
	 * 
	 * @param matrix
	 * @param s
	 * @param test
	 * @param step
	 * @param previous
	 * @param parent
	 * @param result
	 */
	private static void computeLevenshteinDistance(int matrix[][], String s, String test, int step,
			int previous, CharacterNode parent, Set<String> result)
	{
		for (CharacterNode linkedCharacter : parent.getChildren())
		{
			if (step - getLevenshteinWordDistance() <= s.length())
			{
				if (step > previous)
				{
					try
					{
						matrix[0][step] = step;
						test += linkedCharacter.getCharacter();
					}
					catch (Exception e)
					{
						System.err.println("step is " + step + " lev is "
								+ getLevenshteinWordDistance() + " s.len is " + s.length()
								+ " matrix dimension " + matrix.length + ", " + matrix[0].length
								+ " word is " + getWord() + " test=" + test);
					}
				}
				else if (step == previous)
				{
					test = test.substring(0, test.length() - 1) + linkedCharacter.getCharacter();
				}
				for (int i = 1; i < matrix.length; i++)
				{
					for (int j = 1; j <= step; j++)
					{
						matrix[i][j] = Math.min(Math
								.min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1),
								matrix[i - 1][j - 1]
										+ ((s.charAt(i - 1) == test.charAt(j - 1)) ? 0 : 1));
					}
				}
				if (linkedCharacter.isWordEnd()
						&& matrix[matrix.length - 1][step] <= getLevenshteinWordDistance())
				{
					result.add(test);
				}
				if (min(matrix, step) <= getLevenshteinWordDistance())
				{
					if (linkedCharacter.hasLink())
						computeLevenshteinDistance(matrix, s, test, step + 1, step,
								linkedCharacter, result);
				}
				previous = step;
			}
		}
	}
	private static int min(int[][] array, int row)
	{
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < array.length; i++)
			if (array[i][row] < min)
				min = array[i][row];
		return min;
	}
	// for debugging
	private static void printMatrix(int[][] matrix)
	{
		for (int i = 0; i < matrix.length; i++)
		{
			System.err.print("[");
			for (int j = 0; j < matrix[0].length; j++)
			{
				System.err.print(matrix[i][j]);
				System.err.print(",");
			}
			System.err.print("]\n");
		}
	}
	// generate deletions
	public static List<WordWithDeletePositions> normalize(String word)
	{
		final List<WordWithDeletePositions> normalizedWords = new ArrayList<WordWithDeletePositions>();
		final WordWithDeletePositions deleteWord = new WordWithDeletePositions(word);
		normalizedWords.add(deleteWord);
		normalizeRecursive(normalizedWords, deleteWord, getLevenshteinWordDistance(), 0);
		return normalizedWords;
	}
	// recursive generation of deletions
	private static List<WordWithDeletePositions> normalizeRecursive(
			List<WordWithDeletePositions> normalizedResult, WordWithDeletePositions deleteWord,
			int levenshteinWordDistance, int counter)
	{
		final int wordLenght = deleteWord.getWord().length();
		if (levenshteinWordDistance > 0)
		{
			for (int i = counter; i < wordLenght; i++)
			{
				final String nWord = deleteWord.getWord().substring(0, i).concat(
						deleteWord.getWord().substring(i + 1, wordLenght));
				final WordWithDeletePositions deleteWord2 = deleteWord.clone();
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
		FastSimilarSearch.levenshteinWordDistance = levenshteinWordDistance;
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
		FastSimilarSearch.blockSize = blockSize;
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
		FastSimilarSearch.word = word;
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
final class WordWithDeletePositions implements Comparable, Serializable
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
	public WordWithDeletePositions(String word)
	{
		setWord(word);
	}
	public void addDeletePosition(int pos)
	{
		deletePosition[arrayPos] = pos;
		arrayPos++;
	}
	// we need tho clone method to generate neighbors
	public WordWithDeletePositions clone()
	{
		final WordWithDeletePositions clone = new WordWithDeletePositions(getWord());
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
			deletePosition = new int[FastSimilarSearch.getLevenshteinWordDistance()];
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
		final WordWithDeletePositions wordWithDeletePositions = (WordWithDeletePositions) o;
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
final class CharacterNode implements Serializable
{
	private char character;
	private boolean isWordEnd = false;
	final private Map<Character, CharacterNode> childern = new HashMap<Character, CharacterNode>();
	/**
	 * The root node has no character
	 * 
	 */
	CharacterNode()
	{
		this((char) 0);
	}
	CharacterNode addCharacter(char character)
	{
		if (childern.containsKey(character))
			return childern.get(character);
		else
		{
			CharacterNode characterNode = new CharacterNode(character);
			childern.put(character, characterNode);
			return characterNode;
		}
	}
	/**
	 * Nodes other than root have a charecter
	 * 
	 * @param character
	 */
	CharacterNode(char character)
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
	Collection<CharacterNode> getChildren()
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