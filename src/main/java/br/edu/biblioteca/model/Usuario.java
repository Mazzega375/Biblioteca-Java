package br.edu.biblioteca.model;

public class Usuario {

    public enum Tipo { ALUNO, PROFESSOR, BIBLIOTECARIO }

    private int id;
    private String nome;
    private String email;
    private Tipo tipo;
    private boolean bloqueado;

    public Usuario() {}

    public Usuario(int id, String nome, String email, Tipo tipo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
        this.bloqueado = false;
    }

    public int getId()            { return id; }
    public void setId(int v)      { this.id = v; }

    public String getNome()        { return nome; }
    public void setNome(String v)  { this.nome = v; }

    public String getEmail()        { return email; }
    public void setEmail(String v)  { this.email = v; }

    public Tipo getTipo()         { return tipo; }
    public void setTipo(Tipo v)   { this.tipo = v; }

    public boolean isBloqueado()          { return bloqueado; }
    public void setBloqueado(boolean v)   { this.bloqueado = v; }

    public String toCsv() {
        return String.join(";", String.valueOf(id), nome, email, tipo.name(), String.valueOf(bloqueado));
    }

    public static Usuario fromCsv(String linha) {
        String[] p = linha.split(";", -1);
        if (p.length < 5) throw new IllegalArgumentException("Linha CSV inválida: " + linha);
        Usuario u = new Usuario(Integer.parseInt(p[0].trim()), p[1].trim(), p[2].trim(), Tipo.valueOf(p[3].trim()));
        u.setBloqueado(Boolean.parseBoolean(p[4].trim()));
        return u;
    }

    @Override
    public String toString() {
        String status = bloqueado ? " [BLOQUEADO]" : "";
        return String.format("[%d] %s <%s> (%s)%s", id, nome, email, tipo, status);
    }
}
