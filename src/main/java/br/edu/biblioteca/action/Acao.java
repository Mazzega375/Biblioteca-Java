package br.edu.biblioteca.action;

/**
 * Interface que representa uma ação reversível no sistema de biblioteca.
 * Implementa o padrão Command, permitindo executar, desfazer e descrever ações.
 */
public interface Acao {

    /**
     * Executa a ação principal (ex.: cadastrar livro, registrar empréstimo).
     */
    void executar();

    /**
     * Desfaz o efeito da ação, restaurando o estado anterior.
     */
    void desfazer();

    /**
     * Retorna uma descrição legível da ação para exibição no histórico.
     *
     * @return Descrição textual da ação.
     */
    String descricao();
}
