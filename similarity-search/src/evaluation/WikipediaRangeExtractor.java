/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package evaluation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class WikipediaRangeExtractor {

    // Altere para o caminho do seu arquivo XML descompactado da Wikipedia 
    private static final String INPUT_WIKI_XML = "C:\\Users\\ferna\\Downloads\\enwiki-latest-pages-articles-multistream.xml\\enwiki-latest-pages-articles-multistream.xml";
    private static final String OUTPUT_WORD_LIST = "c:\\teste\\palavras_sujas_wikipedia_range2.txt";

    public static void main(String[] args) {
        // Exemplo: Começa a ler em 500 MB e para em 800 MB do arquivo
        long pontoInicialBytes = 0 * 1024L * 1024L;
        long pontoFinalBytes = 3000L * 1024L * 1024L;

        System.out.println("Iniciando extração controlada do Dump da Wikipedia...");
        System.out.println("Lendo intervalo de " + (pontoInicialBytes / (1024 * 1024)) + " MB até " + (pontoFinalBytes / (1024 * 1024)) + " MB.");

        long palavrasUnicasSalvas = extrairPalavrasPorIntervalo(INPUT_WIKI_XML, OUTPUT_WORD_LIST, pontoInicialBytes, pontoFinalBytes);

        System.out.println("\n--- Extração Concluída ---");
        System.out.println("Total de palavras únicas armazenadas no intervalo: " + palavrasUnicasSalvas);
    }

    public static long extrairPalavrasPorIntervalo(String inputPath, String outputPath, long startByte, long endByte) {
        Set<String> uniqueWordsRegistry = new HashSet<>();
        long totalWordsSaved = 0;
        long currentByteOffset = startByte;

        try (RandomAccessFile raf = new RandomAccessFile(inputPath, "r"); BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, StandardCharsets.UTF_8))) {

            // Salta diretamente para o ponto inicial especificado no arquivo
            raf.seek(startByte);

            // Transforma o RandomAccessFile em um leitor de linhas eficiente a partir do ponto atual
            String line;
            // Descarta a primeira linha lida caso o corte tenha acontecido no meio de uma palavra/linha
            if (startByte > 0) {
                line = raf.readLine();
                if (line != null) {
                    currentByteOffset += line.getBytes(StandardCharsets.UTF_8).length + 1; // +1 para o quebra de linha
                }
            }

            while (currentByteOffset < endByte) {
                line = raf.readLine();
                if (line == null) {
                    break; // Fim do arquivo alcançado antes do ponto final
                }

                // Codifica de volta para ler corretamente acentos em UTF-8
                String utf8Line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                currentByteOffset += line.length() + 1; // Atualiza o ponteiro do byte atual

                // Código de substituição para o bloco interno do seu laço 'while (currentByteOffset < endByte)'
// 1. Limpeza rápida de metadados comuns (Ignora linhas que contêm parâmetros de predefinição ou links externos)
                if (utf8Line.contains("|last=") || utf8Line.contains("|first=") || utf8Line.contains("|author-link=")) {
                    // Se quiser ignorar completamente metadados de autores e fontes, descomente a linha abaixo:
                    // continue; 
                }

// 2. Tokenização inteligente: Isolamos apenas letras (incluindo acentos e caracteres internacionais)
// O padrão [^a-zA-ZáàâãéèêíïóôõúçñÁÀÂÃÉÈÊÍÏÓÔÕÚÇÑ]+ quebra o texto usando qualquer símbolo, número ou pontuação como separador
                String[] tokens = utf8Line.toLowerCase().split("[^a-zA-Záàâãéèêíïóôõúçñ]+");

                for (String token : tokens) {
                    String cleanToken = token.trim();

                    // Filtros de qualidade: 
                    // - Remove palavras curtas demais resultantes de quebras de links (ex: "pt", "en", "v", "c")
                    // - Garante que não é um token vazio
                    if (cleanToken.length() > 2) {

                        if (uniqueWordsRegistry.add(cleanToken)) {
                            writer.write(cleanToken);
                            writer.newLine();
                            totalWordsSaved++;
                            
                            //System.out.println(cleanToken);

                            if (totalWordsSaved % 50000 == 0) {
                                System.out.println(" -> " + totalWordsSaved + " palavras purificadas indexadas...");
                            }
                        }
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("Arquivo de entrada não encontrado. Verifique o caminho em 'INPUT_WIKI_XML': " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro na leitura/escrita dos streams de dados: " + e.getMessage());
        }

        return totalWordsSaved;
    }
}
