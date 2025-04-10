/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trie.wrappers;

import trie.SpellChecking;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;

/**
 *
 * @author Sergio david spencer
 */
public class LuceneAutomatonWrapper implements SpellChecking {

    IndexSearcher searcher = null;
    
    public LuceneAutomatonWrapper() {
    }

    @Override
    public String getName() {
        return "Luc-Aut";
    }

    @Override
    public void addWords(List<String> words) {
        Directory directory;
        try {
            directory = new RAMDirectory(); // In-memory index
             //directory = MMapDirectory.open(Paths.get("Index_Automaton"));
            //directory = FSDirectory.open(Paths.get("Index_Automaton"));

            // Use an n-gram tokenizer (index substrings of words)
//            Map<String, String> tokenizerArgs = new HashMap<>();
//            tokenizerArgs.put("minGramSize", "1");
//            tokenizerArgs.put("maxGramSize", "30");

//            Analyzer analyzer = CustomAnalyzer.builder()
//                    .withTokenizer(EdgeNGramTokenizerFactory.class, tokenizerArgs)
//                    .build();
            
            Analyzer analyzer = new StandardAnalyzer();

            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE); // Recreate index
            IndexWriter writer = new IndexWriter(directory, config);

            for (String word : words) {
                Document doc = new Document();
                doc.add(new TextField("word", word, Field.Store.YES)); // Store the word
                writer.addDocument(doc);
            }
            writer.close();
            System.out.println("Index created successfully!");
            DirectoryReader reader = DirectoryReader.open(directory);
            searcher = new IndexSearcher(reader);
        } catch (IOException ex) {
            Logger.getLogger(LuceneAutomatonWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<String> search(String query, int maxD) {
        try {

            
            FuzzyQuery termQuery = new FuzzyQuery(new Term("word", query), maxD);
            TopDocs topDocs = searcher.search(termQuery, Integer.MAX_VALUE);
            String result[] = new String[topDocs.scoreDocs.length];
            int i = 0;
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                result[i] = doc.get("word");
                i++;
            }
            return Arrays.stream(result).collect(
                    Collectors.toList());
        } catch (IOException ex) {
        }

        return new ArrayList<>();

    }

    
    public List<String> topKSearch1(String query, int topK) {
        try {

            FuzzyQuery termQuery = new FuzzyQuery(new Term("word", query));
            TopDocs topDocs = searcher.search(termQuery, topK);
            String result[] = new String[topDocs.scoreDocs.length];
            int i = 0;
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                result[i] = doc.get("word");
                i++;
            }
            return Arrays.stream(result).collect(
                    Collectors.toList());
        } catch (IOException ex) {
        }

        return new ArrayList<>();
    }
    
    @Override
    public List<String> topKSearch(String query, int topK) {
        try {

            FuzzyQuery termQuery = new FuzzyQuery(new Term("word", query));
            TopDocs topDocs = searcher.search(termQuery, topK);
            List<String> result = new ArrayList();
            int i = 0;
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                result.add(doc.get("word"));
            }
            return result;
        } catch (IOException ex) {
        }

        return new ArrayList<>();
    }

}
