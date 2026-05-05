package br.unisales.structures;

/**
 * Para armazenar estatísticas por múltiplas dimensões (ex: mês x
 * categoria)
 * 
 * @param <T> o tipo dos elementos armazenados
 */
public class Matriz<T> {
    private Object[][] dados;
    private int linhas;
    private int colunas;

    public Matriz(int linhas, int colunas) {
        if (linhas <= 0 || colunas <= 0) {
            throw new IllegalArgumentException("Dimensões da matriz devem ser positivas");
        }
        this.linhas = linhas;
        this.colunas = colunas;
        this.dados = new Object[linhas][colunas];
    }

    /**
     * Define um valor em uma posição específica
     */
    public void set(int linha, int coluna, T valor) {
        validarIndices(linha, coluna);
        dados[linha][coluna] = valor;
    }

    /**
     * Retorna o valor em uma posição específica
     */
    @SuppressWarnings("unchecked")
    public T get(int linha, int coluna) {
        validarIndices(linha, coluna);
        return (T) dados[linha][coluna];
    }

    /**
     * Retorna o número de linhas
     */
    public int getLinhas() {
        return linhas;
    }

    /**
     * Retorna o número de colunas
     */
    public int getColunas() {
        return colunas;
    }

    /**
     * Retorna uma linha completa
     */
    @SuppressWarnings("unchecked")
    public T[] getLinha(int linha) {
        if (linha < 0 || linha >= linhas) {
            throw new IndexOutOfBoundsException("Índice de linha inválido: " + linha);
        }
        Object[] resultado = new Object[colunas];
        System.arraycopy(dados[linha], 0, resultado, 0, colunas);
        return (T[]) resultado;
    }

    /**
     * Retorna uma coluna completa
     */
    @SuppressWarnings("unchecked")
    public T[] getColuna(int coluna) {
        if (coluna < 0 || coluna >= colunas) {
            throw new IndexOutOfBoundsException("Índice de coluna inválido: " + coluna);
        }
        Object[] resultado = new Object[linhas];
        for (int i = 0; i < linhas; i++) {
            resultado[i] = dados[i][coluna];
        }
        return (T[]) resultado;
    }

    /**
     * Limpa a matriz (todos os elementos recebem null)
     */
    public void clear() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                dados[i][j] = null;
            }
        }
    }

    /**
     * Valida se os índices estão dentro dos limites
     */
    private void validarIndices(int linha, int coluna) {
        if (linha < 0 || linha >= linhas || coluna < 0 || coluna >= colunas) {
            throw new IndexOutOfBoundsException(
                    String.format("Índices inválidos: [%d][%d] para matriz [%d][%d]",
                            linha, coluna, linhas, colunas));
        }
    }

    /**
     * Retorna uma representação em String da matriz
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < linhas; i++) {
            sb.append("[");
            for (int j = 0; j < colunas; j++) {
                sb.append(dados[i][j]);
                if (j < colunas - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}
