package br.unisales.database.table;

import br.unisales.Enumeration.UsuarioTipoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    private Integer id;
    
    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private UsuarioTipoEnum tipo = UsuarioTipoEnum.ALUNO;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 8)
    private String senha;

    public Usuario() {
    }

    public Usuario(Integer id, String nome, UsuarioTipoEnum tipo, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.email = email;
        this.senha = senha;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public UsuarioTipoEnum getTipo() {
        return tipo;
    }

    public void setTipo(UsuarioTipoEnum tipo) {
        this.tipo = tipo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}