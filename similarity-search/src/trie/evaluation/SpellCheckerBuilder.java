/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trie.evaluation;

import java.util.ArrayList;
import java.util.List;
import trie.BkTreeClusterE;
import trie.Norvig;
import trie.SpellChecking;
import trie.Trie2;
import trie.Trie3;
import trie.VpTreeE;
import trie.fastss.FastSSWrapper;
import trie.wrappers.LevAutomatonWrapper;
import trie.wrappers.LuceneCheckerWrapper;
import trie.wrappers.LuceneAutomatonWrapper;
import trie.wrappers.LuceneNGramAutomatonWrapper;
import trie.wrappers.SymSpellWrapper;

/**
 *
 * @author ferna
 */
public class SpellCheckerBuilder {
    
    public static final int ATRIE = 1;
    public static final int LTRIE = 2;
    public static final int BKTREE = 3;
    public static final int VPTREE = 4;
    public static final int LUCENE_SPELL_CHECKER = 5;
    public static final int LUCENE_AUTOMATON = 6;
    public static final int LEV_AUTOMATON = 7;
    public static final int SYMSPELL = 8;
    public static final int FAST_SS = 9;
    public static final int NORVIG = 10;
    public static final int NGRAM2 = 11;
    public static final int NGRAM3 = 12;
    public static final int ATRIE_2 = 13;
    public static final int ATRIE_3 = 14;
    public static final int ATRIE_4 = 15;
    public static final int ATRIE_UNBOUNDED = 16;
    
    
    
    public static List<SpellChecking> createSpellCheckers(List<Integer> types, int maxEditDist, int prefixLen){
            List<SpellChecking> list = new ArrayList<SpellChecking>();
        for (Integer type : types) {
            SpellChecking spellChecker = createSpellChecking(type, maxEditDist, prefixLen);
            list.add(spellChecker);
        }
        return list;
    }
    
    public static SpellChecking createSpellChecking(int type, int maxEditDist, int prefixLen) {
        switch (type) {
            case ATRIE:
                return new Trie2(prefixLen, maxEditDist);
            case ATRIE_2:
                return new Trie2(prefixLen, 2);
            case ATRIE_3:
                return new Trie2(prefixLen, 3);
            case ATRIE_4:
                return new Trie2(prefixLen, 4);
            case ATRIE_UNBOUNDED:
                return new Trie2(prefixLen, 9999);
            case LTRIE:
                return new Trie3(prefixLen, maxEditDist);
            case BKTREE:
                return new BkTreeClusterE(1, maxEditDist);
            case VPTREE:
                return new VpTreeE(1, maxEditDist);
            case SYMSPELL:
                return new SymSpellWrapper(maxEditDist, prefixLen);
            case LUCENE_AUTOMATON:
                return new LuceneAutomatonWrapper();
            case LUCENE_SPELL_CHECKER:
                return new LuceneCheckerWrapper();
            case NGRAM2:
                return new LuceneNGramAutomatonWrapper(2);
            case NGRAM3:
                return new LuceneNGramAutomatonWrapper(3);
            case LEV_AUTOMATON:
                return new LevAutomatonWrapper(maxEditDist);
            case NORVIG:
                return new Norvig();
            case FAST_SS:
                return new FastSSWrapper(maxEditDist);
        }
        return null;
    }
    
    
}
