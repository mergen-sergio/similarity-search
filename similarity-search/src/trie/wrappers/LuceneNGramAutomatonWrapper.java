/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trie.wrappers;

import distance.Levenshtein;
import trie.SpellChecking;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.ngram.NGramTokenizerFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 *
 * @author Sergio david spencer
 */
public class LuceneNGramAutomatonWrapper implements SpellChecking {

    IndexSearcher searcher = null;
    int ngram = 2;

    public LuceneNGramAutomatonWrapper(int ngram) {
        this.ngram = ngram;
    }

    @Override
    public String getName() {
        return ngram + "_GRAM";
    }

    public Analyzer createNGramAnalyzer(int minGram, int maxGram) throws Exception {
        Map<String, String> tokenizerArgs = new HashMap<>();
        tokenizerArgs.put("minGramSize", String.valueOf(minGram));
        tokenizerArgs.put("maxGramSize", String.valueOf(maxGram));

        return CustomAnalyzer.builder()
                .withTokenizer(NGramTokenizerFactory.class, tokenizerArgs)
                .build();
    }

    @Override
    public void addWords(List<String> words) {
        Directory  directory;
        try {
            directory = new RAMDirectory(); // In-memory index
            //directory = new MMapDirectory(Paths.get("gram_index"));
            Analyzer analyzer = createNGramAnalyzer(ngram, ngram); // 3-5 grams

            IndexWriterConfig config = new IndexWriterConfig(analyzer);
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
        } catch (Exception ex) {
            Logger.getLogger(LuceneAutomatonWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<String> search(String query, int maxD) {
        List<String> results = topKSearch(query, maxD * 10000);

        List<String> results2 = new ArrayList();
        // Iterate through the candidates
        for (String candidate : results) {
            int editDistance = Levenshtein.distance(query, candidate);

            // Only consider candidates within the max edit distance
            if (editDistance > maxD+1) {
                break;
            }
            else results2.add(candidate);
        }

        return results2;

    }

    class Result {

        String word;
        int editDistance;

        Result(String word, int editDistance) {
            this.word = word;
            this.editDistance = editDistance;
        }
    }

    @Override
    public List<String> topKSearch(String query, int topK) {
        try {

            Set<String> queryNGrams = generateNGrams(query, ngram, ngram); // Extract query n-grams

            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            for (String ngram : queryNGrams) {
                booleanQuery.add(new TermQuery(new Term("word", ngram)), BooleanClause.Occur.SHOULD);
            }

            TopDocs results = searcher.search(booleanQuery.build(), topK);
            String[] result = new String[results.scoreDocs.length];
            int i = 0;
            for (ScoreDoc scoreDoc : results.scoreDocs) {
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

    private static Set<String> generateNGrams(String word, int minGram, int maxGram) {
        Set<String> ngrams = new HashSet<>();
        int len = word.length();
        for (int n = minGram; n <= maxGram; n++) {
            for (int i = 0; i <= len - n; i++) {
                ngrams.add(word.substring(i, i + n));
            }
        }
        return ngrams;
    }

}
