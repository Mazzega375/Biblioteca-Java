package br.unisales.structures;

/**
 * Implementação genérica de um Vetor dinâmico (similar ao ArrayList)
 * Armazena elementos com capacidade que cresce conforme necessário
 * 
 * @param <T> o tipo dos elementos armazenados
 */
public class Vetor<T> {
    private static final int CAPACIDADE_INICIAL = 10;
    private T[] elementos;
    private int tamanho;

    @SuppressWarnings("unchecked")
    public Vetor() {
        this.elementos = (T[]) new Object[CAPACIDADE_INICIAL];
        this.tamanho = 0;
    }

    /**
     * Adiciona um elemento ao final do vetor
     */
    public void add(T elemento) {
        if (tamanho == elementos.length) {
            redimensionar();
        }
        elementos[tamanho] = elemento;
        tamanho++;
    }

    /**
     * Adiciona um elemento em uma posição específica
     */
    public void add(int indice, T elemento) {
        if (indice < 0 || indice > tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        if (tamanho == elementos.length) {
            redimensionar();
        }
        for (int i = tamanho; i > indice; i--) {
            elementos[i] = elementos[i - 1];
        }
        elementos[indice] = elemento;
        tamanho++;
    }

    /**
     * Retorna o elemento na posição especificada
     */
    public T get(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        return elementos[indice];
    }

    /**
     * Define um elemento em uma posição específica
     */
    public void set(int indice, T elemento) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        elementos[indice] = elemento;
    }

    /**
     * Remove um elemento na posição especificada
     */
    public T remove(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        T elementoRemovido = elementos[indice];
        for (int i = indice; i < tamanho - 1; i++) {
            elementos[i] = elementos[i + 1];
        }
        tamanho--;
        elementos[tamanho] = null; // Libera referência
        return elementoRemovido;
    }

    /**
     * Remove a primeira ocorrência do elemento especificado
     */
    public boolean remove(T elemento) {
        for (int i = 0; i < tamanho; i++) {
            if (elementos[i].equals(elemento)) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna o tamanho atual do vetor
     */
    public int size() {
        return tamanho;
    }

    /**
     * Verifica se o vetor está vazio
     */
    public boolean isEmpty() {
        return tamanho == 0;
    }

    /**
     * Verifica se o vetor contém o elemento especificado
     */
    public boolean contains(T elemento) {
        for (int i = 0; i < tamanho; i++) {
            if (elementos[i].equals(elemento)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Limpa o vetor removendo todos os elementos
     */
    public void clear() {
        for (int i = 0; i < tamanho; i++) {
            elementos[i] = null;
        }
        tamanho = 0;
    }

    /**
     * Retorna o índice do elemento especificado, ou -1 se não encontrado
     */
    public int indexOf(T elemento) {
        for (int i = 0; i < tamanho; i++) {
            if (elementos[i].equals(elemento)) {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private void redimensionar() {
        T[] novoArray = (T[]) new Object[(int) (elementos.length * 1.5)];
        System.arraycopy(elementos, 0, novoArray, 0, tamanho);
        elementos = novoArray;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < tamanho; i++) {
            sb.append(elementos[i]);
            if (i < tamanho - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
