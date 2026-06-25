package br.edu.biblioteca.model;

/**
 * Representa um exemplar físico de um livro.
 */
public class Exemplar {

    public enum Status { DISPONIVEL, EMPRESTADO, RESERVADO, INATIVO }

    private int id;
    private String isbnLivro;
    private Status status;

    public Exemplar() {}

    public Exemplar(int id, String isbnLivro) {
        this.id = id;
        this.isbnLivro = isbnLivro;
        this.status = Status.DISPONIVEL;
    }

    public int getId()       { return id; }
    public void setId(int v) { this.id = v; }

    public String getIsbnLivro()       { return isbnLivro; }
    public void setIsbnLivro(String v) { this.isbnLivro = v; }

    public Status getStatus()       { return status; }
    public void setStatus(Status v) { this.status = v; }

    public boolean isDisponivel() { return status == Status.DISPONIVEL; }

    /** Formato CSV: id;isbnLivro;status */
    public String toCsv() {
        return String.join(";", String.valueOf(id), isbnLivro, status.name());
    }

    public static Exemplar fromCsv(String linha) {
        String[] p = linha.split(";", -1);
        if (p.length < 3) throw new IllegalArgumentException("Linha CSV inválida: " + linha);
        Exemplar ex = new Exemplar(Integer.parseInt(p[0].trim()), p[1].trim());
        ex.setStatus(Status.valueOf(p[2].trim()));
        return ex;
    }

    @Override
    public String toString() {
        return String.format("[Ex#%d] ISBN %s | %s", id, isbnLivro, status);
    }
}
