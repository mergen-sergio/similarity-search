/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spell_checker.other;

import spell_checker.SpellChecking;
import com.swabunga.spell.SpellChecker;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;
import com.swabunga.spell.event.SpellCheckEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ferna
 */
public class JazzyWrapper implements SpellChecking{

    SpellChecker checker;
    
    public JazzyWrapper(){
    }
    
    @Override
    public void addWords(List<String> words) {
        try {
            checker = new SpellChecker(new SpellDictionaryHashMap(words));
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(JazzyWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

  
    
    @Override
    public List<String> search(String query, int maxD) {
       List<String> result = new ArrayList();
        List<Word> words = checker.getSuggestions(query, maxD);
        for (int i = 0; i < words.size(); i++) {
            result.add(words.get(i).getWord());
        }
        return result;

    
    }

    @Override
    public List<String> topKSearch(String query, int topK) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getName() {
        return "Jazzy";
    }

    
    
}
