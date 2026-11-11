/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spell_checker.trie_based;

import evaluation.StringLoader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import metric.EditDistance;
import object.StringPoint;
import spell_checker.SpellChecking;
import spell_checker.ngram_based.LuceneNGramAutomatonWrapper;

public class UltraCompactrie implements SpellChecking {

    private static final int MAX_NODES = 799699;

    // 1. Estrutura Física da Trie (Caminho = Anagrama Ordenado)
    private final char[] letraTrie = new char[MAX_NODES];
    private final int[] primeiroFilho = new int[MAX_NODES];
    private final int[] proximoIrmao = new int[MAX_NODES];
    private int proximoNoLivre = 1;

    // 2. Estrutura de Reconstrução (Links Temporais / Prefixo Anterior)
    // Usamos um pool separado para "Estados de Palavra" para suportar AMOR e ROMA no mesmo nó
    private final char[] letraOriginal = new char[MAX_NODES];
    private final int[] linkTemporal = new int[MAX_NODES];
    private final int[] listaCabecas = new int[MAX_NODES]; // Aponta o nó da Trie para o primeiro Estado
    private final int[] proximaPalavraNoNo = new int[MAX_NODES]; // Lista ligada de palavras no mesmo anagrama
    private int proximoEstadoLivre = 1;

    @Override
    public void addWords(List<String> words) {
        for (String word : words) {
            inserir(word);
        }
    }

    public List<String> search_(String query, int maxD) {
        List<String> results =  buscarPorAnagrama(query);
//        for (int i = results.size()-1;i>=0; i--) {
//            String candidate = results.get(i);
//            int d = EditDistance.distance(candidate, query);
//            if (d>maxD) results.remove(i);
//        }
        return results;
    }

    @Override
    public List<String> topKSearch(String query, int topK) {
        return new ArrayList();
    }

    @Override
    public String getName() {
        return "ATrie Ultra Compacta";
    }

    /**
     * Insere a palavra original, indexando todos os seus prefixos.
     */
    public void inserir(String palavra) {
        String original = palavra.toLowerCase();
        int idEstadoAnterior = 0; // Raiz de reconstrução

        for (int i = 1; i <= original.length(); i++) {
            String prefixoOriginal = original.substring(0, i);
            char letraAdd = original.charAt(i - 1);
            String ana = ordenar(prefixoOriginal);

            // A. Navega ou cria o nó do anagrama na Trie
            int idNoTrie = garantirCaminhoAnagrama(ana);

            // B. Cria um novo Estado de Reconstrução para este prefixo
            int idEstadoAtual = criarEstado(letraAdd, idEstadoAnterior);

            // C. Se for o fim da palavra completa, registra na cabeça do nó da Trie
            if (i == original.length()) {
                proximaPalavraNoNo[idEstadoAtual] = listaCabecas[idNoTrie];
                listaCabecas[idNoTrie] = idEstadoAtual;
            }

            idEstadoAnterior = idEstadoAtual;
        }
    }

    private int garantirCaminhoAnagrama(String ana) {
        int atual = 0;
        for (char c : ana.toCharArray()) {
            int filho = encontrarFilho(atual, c);
            if (filho == -1) {
                filho = criarNoTrie(atual, c);
            }
            atual = filho;
        }
        return atual;
    }

    private int criarNoTrie(int paiId, char c) {
        int novo = proximoNoLivre++;
        letraTrie[novo] = c;
        proximoIrmao[novo] = primeiroFilho[paiId];
        primeiroFilho[paiId] = novo;
        return novo;
    }

    private int encontrarFilho(int paiId, char c) {
        int f = primeiroFilho[paiId];
        while (f != 0) {
            if (letraTrie[f] == c) {
                return f;
            }
            f = proximoIrmao[f];
        }
        return -1;
    }

    private int criarEstado(char letra, int anterior) {
        int novo = proximoEstadoLivre++;
        letraOriginal[novo] = letra;
        linkTemporal[novo] = anterior;
        return novo;
    }

    /**
     * Busca todas as palavras que resultam no anagrama informado.
     */
    public List<String> buscarPorAnagrama(String busca) {
        String ana = ordenar(busca);
        int noId = localizarNo(ana);
        List<String> resultados = new ArrayList<>();

        if (noId != -1) {
            int idEstado = listaCabecas[noId];
            while (idEstado != 0) {
                resultados.add(reconstruir(idEstado));
                idEstado = proximaPalavraNoNo[idEstado];
            }
        }
        return resultados;
    }

    public List<String> processarResultados(String query, int noId, int maxD, List<String> resultados) {
        

        if (noId != -1) {
            int idEstado = listaCabecas[noId];
            while (idEstado != 0) {
                String word = reconstruir(idEstado);
                int d = EditDistance.distance(word, query);
                if (d<=maxD)
                    resultados.add(word);
                idEstado = proximaPalavraNoNo[idEstado];
            }
        }
        return resultados;
    }
    
    private String reconstruir(int idEstado) {
        StringBuilder sb = new StringBuilder();
        int curr = idEstado;
        while (curr != 0) {
            sb.append(letraOriginal[curr]);
            curr = linkTemporal[curr];
        }
        return sb.reverse().toString();
    }

