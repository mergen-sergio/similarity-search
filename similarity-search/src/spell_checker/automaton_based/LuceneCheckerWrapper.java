/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spell_checker.automaton_based;

import spell_checker.SpellChecking;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spell.DirectSpellChecker;
import org.apache.lucene.search.spell.SuggestMode;
import org.apache.lucene.search.spell.SuggestWord;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author Sergio
 * david spencer
 */
public class LuceneCheckerWrapper implements SpellChecking {

    DirectSpellChecker checker = null;
    IndexReader reader = null;
    
    
    
    public LuceneCheckerWrapper()
    {
    }
      @Override
    public String getName() {
        
        return "LUCENE_CHECKER";
    }
    
    

    public void addWords(List<String> words) {
         try {
//Directory directory = new ByteBuffersDirectory();
Directory directory = FSDirectory.open(Paths.get("Index"));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        
        IndexWriter writer = new IndexWriter(directory, config);

        for (String word : words) {
            Document doc = new Document();
            doc.add(new TextField("word", word, Field.Store.YES));
            writer.addDocument(doc);
        }
        // Commit and close
        writer.commit();
        writer.close();
        
        // 🔹 Open IndexSearcher
        reader = DirectoryReader.open(directory);
        //searcher = new IndexSearcher(reader);

        // 🔹 Initialize DirectSpellChecker
        checker = new DirectSpellChecker();
        checker.setMaxEdits(2); // Allow up to 2 edit distances (Levenshtein)
        checker.setMinPrefix(0); // Require no matching prefix char
        checker.setAccuracy(0.0f); // Adjust similarity threshold
        
        //checker.setDistance(new LevenshteinDistance());
        
        
         } catch (Exception ex) {
            System.out.println("erro");
        }
    }

    @Override
    public List<String> search(String query, int maxD) {
        return topKSearch(query, maxD);
        
    }
    
    

    @Override
    public List<String> topKSearch(String query, int topK) {
       try {
            Term term = new Term("word", query);
            SuggestWord[] result = checker.suggestSimilar(term, topK, reader, SuggestMode.SUGGEST_ALWAYS);
            List result_ = new ArrayList();
            for (SuggestWord sug : result) {
                result_.add(sug.string);
           }
            return result_;
            //String result[] = checker.suggestSimilar(query, topK);
            //return Arrays.stream(result).collect(Collectors.toList());
        } catch (IOException ex) {
        }
        
        return new ArrayList<>();
    }
    
}
