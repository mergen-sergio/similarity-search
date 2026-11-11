/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spell_checker.trie_based;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import spell_checker.SpellChecking;

/**
 *
 * @author ferna
 */
public class ATrie2 implements SpellChecking{

    @Override
    public void addWords(List<String> words) {
        for (String word : words) {
            inserir(word);
        }
    }

    @Override
    public List<String> search(String query, int maxD) {
        return new ArrayList();
    }

    @Override
    public List<String> topKSearch(String query, int topK) {
        return new ArrayList();
    }

    @Override
    public String getName() {
        return "ATrie Compacta";
    }

 // Representa a conexão entre um prefixo e seu antecessor
    static class Link {
        char letraOriginal;
        Node prefixoAnterior;
        int idInsercao;

        Link(char letra, Node anterior, int id) {
            this.letraOriginal = letra;
            this.prefixoAnterior = anterior;
            this.idInsercao = id;
        }
    }

    static class Node {
        // Mapa de filhos para o caminho do anagrama (ordem alfabética)
        Map<Character, Node> filhos = new HashMap<>();
        // Lista de links temporais para reconstrução
        List<Link> links = new ArrayList<>();
    }

    private final Node raiz = new Node();
    private int contadorId = 0;

    public void inserir(String palavra) {
        String original = palavra.toLowerCase();
        int idPalavra = ++contadorId; // ID único para esta inserção
        Node ultimoNoPrefixo = null;

        // Processa cada prefixo: B, BO, BOL, BOLA...
        for (int i = 1; i <= original.length(); i++) {
            String prefixoOriginal = original.substring(0, i);
            char letraAdicionada = original.charAt(i - 1);
            
            // 1. Define o caminho do anagrama (alfabético)
            char[] arr = prefixoOriginal.toCharArray();
            Arrays.sort(arr);
            
            Node noAtual = raiz;
            for (char c : arr) {
                noAtual.filhos.putIfAbsent(c, new Node());
                noAtual = noAtual.filhos.get(c);
            }

            // 2. Cria o link temporal (Y) carimbado com o ID da palavra
            Node anterior = (i == 1) ? raiz : ultimoNoPrefixo;
            noAtual.links.add(new Link(letraAdicionada, anterior, idPalavra));
            
            ultimoNoPrefixo = noAtual;
        }
    }

    public List<String> buscar(String palavraBusca) {
        char[] arr = palavraBusca.toLowerCase().toCharArray();
        Arrays.sort(arr);
        String anagrama = new String(arr);
        
        Node noTerminal = localizarNo(anagrama);
        List<String> resultados = new ArrayList<>();
        
        if (noTerminal != null) {
            // 1. Identifica todos os IDs de palavras que terminam neste anagrama
            Set<Integer> idsValidos = new HashSet<>();
            for (Link l : noTerminal.links) {
                idsValidos.add(l.idInsercao);
            }

            // 2. Para cada ID, reconstrói o caminho único (sem ramificações falsas)
            for (int id : idsValidos) {
                String res = reconstruirParaId(noTerminal, id);
                if (res != null) resultados.add(res);
            }
        }
        return resultados;
    }

    // Reconstrução iterativa por ID (evita StackOverflow e quimeras)
    private String reconstruirParaId(Node terminal, int id) {
        StringBuilder sb = new StringBuilder();
        Node atual = terminal;

        while (atual != raiz) {
            boolean encontrouLink = false;
            for (Link l : atual.links) {
                if (l.idInsercao == id) {
                    sb.append(l.letraOriginal);
                    atual = l.prefixoAnterior;
                    encontrouLink = true;
                    break;
                }
            }
            if (!encontrouLink) return null; // Não deve acontecer se a inserção for íntegra
        }
        return sb.reverse().toString();
    }

    private Node localizarNo(String anagrama) {
        Node atual = raiz;
        for (char c : anagrama.toCharArray()) {
            atual = atual.filhos.get(c);
            if (atual == null) return null;
        }
        return atual;
    }

}