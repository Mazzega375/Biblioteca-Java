package br.unisales.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * Para indexar livros por ISBN ou título
 * 
 * @param <K> o tipo das chaves (deve ser Comparable)
 * @param <V> o tipo dos valores
 */
public class ArvoreBST<K extends Comparable<K>, V> {
    private class Node {
        K chave;
        V valor;
        Node esquerda;
        Node direita;

        Node(K chave, V valor) {
            this.chave = chave;
            this.valor = valor;
        }
    }

    private Node raiz;
    private int tamanho;

    public ArvoreBST() {
        this.raiz = null;
        this.tamanho = 0;
    }

    /**
     * Insere ou atualiza um par chave-valor na árvore
     */
    public V put(K chave, V valor) {
        if (chave == null) {
            throw new IllegalArgumentException("Chave não pode ser nula");
        }
        Node resultado = put(raiz, chave, valor);
        if (raiz == null) {
            tamanho++;
        }
        raiz = resultado;
        return valor;
    }

    private Node put(Node node, K chave, V valor) {
        if (node == null) {
            tamanho++;
            return new Node(chave, valor);
        }

        int cmp = chave.compareTo(node.chave);
        if (cmp < 0) {
            node.esquerda = put(node.esquerda, chave, valor);
        } else if (cmp > 0) {
            node.direita = put(node.direita, chave, valor);
        } else {
            node.valor = valor; // Atualiza valor para chave existente
        }
        return node;
    }

    /**
     * Recupera um valor pela chave
     */
    public V get(K chave) {
        if (chave == null) {
            throw new IllegalArgumentException("Chave não pode ser nula");
        }
        Node node = get(raiz, chave);
        return node == null ? null : node.valor;
    }

    private Node get(Node node, K chave) {
        if (node == null) {
            return null;
        }

        int cmp = chave.compareTo(node.chave);
        if (cmp < 0) {
            return get(node.esquerda, chave);
        } else if (cmp > 0) {
            return get(node.direita, chave);
        } else {
            return node;
        }
    }

    /**
     * Remove um elemento pela chave
     */
    public V remove(K chave) {
        if (chave == null) {
            throw new IllegalArgumentException("Chave não pode ser nula");
        }
        V valor = get(chave);
        if (valor != null) {
            raiz = remove(raiz, chave);
        }
        return valor;
    }

    private Node remove(Node node, K chave) {
        if (node == null) {
            return null;
        }

        int cmp = chave.compareTo(node.chave);
        if (cmp < 0) {
            node.esquerda = remove(node.esquerda, chave);
        } else if (cmp > 0) {
            node.direita = remove(node.direita, chave);
        } else {
            tamanho--;
            // Nó sem filhos
            if (node.esquerda == null && node.direita == null) {
                return null;
            }
            // Nó com um filho
            if (node.esquerda == null) {
                return node.direita;
            }
            if (node.direita == null) {
                return node.esquerda;
            }
            // Nó com dois filhos
            Node sucessor = encontrarMinimo(node.direita);
            node.chave = sucessor.chave;
            node.valor = sucessor.valor;
            node.direita = remove(node.direita, sucessor.chave);
            tamanho++; // Compensa o tamanho-- do remove
        }
        return node;
    }

    /**
     * Retorna o número de elementos na árvore
     */
    public int size() {
        return tamanho;
    }

    /**
     * Verifica se a árvore está vazia
     */
    public boolean isEmpty() {
        return tamanho == 0;
    }

    /**
     * Verifica se contém uma chave
     */
    public boolean containsKey(K chave) {
        return get(chave) != null;
    }

    /**
     * Limpa a árvore
     */
    public void clear() {
        raiz = null;
        tamanho = 0;
    }

    /**
     * Retorna uma lista com os pares (chave, valor) em ordem (in-order)
     */
    public List<Entrada<K, V>> inOrder() {
        List<Entrada<K, V>> resultado = new ArrayList<>();
        inOrder(raiz, resultado);
        return resultado;
    }

    private void inOrder(Node node, List<Entrada<K, V>> resultado) {
        if (node == null) {
            return;
        }
        inOrder(node.esquerda, resultado);
        resultado.add(new Entrada<>(node.chave, node.valor));
        inOrder(node.direita, resultado);
    }

    /**
     * Retorna uma lista com as chaves em ordem (in-order)
     */
    public List<K> keys() {
        List<K> resultado = new ArrayList<>();
        keys(raiz, resultado);
        return resultado;
    }

    private void keys(Node node, List<K> resultado) {
        if (node == null) {
            return;
        }
        keys(node.esquerda, resultado);
        resultado.add(node.chave);
        keys(node.direita, resultado);
    }

    /**
     * Retorna uma lista com os valores em ordem (in-order)
     */
    public List<V> values() {
        List<V> resultado = new ArrayList<>();
        values(raiz, resultado);
        return resultado;
    }

    private void values(Node node, List<V> resultado) {
        if (node == null) {
            return;
        }
        values(node.esquerda, resultado);
        resultado.add(node.valor);
        values(node.direita, resultado);
    }

    /**
     * Retorna o valor mínimo (menor chave)
     */
    public V min() {
        if (isEmpty()) {
            throw new IllegalStateException("Árvore vazia");
        }
        return encontrarMinimo(raiz).valor;
    }

    /**
     * Retorna o valor máximo (maior chave)
     */
    public V max() {
        if (isEmpty()) {
            throw new IllegalStateException("Árvore vazia");
        }
        return encontrarMaximo(raiz).valor;
    }

    private Node encontrarMinimo(Node node) {
        while (node.esquerda != null) {
            node = node.esquerda;
        }
        return node;
    }

    private Node encontrarMaximo(Node node) {
        while (node.direita != null) {
            node = node.direita;
        }
        return node;
    }

    /**
     * Retorna a altura da árvore
     */
    public int altura() {
        return altura(raiz);
    }

    private int altura(Node node) {
        if (node == null) {
            return -1;
        }
        return 1 + Math.max(altura(node.esquerda), altura(node.direita));
    }

    /**
     * Classe interna para representar uma entrada (chave-valor)
     */
    public static class Entrada<K, V> {
        public K chave;
        public V valor;

        public Entrada(K chave, V valor) {
            this.chave = chave;
            this.valor = valor;
        }

        @Override
        public String toString() {
            return chave + "=" + valor;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        List<Entrada<K, V>> entradas = inOrder();
        for (int i = 0; i < entradas.size(); i++) {
            sb.append(entradas.get(i));
            if (i < entradas.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
