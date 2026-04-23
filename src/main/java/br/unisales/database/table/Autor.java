package br.unisales.database.table;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "autor")
public class Autor {
    @Id
    private Integer id;

    @Column(name = "nome", nullable = false, length = 100, unique = true)
    private String nome;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LivroAutor> livroAutores = new ArrayList<>();

    public Autor() {
    }

    public Autor(Integer id, String nome, List<LivroAutor> livroAutores) {
        this.id = id;
        this.nome = nome;
        this.livroAutores = livroAutores;
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

    public List<LivroAutor> getLivroAutores() {
        return livroAutores;
    }

    public void setLivroAutores(List<LivroAutor> livroAutores) {
        this.livroAutores = livroAutores;
    }
}