    private int localizarNo(String ana) {
        int atual = 0;
        for (char c : ana.toCharArray()) {
            atual = encontrarFilho(atual, c);
            if (atual == -1) {
                return -1;
            }
        }
        return atual;
    }

    private String ordenar(String s) {
        char[] arr = s.toCharArray();
        Arrays.sort(arr);
        return new String(arr);
    }
    
    
    @Override
    public List<String> search(String query, int maxD) {
    // 1. Criar o pool de frequências das letras da busca
    int[] poolBusca = new int[26];
    for (char c : query.toLowerCase().toCharArray()) {
        if (c >= 'a' && c <= 'z') {
            poolBusca[c - 'a']++;
        }
    }
    
    List<String> resultados = new ArrayList();

    // 2. Disparar a recursão a partir da Raiz (ID 0)
    // Parâmetros: noId, pool, erros, maxErros, profundidadeAtual, tamanhoAlvo
    buscarComErro(0, poolBusca, 0, maxD, 0, query.length(), query, resultados);
    return resultados;
    }
    
    private void buscarComErro(int noId, int[] pool, int erros, int max, int prof, int tamAlvo, String query, List<String> resultados) {
    int filho = primeiroFilho[noId];

    while (filho != 0) {
        int idxLetra = letraTrie[filho] - 'a';
        int erroNesteNo = 0;
        boolean consumiuDoPool = false;

        // Lógica de erro:
        if (pool[idxLetra] > 0) {
            pool[idxLetra]--; // Letra existe no pool, consumimos ela
            consumiuDoPool = true;
        } else {
            erroNesteNo = 1; // Letra não existe no pool ou já esgotou
        }

        int errosTotais = erros + erroNesteNo;

        // PODA: Só continua se os erros acumulados não excederem o máximo
        if (errosTotais <= max) {
            
            // Verificação de resultado no nó terminal
            if (listaCabecas[filho] != 0) {
                // Opcional: validar se o tamanho da palavra encontrada é compatível
                // Uma palavra de tamanho N encontrada na prof P tem (N - P) letras extras
                int tamEncontrado = prof + 1;
                int diferencaTamanho = Math.abs(tamAlvo - tamEncontrado);
                
                // O erro real considera letras diferentes E diferença de tamanho
                if (errosTotais + (tamAlvo > tamEncontrado ? (tamAlvo - tamEncontrado) : 0) <= max) {
                    processarResultados(query, filho, max, resultados);
                }
            }

            // Desce para o próximo nível
            buscarComErro(filho, pool, errosTotais, max, prof + 1, tamAlvo, query, resultados);
        }

        // BACKTRACKING: Se consumiu a letra do pool, devolve antes de ir para o irmão
        if (consumiuDoPool) {
            pool[idxLetra]++;
        }

        // Move para o irmão (mesmo nível, outra letra)
        filho = proximoIrmao[filho];
    }
}

    public static void main(String[] args) {
        try {
            
//        points.add(new StringPoint("bola"));
//        points.add(new StringPoint("labo"));
//        points.add(new StringPoint("amal"));
//        points.add(new StringPoint("lama"));
//        points.add(new StringPoint("leme"));
//        points.add(new StringPoint("bala"));
//        points.add(new StringPoint("loba"));
//        points.add(new StringPoint("mele"));
//        points.add(new StringPoint("mole"));
//        points.add(new StringPoint("elam"));
//        points.add(new StringPoint("belo"));

            //bola,labo, amal,lama,leme,bala,loba,mele,mole,elam,belo
            //trie.inserir("belo");

            String dataFile = "googlebooks_cleaned.txt";
          

//            String[] array = StringLoader.loadStringArray("C:\\teste\\googlebooks_cleaned.txt", 99999,
//                    3, 49);
            
            ArrayList<StringPoint> points = StringLoader.loadStrings("C:\\teste\\googlebooks_cleaned.txt", 200,
                    3, 49, false);
            
            System.out.println("words:"+points.size());

            
            System.gc();
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage beforeHeapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
            long start = System.currentTimeMillis();
                      
            List<String> words_ = points.stream().map(word -> new String(word.word))
                .collect(Collectors.toList());
            
            
                        
            long end = System.currentTimeMillis();
            long time = end - start;
            System.out.println("time to build(ms) :" + time);
            System.gc();
            MemoryUsage afterHeapMemoryUsage = memoryMXBean.getHeapMemoryUsage();

            long usedMemoryBefore = beforeHeapMemoryUsage.getUsed();
            long usedMemoryAfter = afterHeapMemoryUsage.getUsed();
            System.out.println("Used Memory: " + ((usedMemoryAfter - usedMemoryBefore) / 1024) + " kb");
            System.out.println("");
            
  
SpellChecking trie = new LuceneNGramAutomatonWrapper(2);
            trie.addWords(words_);
            

            System.out.println("consulta");
            Random r = new Random();
            //for (int i = 0; i < points.size(); i++) {
            for (int i = 0; i < 10; i++) {
                StringPoint point = points.get(r.nextInt(points.size() - 1));
                //StringPoint point = points.get(i);
                //System.out.println("Busca " + point.word + ":" + trie.buscarPorAnagrama(point.word));

            }

            //System.out.println("Busca 'ablo': " + trie.buscar("ablo"));
            //System.out.println("Busca 'aalm': " + trie.buscar("amal"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
