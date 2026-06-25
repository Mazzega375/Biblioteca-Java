package br.edu.biblioteca.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Representa um empréstimo de livro na biblioteca.
 */
public class Emprestimo {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final double VALOR_MULTA_DIA = 1.50;

    private int id;
    private int usuarioId;
    private String isbnLivro;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;  // null se ainda emprestado
    private boolean devolvido;

    public Emprestimo() {}

    public Emprestimo(int id, int usuarioId, String isbnLivro,
                      LocalDate dataEmprestimo, LocalDate dataPrevistaDevolucao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.isbnLivro = isbnLivro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.devolvido = false;
    }

    /** Registra a devolução com a data informada. */
    public void devolver(LocalDate data) {
        this.dataDevolucao = data;
        this.devolvido = true;
    }

    /**
     * Calcula multa por atraso. Retorna 0 se não houver atraso.
     */
    public double calcularMulta() {
        LocalDate referencia = devolvido ? dataDevolucao : LocalDate.now();
        long diasAtraso = ChronoUnit.DAYS.between(dataPrevistaDevolucao, referencia);
        return diasAtraso > 0 ? diasAtraso * VALOR_MULTA_DIA : 0.0;
    }

    public long diasAtraso() {
        LocalDate ref = devolvido ? dataDevolucao : LocalDate.now();
        long d = ChronoUnit.DAYS.between(dataPrevistaDevolucao, ref);
        return Math.max(0, d);
    }

    public boolean estaEmAtraso() {
        return diasAtraso() > 0;
    }

    // getters / setters
    public int getId()          { return id; }
    public void setId(int v)    { this.id = v; }

    public int getUsuarioId()       { return usuarioId; }
    public void setUsuarioId(int v) { this.usuarioId = v; }

    public String getIsbnLivro()        { return isbnLivro; }
    public void setIsbnLivro(String v)  { this.isbnLivro = v; }

    public LocalDate getDataEmprestimo()          { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDate v)    { this.dataEmprestimo = v; }

    public LocalDate getDataPrevistaDevolucao()       { return dataPrevistaDevolucao; }
    public void setDataPrevistaDevolucao(LocalDate v) { this.dataPrevistaDevolucao = v; }

    public LocalDate getDataDevolucao()       { return dataDevolucao; }
    public void setDataDevolucao(LocalDate v) { this.dataDevolucao = v; }

    public boolean isDevolvido()         { return devolvido; }
    public void setDevolvido(boolean v)  { this.devolvido = v; }

    /** Formato CSV: id;usuarioId;isbnLivro;dataEmprestimo;dataPrevista;dataDevolucao;devolvido */
    public String toCsv() {
        String dev = dataDevolucao != null ? dataDevolucao.format(FMT) : "";
        return String.join(";",
                String.valueOf(id),
                String.valueOf(usuarioId),
                isbnLivro,
                dataEmprestimo.format(FMT),
                dataPrevistaDevolucao.format(FMT),
                dev,
                String.valueOf(devolvido));
    }

    public static Emprestimo fromCsv(String linha) {
        String[] p = linha.split(";", -1);
        if (p.length < 7) throw new IllegalArgumentException("Linha CSV inválida: " + linha);
        Emprestimo e = new Emprestimo(
                Integer.parseInt(p[0].trim()),
                Integer.parseInt(p[1].trim()),
                p[2].trim(),
                LocalDate.parse(p[3].trim(), FMT),
                LocalDate.parse(p[4].trim(), FMT));
        if (!p[5].trim().isEmpty()) e.setDataDevolucao(LocalDate.parse(p[5].trim(), FMT));
        e.setDevolvido(Boolean.parseBoolean(p[6].trim()));
        return e;
    }

    @Override
    public String toString() {
        String status = devolvido ? "DEVOLVIDO em " + dataDevolucao : "ATIVO";
        return String.format("[Emp#%d] Usuário %d | ISBN %s | %s → %s | %s | Multa: R$%.2f",
                id, usuarioId, isbnLivro, dataEmprestimo, dataPrevistaDevolucao, status, calcularMulta());
    }
}
