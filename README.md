# Approximate Dictionary Search Evaluation

This repository contains the code and scripts used to evaluate several approximate dictionary search methods (spell Checkers). The evaluation is based on measuring construction time, memory usage, and query performance (range and top-k queries).

## Implemented Methods

The following approximate search methods are implemented and compared:

- **ATRIE**  
  A trie-based structure using sorted character sequences (anagram representation). It supports efficient range and top-k queries. It can be configured with a maximum edit distance (e.g., 2, 3, 4) or with unlimited distance.

- **LTRIE**  
  Similar to ATRIE but built using the lexicographic representation of words instead of sorted characters. Also supports fast approximate search.

- **BKTREE**  
  A tree-based index where each node contains a word, and children are organized by edit distance to that word. Suited for small to medium-sized dictionaries.

- **VPTREE**  
  A metric tree built from pivot-based partitioning. Supports approximate search by exploring subtrees based on triangle inequality.

- **LUC_AUT**  
  A Levenshtein automaton built using the Lucene library's framework. Suitable for low edit distances.

- **LEV_AUT**  
  A standalone implementation of the Levenshtein automaton, capable of accepting or rejecting words based on a specified maximum distance.

- **SYMSPELL**  
  A popular algorithm that precomputes deletions of dictionary words. Extremely fast for small distances (typically 1 or 2), but has high memory consumption and limited flexibility.

- **NGRAM**  
  An indexing method that uses character n-grams. Supports both bigrams (n=2) and trigrams (n=3). Approximate matches are found based on overlapping n-grams.

## Running the Evaluation

To reproduce the experiments:

1. Ensure you have Java and a compatible IDE (e.g., NetBeans or IntelliJ).
2. Make sure dependencies (e.g., classes from the project and external libraries) are properly set.
3. Set the entry point to `evaluation.Evaluator`.
4. Configure the desired `type` of experiment:
   - `Evaluator.BUILD` for index construction.
   - `Evaluator.RANGE_QUERY` for range queries.
   - `Evaluator.TOPK_QUERY` for top-k queries.
5. Provide input files such as `googlebooks_cleaned.txt` and `queries_large_words.txt` in the project root.
6. Modify parameters such as:
   - `maxEditDist` for setting maximum allowed edit distance.
   - `topk` for top-k search.
   - `includeOneTypo` to optionally add noise to query words.

## Summary of Findings

We focus our analysis on **ATRIE** and **SYMSPELL**, the most promising methods among those evaluated:

- **ATRIE** stands out as a **robust and versatile solution**. It offers:
  - High accuracy in both range and top-k queries.
  - Efficient index size and fast query times.
  - Support for larger edit distances.
  - Consistent performance across different query lengths and noise levels.

- **SYMSPELL**, while **extremely fast** for edit distance 1 or 2, presents notable trade-offs:
  - It requires substantial memory to store precomputed deletions.
  - It lacks flexibility to handle higher distances or custom similarity definitions.
  - Precision drops sharply when querying longer or noisier words.

In summary, **ATRIE delivers a strong balance of efficiency, scalability, and accuracy**. **SYMSPELL** may still be useful when memory is abundant and ultra-low-latency for low-error queries is essential.

<!-- ## Citation

If you use this code in your own work, please consider citing our paper:

> _Title: Anagram-Based Approximate Dictionary Search with Trie Indexing_  
> _Authors: [Redacted for review]_  
> Submitted to: *Computing Journal*, 2025.

--- -->

For questions or clarifications, feel free to open an issue in this repository.
