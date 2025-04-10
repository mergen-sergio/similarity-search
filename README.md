# Anagram-Based Approximate Dictionary Search

This repository contains the source code and experimental setup for the paper _"Anagram-Based Approximate Dictionary Search with Trie Indexing."_ The project includes implementations of efficient approximate string matching methods and an evaluation framework for comparing query performance and memory usage.

## Requirements

- Java 8 or above
- JDK-compatible IDE (e.g., NetBeans or IntelliJ) or CLI (command line)
- Approximately 8GB RAM for full-scale experiments

## Project Structure

The main evaluation class is:

```java
evaluation.Evaluator
```

This class allows running the different stages of the evaluation pipeline, including:

- Index building
- Range queries
- Top-k queries

You can control the execution mode by changing the `type` field in the `run()` method:

```java
int type = Evaluator.TOPK_QUERY;
```

Options include:

```java
Evaluator.BUILD         // Builds the indexes only
Evaluator.RANGE_QUERY   // Runs range queries
Evaluator.TOPK_QUERY    // Runs top-k nearest neighbor queries
```

## Dataset and Queries

- The input dictionary is expected at:  
  `googlebooks_cleaned.txt`

- Query sets are defined in files like:  
  `queries_large_words.txt`

These files must be placed in the root folder or paths adjusted accordingly.

## Running the Experiments

### Compile

Use an IDE or run:

```bash
javac evaluation/Evaluator.java
```

Make sure dependencies (e.g., classes from `spell_checker`, `object`, etc.) are also compiled.

### Run

Execute the main class:

```bash
java evaluation.Evaluator
```

The output will include timing results and performance summaries for each method under evaluation.

## Notes

- The current setup uses two indexing strategies: `A-Trie` and `L-Trie`.
- You can adjust the number of repetitions, edit distance thresholds, and dataset size within the `Evaluator.java` class.
- The method `LinearSearch` is used as the brute-force baseline.

<!-- ## Citation

If you use this code in your own work, please consider citing our paper:

> _Title: Anagram-Based Approximate Dictionary Search with Trie Indexing_  
> _Authors: [Redacted for review]_  
> Submitted to: *Computing Journal*, 2025.

--- -->

For questions or clarifications, feel free to open an issue in this repository.
