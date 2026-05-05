package br.unisales.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Para recomendações entre livros
 * 
 * @param <T> o tipo dos vértices (deve ser comparável/hashable)
 */
public class Grafo<T> {
    private Map<T, List<T>> adjacencia;
    private Set<T> vertices;

    public Grafo() {
        this.adjacencia = new HashMap<>();
        this.vertices = new HashSet<>();
    }

    /**
     * Adiciona um vértice ao grafo
     */
    public void adicionarVertice(T vertice) {
        if (!vertices.contains(vertice)) {
            vertices.add(vertice);
            adjacencia.put(vertice, new ArrayList<>());
        }
    }

    /**
     * Remove um vértice do grafo
     */
    public void removerVertice(T vertice) {
        if (vertices.contains(vertice)) {
            vertices.remove(vertice);
            adjacencia.remove(vertice);
            // Remove todas as arestas que apontam para este vértice
            for (List<T> adjacentes : adjacencia.values()) {
                adjacentes.remove(vertice);
            }
        }
    }

    /**
     * Adiciona uma aresta (conexão) entre dois vértices
     */
    public void adicionarAresta(T origem, T destino) {
        if (!vertices.contains(origem) || !vertices.contains(destino)) {
            throw new IllegalArgumentException("Um ou ambos os vértices não existem");
        }
        if (!adjacencia.get(origem).contains(destino)) {
            adjacencia.get(origem).add(destino);
        }
    }

    /**
     * Remove uma aresta entre dois vértices
     */
    public void removerAresta(T origem, T destino) {
        if (vertices.contains(origem)) {
            adjacencia.get(origem).remove(destino);
        }
    }

    /**
     * Retorna a lista de vértices adjacentes a um vértice
     */
    public List<T> getAdjacentes(T vertice) {
        if (!vertices.contains(vertice)) {
            throw new IllegalArgumentException("Vértice não existe");
        }
        return new ArrayList<>(adjacencia.get(vertice));
    }

    /**
     * Verifica se existe uma aresta entre dois vértices
     */
    public boolean ehConectado(T origem, T destino) {
        if (!vertices.contains(origem) || !vertices.contains(destino)) {
            return false;
        }
        return adjacencia.get(origem).contains(destino);
    }

    /**
     * Retorna o número de vértices
     */
    public int getNumVertices() {
        return vertices.size();
    }

    /**
     * Retorna o número de arestas
     */
    public int getNumArestas() {
        int total = 0;
        for (List<T> adjacentes : adjacencia.values()) {
            total += adjacentes.size();
        }
        return total;
    }

    /**
     * Retorna o grau de um vértice (número de adjacências)
     */
    public int getGrau(T vertice) {
        if (!vertices.contains(vertice)) {
            throw new IllegalArgumentException("Vértice não existe");
        }
        return adjacencia.get(vertice).size();
    }

    /**
     * Verifica se o grafo está vazio
     */
    public boolean isEmpty() {
        return vertices.isEmpty();
    }

    /**
     * Limpa o grafo
     */
    public void clear() {
        vertices.clear();
        adjacencia.clear();
    }

    /**
     * Retorna todos os vértices
     */
    public Set<T> getVertices() {
        return new HashSet<>(vertices);
    }

    /**
     * Busca em profundidade (DFS) a partir de um vértice
     */
    public List<T> buscaProfundidade(T inicio) {
        if (!vertices.contains(inicio)) {
            throw new IllegalArgumentException("Vértice não existe");
        }
        List<T> visitados = new ArrayList<>();
        Set<T> marcados = new HashSet<>();
        dfs(inicio, marcados, visitados);
        return visitados;
    }

    private void dfs(T vertice, Set<T> marcados, List<T> visitados) {
        marcados.add(vertice);
        visitados.add(vertice);
        for (T adjacente : adjacencia.get(vertice)) {
            if (!marcados.contains(adjacente)) {
                dfs(adjacente, marcados, visitados);
            }
        }
    }

    /**
     * Busca em largura (BFS) a partir de um vértice
     */
    public List<T> buscaLargura(T inicio) {
        if (!vertices.contains(inicio)) {
            throw new IllegalArgumentException("Vértice não existe");
        }
        List<T> visitados = new ArrayList<>();
        Set<T> marcados = new HashSet<>();
        MinhaFila<T> fila = new MinhaFila<>();
        
        fila.enqueue(inicio);
        marcados.add(inicio);
        
        while (!fila.isEmpty()) {
            T vertice = fila.dequeue();
            visitados.add(vertice);
            for (T adjacente : adjacencia.get(vertice)) {
                if (!marcados.contains(adjacente)) {
                    fila.enqueue(adjacente);
                    marcados.add(adjacente);
                }
            }
        }
        return visitados;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Grafo:\n");
        for (T vertice : vertices) {
            sb.append(vertice).append(" -> ").append(adjacencia.get(vertice)).append("\n");
        }
        return sb.toString();
    }
}
