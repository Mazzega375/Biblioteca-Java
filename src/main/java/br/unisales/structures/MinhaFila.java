package br.unisales.structures;

/**
 * Estrutura FIFO (First In, First Out)
 * Para controle de reservas e filas de espera
 * 
 * @param <T> o tipo dos elementos armazenados
 */
public class MinhaFila<T> {
    private class Node {
        T dado;
        Node proximo;

        Node(T dado) {
            this.dado = dado;
        }
    }

    private Node inicio;
    private Node fim;
    private int tamanho;

    public MinhaFila() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    /**
     * Adiciona um elemento ao final da fila
     */
    public void enqueue(T elemento) {
        Node novoNode = new Node(elemento);
        if (isEmpty()) {
            inicio = novoNode;
        } else {
            fim.proximo = novoNode;
        }
        fim = novoNode;
        tamanho++;
    }

    /**
     * Remove e retorna o primeiro elemento da fila
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Fila vazia");
        }
        T dado = inicio.dado;
        inicio = inicio.proximo;
        tamanho--;

        if (isEmpty()) {
            fim = null;
        }
        return dado;
    }

    /**
     * Retorna o primeiro elemento sem remover
     */
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Fila vazia");
        }
        return inicio.dado;
    }

    /**
     * Verifica se a fila está vazia
     */
    public boolean isEmpty() {
        return tamanho == 0;
    }

    /**
     * Retorna o tamanho da fila
     */
    public int size() {
        return tamanho;
    }

    /**
     * Limpa a fila
     */
    public void clear() {
        inicio = null;
        fim = null;
        tamanho = 0;
    }

    /**
     * Retorna uma representação em String da fila
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node atual = inicio;
        int contador = 0;
        while (atual != null && contador < 10) { // Limitado a 10 elementos
            sb.append(atual.dado);
            if (atual.proximo != null) {
                sb.append(", ");
            }
            atual = atual.proximo;
            contador++;
        }
        if (atual != null) {
            sb.append("...");
        }
        sb.append("]");
        return sb.toString();
    }
}
