package br.edu.biblioteca.ui;

import br.edu.biblioteca.repository.*;

import java.util.ArrayDeque;
import br.edu.biblioteca.action.Acao;

/**
 * Instancia os repositórios e mantém a Pilha de histórico de ações (undo).
 *
 * Estrutura de Pilha → histórico de operações (requisito do projeto).
 */
public class BibliotecaContext {

    // Repositórios (fontes de dados)
    public final LivroRepository     livroRepo     = new LivroRepository();
    public final UsuarioRepository   usuarioRepo   = new UsuarioRepository();
    public final EmprestimoRepository emprestimoRepo = new EmprestimoRepository();
    public final ReservaRepository   reservaRepo   = new ReservaRepository();
    public final ExemplarRepository  exemplarRepo  = new ExemplarRepository();

    // Pilha de histórico de ações (LIFO) — usada para Undo
    private final ArrayDeque<Acao> pilhaHistorico = new ArrayDeque<>();

    /**
     * Executa uma ação e a empilha no histórico para possível desfazer.
     */
    public void executar(Acao acao) {
        acao.executar();
        pilhaHistorico.push(acao);
    }

    /**
     * Desfaz a última ação executada (pop da pilha).
     */
    public void desfazerUltima() {
        if (pilhaHistorico.isEmpty()) {
            System.out.println("  Nenhuma ação para desfazer.");
            return;
        }
        Acao ultima = pilhaHistorico.pop();
        System.out.println("↶ Desfazendo: " + ultima.descricao());
        ultima.desfazer();
    }

    /** Retorna a descrição da última ação (sem remover da pilha). */
    public String descricaoUltimaAcao() {
        if (pilhaHistorico.isEmpty()) return "(sem histórico)";
        return pilhaHistorico.peek().descricao();
    }

    /** Número de ações no histórico. */
    public int tamanhoHistorico() {
        return pilhaHistorico.size();
    }

    /** Lista todo o histórico de ações (mais recente primeiro). */
    public void imprimirHistorico() {
        if (pilhaHistorico.isEmpty()) {
            System.out.println("  (histórico vazio)");
            return;
        }
        int i = 1;
        for (Acao a : pilhaHistorico) {
            System.out.printf("  %d. %s%n", i++, a.descricao());
        }
    }
}

