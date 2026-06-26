package br.edu.biblioteca.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Reserva {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public enum Status { AGUARDANDO, ATENDIDA, CANCELADA }

    private int id;
    private int usuarioId;
    private String isbnLivro;
    private LocalDate dataReserva;
    private Status status;

    public Reserva() {}

    public Reserva(int id, int usuarioId, String isbnLivro, LocalDate dataReserva) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.isbnLivro = isbnLivro;
        this.dataReserva = dataReserva;
        this.status = Status.AGUARDANDO;
    }

    public int getId()         { return id; }
    public void setId(int v)   { this.id = v; }

    public int getUsuarioId()       { return usuarioId; }
    public void setUsuarioId(int v) { this.usuarioId = v; }

    public String getIsbnLivro()        { return isbnLivro; }
    public void setIsbnLivro(String v)  { this.isbnLivro = v; }

    public LocalDate getDataReserva()       { return dataReserva; }
    public void setDataReserva(LocalDate v) { this.dataReserva = v; }

    public Status getStatus()       { return status; }
    public void setStatus(Status v) { this.status = v; }

    public String toCsv() {
        return String.join(";",
                String.valueOf(id), String.valueOf(usuarioId), isbnLivro,
                dataReserva.format(FMT), status.name());
    }

    public static Reserva fromCsv(String linha) {
        String[] p = linha.split(";", -1);
        if (p.length < 5) throw new IllegalArgumentException("Linha CSV inválida: " + linha);
        Reserva r = new Reserva(Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()),
                p[2].trim(), LocalDate.parse(p[3].trim(), FMT));
        r.setStatus(Status.valueOf(p[4].trim()));
        return r;
    }

    @Override
    public String toString() {
        return String.format("[Res#%d] Usuário %d | ISBN %s | %s | %s",
                id, usuarioId, isbnLivro, dataReserva, status);
    }
}
