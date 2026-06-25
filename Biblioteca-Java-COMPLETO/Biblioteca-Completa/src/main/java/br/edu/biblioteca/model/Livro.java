package br.edu.biblioteca.model;

/**
 * Representa um livro no acervo da biblioteca.
 */
public class Livro {
    private String isbn;
    private String titulo;
    private String autor;
    private String editora;
    private int ano;
    private String categoria;

    public Livro() {}

    public Livro(String isbn, String titulo, String autor, String editora, int ano, String categoria) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
        this.ano = ano;
        this.categoria = categoria;
    }

    public String getIsbn()       { return isbn; }
    public void setIsbn(String v) { this.isbn = v; }

    public String getTitulo()       { return titulo; }
    public void setTitulo(String v) { this.titulo = v; }

    public String getAutor()       { return autor; }
    public void setAutor(String v) { this.autor = v; }

    public String getEditora()       { return editora; }
    public void setEditora(String v) { this.editora = v; }

    public int getAno()       { return ano; }
    public void setAno(int v) { this.ano = v; }

    public String getCategoria()       { return categoria; }
    public void setCategoria(String v) { this.categoria = v; }

    /** Formato CSV: isbn;titulo;autor;editora;ano;categoria */
    public String toCsv() {
        return String.join(";", isbn, titulo, autor, editora, String.valueOf(ano), categoria);
    }

    public static Livro fromCsv(String linha) {
        String[] p = linha.split(";", -1);
        if (p.length < 6) throw new IllegalArgumentException("Linha CSV inválida: " + linha);
        return new Livro(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(),
                Integer.parseInt(p[4].trim()), p[5].trim());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%d) | %s | %s", isbn, titulo, autor, ano, editora, categoria);
    }
}
