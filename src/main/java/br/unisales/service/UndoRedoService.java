package br.unisales.service;

import br.unisales.structures.MinhaPilha;

/**
 * Serviço de Undo/Redo para registro de ações no sistema
 * Utiliza Pilhas para armazenar histórico de operações
 * Permite desfazer e refazer ações
 */
public class UndoRedoService {

    private static class Acao {
        String descricao;
        String tipo; // INSERT, UPDATE, DELETE, EMPRESTIMO, DEVOLUCAO, etc.
        Object dado;
        long timestamp;

        Acao(String descricao, String tipo, Object dado) {
            this.descricao = descricao;
            this.tipo = tipo;
            this.dado = dado;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("[%s] %s - %s", tipo, descricao, new java.util.Date(timestamp));
        }
    }

    private MinhaPilha<Acao> pilhaUndo;
    private MinhaPilha<Acao> pilhaRedo;
    private int limiteHistorico;

    public UndoRedoService(int limiteHistorico) {
        this.pilhaUndo = new MinhaPilha<>();
        this.pilhaRedo = new MinhaPilha<>();
        this.limiteHistorico = limiteHistorico;
    }

    public UndoRedoService() {
        this(100); // Limite padrão de 100 ações
    }

    /**
     * Registra uma ação no histórico
     * @param descricao Descrição da ação
     * @param tipo Tipo da ação (INSERT, UPDATE, DELETE, etc.)
     * @param dado Dados relacionados à ação
     */
    public void registrarAcao(String descricao, String tipo, Object dado) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição da ação não pode estar vazia");
        }

        Acao acao = new Acao(descricao, tipo != null ? tipo : "ACAO", dado);
        pilhaUndo.push(acao);

        // Limpa o histórico de redo quando uma nova ação é registrada
        pilhaRedo.clear();

        // Limita o tamanho do histórico
        if (pilhaUndo.size() > limiteHistorico) {
            // Remove as ações mais antigas (não há método de remover do final da pilha, 
            // então criamos uma nova pilha)
            MinhaPilha<Acao> novaPilha = new MinhaPilha<>();
            while (pilhaUndo.size() > limiteHistorico - 1) {
                pilhaUndo.pop();
            }
        }

        System.out.println("✓ Ação registrada: " + acao);
    }

    /**
     * Desfaz a última ação registrada
     * @return A ação desfeita, ou null se não houver ações
     */
    public Acao desfazer() {
        if (pilhaUndo.isEmpty()) {
            System.out.println("⚠ Nenhuma ação para desfazer");
            return null;
        }

        Acao acao = pilhaUndo.pop();
        pilhaRedo.push(acao);

        System.out.println("↶ Desfeito: " + acao);
        return acao;
    }

    /**
     * Refaz a última ação desfeita
     * @return A ação refeita, ou null se não houver ações
     */
    public Acao refazer() {
        if (pilhaRedo.isEmpty()) {
            System.out.println("⚠ Nenhuma ação para refazer");
            return null;
        }

        Acao acao = pilhaRedo.pop();
        pilhaUndo.push(acao);

        System.out.println("↷ Refeito: " + acao);
        return acao;
    }

    /**
     * Verifica se há ações para desfazer
     */
    public boolean podeDesfazer() {
        return !pilhaUndo.isEmpty();
    }

    /**
     * Verifica se há ações para refazer
     */
    public boolean podeRefazer() {
        return !pilhaRedo.isEmpty();
    }

    /**
     * Retorna a descrição da próxima ação a desfazer
     */
    public String proximaAcaoDesfazer() {
        if (pilhaUndo.isEmpty()) {
            return null;
        }
        return pilhaUndo.peek().descricao;
    }

    /**
     * Retorna a descrição da próxima ação a refazer
     */
    public String proximaAcaoRefazer() {
        if (pilhaRedo.isEmpty()) {
            return null;
        }
        return pilhaRedo.peek().descricao;
    }

    /**
     * Retorna o número de ações no histórico de desfazer
     */
    public int getTamanhoHistoricoUndo() {
        return pilhaUndo.size();
    }

    /**
     * Retorna o número de ações no histórico de refazer
     */
    public int getTamanhoHistoricoRedo() {
        return pilhaRedo.size();
    }

    /**
     * Limpa todo o histórico
     */
    public void limparHistorico() {
        pilhaUndo.clear();
        pilhaRedo.clear();
        System.out.println("🗑 Histórico limpo");
    }

    /**
     * Retorna o histórico completo de undo como string
     */
    public String obterHistoricoUndo() {
        if (pilhaUndo.isEmpty()) {
            return "Histórico vazio";
        }

        StringBuilder sb = new StringBuilder("Histórico de Undo:\n");
        MinhaPilha<Acao> pilhaTemp = new MinhaPilha<>();
        
        int contador = 1;
        while (!pilhaUndo.isEmpty()) {
            Acao acao = pilhaUndo.pop();
            sb.append(contador).append(". ").append(acao).append("\n");
            pilhaTemp.push(acao);
            contador++;
        }

        // Reconstrói a pilha original
        while (!pilhaTemp.isEmpty()) {
            pilhaUndo.push(pilhaTemp.pop());
        }

        return sb.toString();
    }

    /**
     * Retorna o histórico completo de redo como string
     */
    public String obterHistoricoRedo() {
        if (pilhaRedo.isEmpty()) {
            return "Histórico vazio";
        }

        StringBuilder sb = new StringBuilder("Histórico de Redo:\n");
        MinhaPilha<Acao> pilhaTemp = new MinhaPilha<>();
        
        int contador = 1;
        while (!pilhaRedo.isEmpty()) {
            Acao acao = pilhaRedo.pop();
            sb.append(contador).append(". ").append(acao).append("\n");
            pilhaTemp.push(acao);
            contador++;
        }

        // Reconstrói a pilha original
        while (!pilhaTemp.isEmpty()) {
            pilhaRedo.push(pilhaTemp.pop());
        }

        return sb.toString();
    }

    /**
     * Define o limite máximo de ações no histórico
     */
    public void setLimiteHistorico(int limite) {
        if (limite <= 0) {
            throw new IllegalArgumentException("Limite deve ser positivo");
        }
        this.limiteHistorico = limite;
    }

    /**
     * Retorna o limite máximo de ações
     */
    public int getLimiteHistorico() {
        return limiteHistorico;
    }
}
