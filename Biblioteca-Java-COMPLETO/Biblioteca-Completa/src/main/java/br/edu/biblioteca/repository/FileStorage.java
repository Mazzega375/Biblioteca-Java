package br.edu.biblioteca.repository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitário para leitura e escrita de arquivos CSV/TXT.
 * Todos os repositórios delegam operações de I/O a esta classe.
 *
 * Estrutura dos arquivos gerados:
 *   dados/livros.csv
 *   dados/usuarios.csv
 *   dados/emprestimos.csv
 *   dados/reservas.csv
 *   dados/exemplares.csv
 */
public class FileStorage {

    private static final String DIRETORIO_BASE = "dados";

    static {
        // Garante que o diretório de dados exista ao carregar a classe
        try {
            Files.createDirectories(Paths.get(DIRETORIO_BASE));
        } catch (IOException e) {
            System.err.println("Aviso: não foi possível criar diretório 'dados': " + e.getMessage());
        }
    }

    /**
     * Retorna o caminho completo para um arquivo dentro do diretório de dados.
     *
     * @param nomeArquivo Ex.: "livros.csv"
     * @return Caminho absoluto do arquivo.
     */
    public static Path caminho(String nomeArquivo) {
        return Paths.get(DIRETORIO_BASE, nomeArquivo);
    }

    /**
     * Lê todas as linhas de um arquivo CSV, ignorando linhas vazias e comentários (#).
     *
     * @param nomeArquivo Nome do arquivo (ex.: "livros.csv").
     * @return Lista de linhas lidas.
     */
    public static List<String> lerLinhas(String nomeArquivo) {
        Path path = caminho(nomeArquivo);
        List<String> linhas = new ArrayList<>();
        if (!Files.exists(path)) return linhas;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (!linha.isEmpty() && !linha.startsWith("#")) {
                    linhas.add(linha);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo '" + nomeArquivo + "': " + e.getMessage());
        }
        return linhas;
    }

    /**
     * Escreve (substitui) todas as linhas no arquivo CSV.
     *
     * @param nomeArquivo Nome do arquivo.
     * @param linhas      Linhas a escrever.
     */
    public static void escreverLinhas(String nomeArquivo, List<String> linhas) {
        Path path = caminho(nomeArquivo);
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path.toFile(), false), StandardCharsets.UTF_8))) {
            for (String linha : linhas) {
                bw.write(linha);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever arquivo '" + nomeArquivo + "': " + e.getMessage());
        }
    }

    /**
     * Acrescenta uma linha ao final do arquivo CSV.
     *
     * @param nomeArquivo Nome do arquivo.
     * @param linha       Linha CSV a acrescentar.
     */
    public static void acrescentarLinha(String nomeArquivo, String linha) {
        Path path = caminho(nomeArquivo);
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path.toFile(), true), StandardCharsets.UTF_8))) {
            bw.write(linha);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao acrescentar linha em '" + nomeArquivo + "': " + e.getMessage());
        }
    }

    /**
     * Verifica se um arquivo de dados existe.
     */
    public static boolean existe(String nomeArquivo) {
        return Files.exists(caminho(nomeArquivo));
    }
}
