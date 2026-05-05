package br.unisales.structures;

/**
 * Estrutura LIFO (Last In, First Out)
 * Para histórico de operações, desfazer/refazer
 * 
 * @param <T> o tipo dos elementos armazenados
 */
public class MinhaPilha<T> {
    private class Node {
        T dado;
        Node proximo;

        Node(T dado) {
            this.dado = dado;
        }
    }

    private Node topo;
    private int tamanho;

    public MinhaPilha() {
        this.topo = null;
        this.tamanho = 0;
    }

    /**
     * Adiciona um elemento ao topo da pilha
     */
    public void push(T elemento) {
        Node novoNode = new Node(elemento);
        novoNode.proximo = topo;
        topo = novoNode;
        tamanho++;
    }

    /**
     * Remove e retorna o elemento do topo da pilha
     */
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Pilha vazia");
        }
        T dado = topo.dado;
        topo = topo.proximo;
        tamanho--;
        return dado;
    }

    /**
     * Retorna o elemento do topo sem remover
     */
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Pilha vazia");
        }
        return topo.dado;
    }

    /**
     * Verifica se a pilha está vazia
     */
    public boolean isEmpty() {
        return tamanho == 0;
    }

    /**
     * Retorna o tamanho da pilha
     */
    public int size() {
        return tamanho;
    }

    /**
     * Limpa a pilha
     */
    public void clear() {
        topo = null;
        tamanho = 0;
    }

    /**
     * Retorna uma representação em String da pilha
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node atual = topo;
        int contador = 0;
        while (atual != null && contador < 10) { // Limitado a 10 elementos para evitar listas muito longas
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
